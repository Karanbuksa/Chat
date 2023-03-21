package server.token_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.Deserializer;

import io.jsonwebtoken.io.SerializationException;
import java.io.IOException;
import java.util.Map;

public class JsonMapDeserializer implements Deserializer<Map<String, ?>> {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public Map<String, ?> deserialize(byte[] bytes) throws SerializationException {
        try {
            return mapper.readValue(bytes, Map.class);
        } catch (IOException e) {
            throw new SerializationException("Unable to deserialize JSON map", e);
        }
    }

}
