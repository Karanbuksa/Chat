package server;

import server.tasks.AcceptingConnectionRunnable;
import server.tasks.ProcessingConnectionRunnable;
import server.tasks.User;
import server.tasks.chatRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static server.Parser.jsonToList;
import static server.Parser.readString;

public class Server {
    private static final BlockingQueue<BufferedReader> ins0 = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<PrintWriter> outs0 = new ArrayBlockingQueue<>(100);
    private static final ConcurrentHashMap<String, BufferedReader> ins1 = new ConcurrentHashMap<>(100);
    private static final ConcurrentHashMap<String, PrintWriter> outs1 = new ConcurrentHashMap<>(100);
    protected static List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        users.addAll(jsonToList(readString("src/main/java/server/users.json")));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                8,
                8,
                5000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100)
        );
        executor.allowCoreThreadTimeOut(true);
        ins1.put("Server", new BufferedReader(new InputStreamReader(System.in)));
        executor.submit(new AcceptingConnectionRunnable(ins0, outs0));
        for (int i = 0; i < 5; i++) {
            executor.submit(new ProcessingConnectionRunnable(ins0, outs0, ins1, outs1, users));
        }
        executor.submit(new chatRunnable(ins0, outs0, ins1, outs1));
        executor.shutdown();
    }
}
