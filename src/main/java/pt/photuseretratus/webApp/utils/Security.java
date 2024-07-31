package pt.photuseretratus.webApp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pt.photuseretratus.webApp.configuration.AppConfiguration;
import pt.photuseretratus.webApp.dtos.RequestToken;
import pt.photuseretratus.webApp.exceptions.SecurityLevelException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Component
@AllArgsConstructor
@Slf4j
public class Security {

    private static final String ALGORITHM = "AES";
    private AppConfiguration appConfiguration;

    public String encrypt(String pass) throws SecurityLevelException {
        try {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, getKey());
        byte[] encValue = c.doFinal(pass.getBytes());
        return Base64.getEncoder().encodeToString(encValue);
        } catch (Exception e) {
            throw new SecurityLevelException("Problem encrypting", e);
        }
    }

    public String decrypt(String pass) throws SecurityLevelException {
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decodedValue = Base64.getDecoder().decode(pass);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (Exception e) {
            throw new SecurityLevelException("Problem decrypting", e);
        }
    }

    private Key getKey() {
        byte[] secretValue = appConfiguration.getSecurity().get("cipher").getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretValue, ALGORITHM);
    }

    private String getSignature() {
        return appConfiguration.getSecurity().get("signature");
    }

    public String tokenBuilder(RequestToken requestToken) {
        Date exp = new Date(System.currentTimeMillis() + 1000L * 60 * 30);
        Claims claims = Jwts.claims().setSubject(requestToken.getUser());
        return Jwts.builder()
                .setClaims(claims)
                .claim("admin", requestToken.isAdmin())
                .signWith(SignatureAlgorithm.HS256, getSignature())
                .setExpiration(exp)
                .compact();
    }

    private Claims getTokenClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignature())
                .parseClaimsJws(token).getBody();
    }

    public String getTokenSubject(String token) {
        return getTokenClaims(token).getSubject();
    }

    public boolean getTokenAdminStatus(String token) {
        return (boolean) getTokenClaims(token).get("admin");
    }

}
