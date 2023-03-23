package server.tasks;

import io.jsonwebtoken.Claims;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static server.Parser.listToJson;
import static server.Parser.writeString;
import static server.token_service.TokenService.generateToken;
import static server.token_service.TokenService.readToken;

public class ProcessingConnectionRunnable implements Runnable {

    protected static BlockingQueue<PrintWriter> outs0;
    protected static BlockingQueue<BufferedReader> ins0;
    protected static ConcurrentHashMap<String, BufferedReader> ins1;
    protected static ConcurrentHashMap<String, PrintWriter> outs1;
    protected static List<User> users;

    public ProcessingConnectionRunnable(
            BlockingQueue<BufferedReader> ins0,
            BlockingQueue<PrintWriter> outs0,
            ConcurrentHashMap<String, BufferedReader> ins1,
            ConcurrentHashMap<String, PrintWriter> outs1,
            List<User> users
    ) {
        ProcessingConnectionRunnable.ins0 = ins0;
        ProcessingConnectionRunnable.outs0 = outs0;
        ProcessingConnectionRunnable.ins1 = ins1;
        ProcessingConnectionRunnable.outs1 = outs1;
        ProcessingConnectionRunnable.users = users;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                PrintWriter out = outs0.take();
                BufferedReader in = ins0.take();
                String token = in.readLine();
                List<User> userList = new ArrayList<>();
                if (!token.equals("null")) {
                    Claims claims = readToken(token);
                    userList = users.stream()
                            .filter(x -> x.getEmail().equals(claims.get("sub")))
                            .toList();
                    if (!userList.isEmpty()) {
                        out.println("valid token");
                    }
                }
                if (token.equals("null") || userList.isEmpty()) {
                    out.println("invalid token");
                    boolean f = true;
                    while (f) {
                        out.println("1. Войти      2.Зарегистрироваться     3. Выйти");
                        switch (in.readLine()) {
                            case "1" -> {
                                User user;
                                if ((user = signIn(out, in)) != null) {
                                    f = false;
                                    userList.add(user);
                                }
                            }
                            case "2" -> {
                                userList.add(signUp(out, in));
                                f = false;
                            }
                            case "3" -> {
                                out.println("/stop");
                                out.close();
                                in.close();
                            }
                        }
                    }
                }
                sendLog(out);
                ins1.put(userList.get(0).getUsername(), in);
                outs1.put(userList.get(0).getUsername(), out);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private static void sendLog(PrintWriter out) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        new BufferedReader(new FileReader("src/main/java/server/log.txt"))
                .lines()
                .forEach(x -> sb.append(x).append("\n"));
        out.println(sb);
    }

    private static User signIn(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Введите почту или логин");
        String usernameOrEmail = in.readLine();
        out.println("Введите пароль");
        String password = in.readLine();
        List<User> foundUsers = users.stream()
                .filter(x -> x.getEmail().equals(usernameOrEmail) ||
                        x.getUsername().equals(usernameOrEmail)).toList();
        if (foundUsers.isEmpty()) {
            out.println("Такой пользователь не зарегистирован. Нажмите Enter, чтобы продолжить.");
            in.readLine();
            return null;
        } else if (!Objects.equals(password, foundUsers.get(0).getPassword())) {
            out.println("Неверный пароль. Нажмите Enter, чтобы продолжить.");
            in.readLine();
            return null;
        } else {
            out.println("Добро пожаловать, " + foundUsers.get(0).getName() + ". Нажмите Enter, чтобы продолжить.");
            in.readLine();
            out.println("/exit");
            String token1 = generateToken(foundUsers.get(0).getEmail());
            out.println(token1);
            return foundUsers.get(0);
        }
    }

    private static User signUp(PrintWriter out, BufferedReader in) throws IOException {
        User user = new User();
        String nickName = getString(out, in, "ник", User::getUsername);
        String email = getString(out, in, "адрес", User::getEmail);
        out.println("Введите пароль");
        String password = in.readLine();
        long age;
        while (true) {
            out.println("Введите возраст");
            try {
                age = Long.parseLong(in.readLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        out.println("Введите имя");
        String name = in.readLine();
        out.println("Введите фамилию");
        String surname = in.readLine();
        user.setUsername(nickName);
        user.setAge(age);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setSurname(surname);
        users.add(user);
        writeString(listToJson(users), "src/main/java/server/users.json");
        out.println("Вы зарегистрированы. Нажмите Enter, чтобы продолжить.");
        in.readLine();
        out.println("/exit");
        String token1 = generateToken(user.getEmail());
        out.println(token1);
        return user;
    }

    private static String getString(PrintWriter out, BufferedReader in, String str, Function<User, String> action) throws IOException {
        String value = null;
        boolean correct = false;
        out.println("Введите " + str);
        while (!correct) {
            value = in.readLine();
            String finalValue = value;
            if (!users.stream()
                    .map(action)
                    .filter(x -> x.equals(finalValue))
                    .toList()
                    .isEmpty()) {
                out.println("Пользователь с таким " + str + "ом уже существует. Введите другой.");
            } else {
                correct = true;
            }
        }
        return value;
    }
}