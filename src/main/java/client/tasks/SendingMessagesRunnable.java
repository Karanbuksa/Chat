package client.tasks;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.Scanner;

public class SendingMessagesRunnable implements Runnable {

    private final PrintWriter out;
    Scanner scanner;

    public SendingMessagesRunnable(PrintWriter out) {
        this.out = out;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        String message = scanner.nextLine();
        while (!Thread.currentThread().isInterrupted() || Objects.equals(message, "/exit")) {
            out.println(message);
            message = scanner.nextLine();
        }
    }
}
