package client.tasks;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceivingMessagesRunnable implements Runnable {
    private final BufferedReader in;

    public ReceivingMessagesRunnable(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        System.out.println("Добро пожаловать в чат!");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                System.out.println(in.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
