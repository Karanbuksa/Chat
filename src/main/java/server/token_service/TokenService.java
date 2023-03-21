package server.token_service;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


import java.nio.charset.StandardCharsets;
import java.security.Key;


public class TokenService {
    private static final Gson gson = new Gson();
    private static final Key key = Keys.hmacShaKeyFor("50e6d9d6f8e87b3d91e9377c2a558dcb1c84ed1f647012cb7e9d8d46a5c5de5a".getBytes(StandardCharsets.UTF_8));
    public static String generateToken(String userId) {

        return Jwts.builder()
                .serializeToJsonWith(new GsonSerializer<>(gson))
                .setSubject(userId)
                .signWith(key)
                .compact();
    }
    public static Claims readToken(String jwt){
        return Jwts.parserBuilder()
                .deserializeJsonWith(new JsonMapDeserializer())
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
