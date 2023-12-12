package pt.photuseretratus.webApp.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import pt.photuseretratus.webApp.dtos.RequestToken;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public abstract class Security {

    private static final String ALGORITHM = "AES";

    public static String encrypt(String pass) throws Exception {
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] encValue = c.doFinal(pass.getBytes());
            return Base64.getEncoder().encodeToString(encValue);
    }

    public static String decrypt(String pass) throws  Exception{
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decodedValue = Base64.getDecoder().decode(pass);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
    }

    private static Key getKey(){
        String secret = System.getenv("KEY");
        byte[] secretValue = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretValue, ALGORITHM);
    }

    private static String getSignature(){
        return System.getenv("SIGN");
    }

    public static String tokenBuilder(RequestToken requestToken){
        Date exp = new Date(System.currentTimeMillis() + 1000L * 60 * 30);
        Claims claims = Jwts.claims().setSubject(requestToken.getUser());
        return Jwts.builder()
                .setClaims(claims)
                .claim("admin", requestToken.isAdmin())
                .signWith(SignatureAlgorithm.HS512, getSignature())
                .setExpiration(exp)
                .compact();
    }

    private static Claims getTokenClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSignature())
                .parseClaimsJws(token).getBody();
    }

    public static String getTokenSubject(String token){
        return getTokenClaims(token).getSubject();
    }

    public static boolean getTokenAdminStatus(String token){
        return (boolean) getTokenClaims(token).get("admin");
    }

}
