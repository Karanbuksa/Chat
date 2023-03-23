package server.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class chatRunnable implements Runnable {
    protected static final Logger logger = LogManager.getLogger(chatRunnable.class);
    protected static BlockingQueue<BufferedReader> ins0;
    protected static BlockingQueue<PrintWriter> outs0;
    protected static ConcurrentHashMap<String, BufferedReader> ins;
    protected static ConcurrentHashMap<String, PrintWriter> outs;

    public chatRunnable(
            BlockingQueue<BufferedReader> ins0,
            BlockingQueue<PrintWriter> outs0,
            ConcurrentHashMap<String, BufferedReader> ins,
            ConcurrentHashMap<String, PrintWriter> outs

    ) {
        chatRunnable.ins0 = ins0;
        chatRunnable.outs0 = outs0;
        chatRunnable.ins = ins;
        chatRunnable.outs = outs;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            ins.forEach(
                    (sendingNickName, in) -> {
                        String message;
                        try {
                            if (in.ready()) {
                                message = sendingNickName + ": " + in.readLine();
                                System.out.println(message);
                                logger.trace(message);
                                outs.forEach(
                                        (receivingNickName, printWriter) -> {
                                            if (Objects.equals(message, "/sign out")) {
                                                try {
                                                    ins0.put(in);
                                                    outs0.put(printWriter);
                                                    ins.remove(sendingNickName);
                                                    outs.remove(sendingNickName);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (!Objects.equals(receivingNickName, sendingNickName)) {
                                                printWriter.println(message);
                                            }
                                        }
                                );
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
