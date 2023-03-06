package server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static server.Parser.*;
import static server.Parser.listToJson;
import static server.TokenService.generateToken;


public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final BlockingQueue<Socket> bq = new ArrayBlockingQueue<>(100);
//    private static final BlockingQueue<BufferedReader> ins = new ArrayBlockingQueue<>(100);
//    private static final BlockingQueue<PrintWriter> outs = new ArrayBlockingQueue<>(100);
    private static final List<User> users = new ArrayList<>();


    public static void main(String[] args) {
        users.addAll(jsonToList(readString("src/main/java/server/user.json")));
        List<Thread> acceptingThreads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            acceptingThreads.add(new Thread(acceptingConnections));
        }
        acceptingThreads.forEach(Thread::start);
        List<Thread> processingThreads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            processingThreads.add(new Thread(processingConnection));
            processingThreads.forEach(Thread::start);
        }
    }

    private static int checkPort() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/server/settings.txt"))) {
            String str = br.readLine();
            String[] strs = str.split(" = ");
            return Integer.parseInt(strs[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static final Runnable processingConnection = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket clientSocket = bq.take();
                try (OutputStream clientOutputStream = clientSocket.getOutputStream();
                     InputStream clientInputStream = clientSocket.getInputStream()
                ) {
                    PrintWriter out = new PrintWriter(clientOutputStream, true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientInputStream));
                    String token = in.readLine();
                    if (token == null) {
                        User user = new User();
                        boolean f = true;
                        while (f) {
                            out.println("1. Войти      2.Зарегистрироваться");
                            switch (in.readLine()) {
                                case "1" -> {
                                    if (signIn(out, in, user)) {
                                        f = false;
                                    }
                                }
                                case "2" -> {
                                    signUp(out, in, user);
                                    f = false;
                                }
                            }
                        }
                        out.print(generateToken(user.getUsername()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private static final Runnable acceptingConnections = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                bq.put(Objects.requireNonNull(acceptConnection(checkPort())));
                logger.info("New connection accepted");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private static Socket acceptConnection(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            return ss.accept();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean signIn(PrintWriter out, BufferedReader in, User found) throws IOException {
        boolean accepted = false;
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
        } else if (!Objects.equals(password, foundUsers.get(0).getPassword())) {
            out.println("Неверный пароль. Нажмите Enter, чтобы продолжить.");
            in.readLine();
        } else {
            out.println("Добро пожаловать, " + foundUsers.get(0).getName() + ". Нажмите Enter, чтобы продолжить.");
            in.readLine();
            accepted = true;
        }
        found = foundUsers.get(0);
        return accepted;
    }

    private static void signUp(PrintWriter out, BufferedReader in, User user) throws IOException {
        String nickName = getString(out, in, "ник");
        String email = getString(out, in, "адрес");
        out.println("Введите пароль");
        String password = in.readLine();
        out.println("Введите возраст");
        Long age = Long.parseLong(in.readLine());
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
        writeString(listToJson(users), "users.json");
        out.println("Вы зарегистрированы. Нажмите Enter, чтобы продолжить.");
        in.readLine();
    }

    private static String getString(PrintWriter out, BufferedReader in, String str) throws IOException {
        String value = null;
        boolean correct = false;
        while (!correct) {
            out.println("Введите " + str);
            value = in.readLine();
            String finalValue = value;
            if (!users.stream().filter(x -> x.getUsername().equals(finalValue)).toList().isEmpty()) {
                out.println("Пользователь с таким " + str + "ом уже существует");
            } else {
                correct = true;
            }
        }
        return value;
    }
}
