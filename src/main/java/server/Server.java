package server;



import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static server.Parser.jsonToList;
import static server.Parser.readString;


public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final BlockingQueue<Socket> bq = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<BufferedReader> ins = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<PrintWriter> outs = new ArrayBlockingQueue<>(100);
    private static final List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);
            HttpContext context = server.createContext("src/main/java/server");
            context.setHandler(new HttpHandler() { 
                @Override
                public void handle(HttpExchange exchange) throws IOException {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.addAll(jsonToList(readString("src/main/java/server/user.json")));

        Thread acceptingClientsThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    bq.put(Objects.requireNonNull(acceptConnection(checkPort())));
                    logger.info("New connection accepted");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread processingConnectionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = bq.take();
                    try (OutputStream clientOutputStream = clientSocket.getOutputStream();
                         InputStream clientInputStream = clientSocket.getInputStream()
                    ) {
                        PrintWriter out = new PrintWriter(clientOutputStream, true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientInputStream));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private static int checkPort() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/server/settings.txt"))) {
            return Integer.parseInt(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

    }

    private static Socket acceptConnection(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            return ss.accept();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
