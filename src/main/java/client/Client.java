package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static server.Parser.readString;

public class Client {


    public static void main(String[] args) throws RuntimeException {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        try {
            Object object = jsonParser.parse(readString("src/main/java/client/settings.json"));
            jsonArray = (JSONArray) object;
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        LinkedTreeMap obj = (LinkedTreeMap) gson.fromJson(jsonArray.get(0).toString(), Object.class);
        String host = obj.get("host").toString();
        int port = ((Double) obj.get("port")).intValue();
        String token = (String) obj.get("token");

        try (Socket socket = new Socket(host, port)) {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            printWriter.print(token);
            String response = bufferedReader.readLine();
            while (!response.equals("end")) {
                System.out.println(response);
                printWriter.println(scanner.nextLine());
                response = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
