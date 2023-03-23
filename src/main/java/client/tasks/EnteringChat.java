package client.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class EnteringChat implements Runnable {

    @Override
    public void run() {
        JSONObject jsonObj = readingSettings();
        String host = jsonObj.get("host").toString();
        int port = ((Long) jsonObj.get("port")).intValue();
        String token = (String) jsonObj.get("token");
        try {
            Socket socket = new Socket(host, port);
            PrintWriter clientPrintWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader clientBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            clientPrintWriter.println(token);
            processingConnection(clientPrintWriter, clientBufferedReader, scanner);
            if (!Thread.currentThread().isInterrupted()) {
                token = clientBufferedReader.readLine();
                setToken(token, jsonObj);
                new Thread(new SendingMessagesRunnable(clientBufferedReader, clientPrintWriter, jsonObj, EnteringChat::setToken)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void processingConnection(PrintWriter clientPrintWriter, BufferedReader clientBufferedReader, Scanner scanner) throws IOException {
        String response;
        String message = null;
        int i = 0;
        if (Objects.equals(response = clientBufferedReader.readLine(), "invalid token")) {
            while (!Objects.equals(response, "/exit") && !Objects.equals(message, "/exit")) {
                if (i == 0) {
                    i++;
                    response = clientBufferedReader.readLine();
                    continue;
                }
                System.out.println(response);
                clientPrintWriter.println(message = scanner.nextLine());
                response = clientBufferedReader.readLine();
                if (Objects.equals(message, "/exit")) {
                    clientBufferedReader.close();
                    clientPrintWriter.close();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static JSONObject readingSettings() {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = new JSONObject();
        String filePath = "src/main/java/client/settings.json";
        if (System.getProperty("settingsFilePath") != null) {
            filePath = System.getProperty("settingsFilePath");
        }
        try {
            jsonObj = (JSONObject) jsonParser.parse(new FileReader(filePath));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    private static void setToken(String token, JSONObject jsonObj) {
        jsonObj.put("token", token);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(jsonObj);
        try (FileWriter writer = new FileWriter(System.getProperty("settingsFilePath"))) {
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
