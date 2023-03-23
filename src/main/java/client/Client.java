package client;


import client.tasks.EnteringChat;


public class Client {


    public static void main(String[] args) throws RuntimeException {
       Thread firstThread = new Thread(new EnteringChat());
        firstThread.start();
        try {
            firstThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
