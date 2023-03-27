package client.tasks;

import org.json.simple.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiConsumer;

public class SendingMessagesRunnable implements Runnable {

    private final JSONObject jsonObject;
    private final BiConsumer<String, JSONObject> setToken;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Scanner scanner;

    public SendingMessagesRunnable(BufferedReader in, PrintWriter out, JSONObject jsonObject, BiConsumer<String, JSONObject> setToken) {
        this.jsonObject = jsonObject;
        this.setToken = setToken;
        this.out = out;
        this.in = in;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        Thread receivingMessagesThread = new Thread(new ReceivingMessagesRunnable(in));
        receivingMessagesThread.start();
        String message = scanner.nextLine();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/java/client/log.txt", true))) {
            while (!Thread.currentThread().isInterrupted()) {
                if (signOut(message, receivingMessagesThread)) break;
                if (exit(message, receivingMessagesThread)) break;
                out.println(message);
                log(message, bw);
                message = scanner.nextLine();
            }
            System.out.println("Пока");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String message, BufferedWriter bw) {
        try {
            bw.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " - " + message + "\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean exit(String message, Thread receivingMessagesThread) {
        if (Objects.equals(message, "/exit")) {
            receivingMessagesThread.interrupt();
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            return true;
        }
        return false;
    }

    private boolean signOut(String message, Thread receivingMessagesThread) {
        if (Objects.equals(message, "/sign out")) {
            setToken.accept(null, jsonObject);
            receivingMessagesThread.interrupt();
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            new Thread(new EnteringChat()).start();
            return true;
        }
        return false;
    }
}
