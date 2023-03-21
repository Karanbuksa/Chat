package client;

import client.tasks.ReceivingMessagesRunnable;
import client.tasks.SendingMessagesRunnable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.util.EnvironmentPropertySource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private static String filePath = "src/main/java/client/settings.json";

    public static void main(String[] args) throws RuntimeException {
        JSONObject jsonObj = readingSettings();
        String host = jsonObj.get("host").toString();
        int port = ((Long) jsonObj.get("port")).intValue();
        String token = (String) jsonObj.get("token");
        try {
            //noinspection resource
            Socket socket = new Socket(host, port);
            PrintWriter clientPrintWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader clientBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            String response = clientBufferedReader.readLine();
            //noinspection StatementWithEmptyBody
            while (!Objects.equals(response, "connected")) {
            }
            clientPrintWriter.println(token);
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
                }
                token = clientBufferedReader.readLine();
                readAndSetToken(token, jsonObj);
            }
            clientBufferedReader.lines();
            new Thread(new SendingMessagesRunnable(clientPrintWriter)).start();
            new Thread(new ReceivingMessagesRunnable(clientBufferedReader)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject readingSettings() {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = new JSONObject();
        EnvironmentPropertySource environmentPropertySource = new EnvironmentPropertySource();
        if (environmentPropertySource.getPropertyNames().contains("filePath")) {
            filePath = environmentPropertySource.getProperty("filePath");
        }
        try {
            jsonObj = (JSONObject) jsonParser.parse(new FileReader(filePath));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    private static void readAndSetToken(String token, JSONObject jsonObj) {
        jsonObj.put("token", token);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(jsonObj);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
