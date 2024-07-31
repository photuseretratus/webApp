package pt.photuseretratus.webApp.services;

import com.google.gson.Gson;
import io.imagekit.sdk.exceptions.UnknownException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.photuseretratus.webApp.configuration.AppConfiguration;
import pt.photuseretratus.webApp.dtos.RequestToken;
import pt.photuseretratus.webApp.exceptions.AuthenticationLevelException;
import pt.photuseretratus.webApp.exceptions.SecurityLevelException;
import pt.photuseretratus.webApp.utils.Security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
@AllArgsConstructor
public class AuthenticationService {


    private Security security;
    private AppConfiguration configuration;
    private Gson gson;
    private ImageKitService imageKitService;

    public String login(final RequestToken requestToken) throws AuthenticationLevelException{

        RequestToken user;
        try {
            Path path = Path.of(configuration.getDatabase().get("path") + requestToken.getUser() + ".json");
            user = gson.fromJson(security.decrypt(Files.readString(path)), RequestToken.class);
        } catch (IOException | SecurityLevelException e) {
            throw new AuthenticationLevelException("Utilizador ou password errados",NOT_FOUND, e);
        }
        if (user.getPass().equals(requestToken.getPass())) {
            return security.tokenBuilder(user);
        } else {
            throw new AuthenticationLevelException("Utilizador ou password errados",UNAUTHORIZED);
        }
    }

    public String register(final RequestToken requestToken) {

        try {
            Path path = Path.of(configuration.getDatabase().get("path") + requestToken.getUser() + ".json");
            if (security.getTokenAdminStatus(requestToken.getToken())) {
                Files.writeString(path, security.encrypt(gson.toJson(requestToken)));
                imageKitService.createFolder(requestToken);
                return "Registado com sucesso";
            } else {
                throw new AuthenticationLevelException("Utilizador ou password errados",UNAUTHORIZED);
            }
        } catch (IOException | UnknownException | SecurityLevelException e) {
            throw new AuthenticationLevelException("Utilizador ou password errados",NOT_FOUND, e);
        }
    }

    public String validateAuthority(final RequestToken requestToken) {
        try {
            return String.valueOf(security.getTokenAdminStatus(requestToken.getToken()));
        } catch (Exception e) {
            throw new AuthenticationLevelException("Erro a validar autorização do utilizador", INTERNAL_SERVER_ERROR, e);
        }
    }

}
