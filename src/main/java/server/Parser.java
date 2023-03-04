package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static List<User> jsonToList(String fileName) {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        List<User> objList = new ArrayList<>();
        try {
            Object object = jsonParser.parse(fileName);
            jsonArray = (JSONArray) object;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Object object : jsonArray) {
            User user = gson.fromJson(object.toString(), User.class);
            objList.add(user);
        }
        return objList;
    }

    public static String listToJson(List<User> list) {
        Type listType = new TypeToken<List<User>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(list, listType);
    }
    public static String objToJson(User obj) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(obj, obj.getClass());
    }
    public static void writeString(String string, String name) {
        try (FileWriter fileWriter = new FileWriter(name)) {
            fileWriter.write(string);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readString(String fileName) throws RuntimeException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                stringBuilder.append(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
