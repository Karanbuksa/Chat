package server.token_service;

import com.google.gson.Gson;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.io.Serializer;

public class GsonSerializer<T> implements Serializer<T> {

    private final Gson gson;

    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public byte[] serialize(T t) throws IOException {
        return gson.toJson(t).getBytes();
    }

}