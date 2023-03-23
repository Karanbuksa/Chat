package server.tasks;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class AcceptingConnectionRunnable implements Runnable {
    protected static BlockingQueue<BufferedReader> ins;
    protected static BlockingQueue<PrintWriter> outs;

    public AcceptingConnectionRunnable(BlockingQueue<BufferedReader> ins,BlockingQueue<PrintWriter> outs) {
        AcceptingConnectionRunnable.ins = ins;
        AcceptingConnectionRunnable.outs = outs;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Socket socket = acceptConnection(checkPort());
            try {
                assert socket != null;
                OutputStream clientOutputStream = socket.getOutputStream();
                InputStream clientInputStream = socket.getInputStream();
                PrintWriter out = new PrintWriter(clientOutputStream, true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientInputStream));
                ins.put(in);
                outs.put(out);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
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

    private static Socket acceptConnection(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            Socket socket = ss.accept();
            System.out.println("New connection accepted");
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}