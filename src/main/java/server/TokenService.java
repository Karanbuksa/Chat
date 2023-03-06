package server;
import io.jsonwebtoken.Jwts;



public class TokenService {


    public static String generateToken(String userId) {

        String token = Jwts.builder()
                .setSubject(userId)
                .compact();

        return token;
    }
}
