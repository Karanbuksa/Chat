package client.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReceivingMessagesRunnable implements Runnable {
    private final BufferedReader in;


    public ReceivingMessagesRunnable(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        String log = receivingLog(in);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/java/client/log.txt", true))) {
            if (!log.isEmpty()) {
                System.out.print(log);
            }
            System.out.println("Добро пожаловать в чат!\nЧтобы выйти из учётной записи введите \"/sign out\"\nЧтобы выйти из приложения введите \"/exit\"");
            String message;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (in.ready()) {
                        message = in.readLine();
                        System.out.println(message);
                        log(message, bw);
                    }
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String receivingLog(BufferedReader clientBufferedReader) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/java/client/log.txt", false))) {
            StringBuilder sb = new StringBuilder();
            while (clientBufferedReader.ready()) {
                sb.append(clientBufferedReader.readLine()).append("\n");
            }
            if (!sb.isEmpty()) {
                sb.delete(sb.length() - 1, sb.length());
            }
            String log = sb.toString();
            try {
                bw.write(log);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return log;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void log(String message, BufferedWriter bw) {
        try {
            bw.write(message + "\n");
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
