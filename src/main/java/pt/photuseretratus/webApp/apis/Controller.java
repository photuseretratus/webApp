package pt.photuseretratus.webApp.apis;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.photuseretratus.webApp.configuration.AppConfiguration;
import pt.photuseretratus.webApp.dtos.FormLayout;
import pt.photuseretratus.webApp.dtos.RequestToken;
import pt.photuseretratus.webApp.services.EmailService;
import pt.photuseretratus.webApp.services.ImageKitService;
import pt.photuseretratus.webApp.services.Security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class Controller {

    private ImageKitService imageKitService;
    private EmailService email;
    private AppConfiguration configuration;
    private Security security;

    private HttpHeaders getHttpHeader(MediaType mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        return httpHeaders;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/getUrl",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUrl(@RequestBody RequestToken requestToken) {
        try {
            return new ResponseEntity<>(imageKitService.getURL(requestToken), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Error", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/getFolder",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getFolder(@RequestBody RequestToken requestToken) {
        try {
            return new ResponseEntity<>(imageKitService.getFolder(requestToken), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(new ArrayList<>(), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/getUserFolders",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getUserFolders(@RequestBody RequestToken requestToken) {
        try {
            requestToken.setPath("Reportagens/" + security.getTokenSubject(requestToken.getToken()) + "/");
            return new ResponseEntity<>(imageKitService.getFoldersNames(requestToken), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(new ArrayList<>(), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/contact",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Contact(@ModelAttribute FormLayout formLayout) {

        try {
            email.sendEmail(formLayout);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Erro a submeter por favor tente novamente", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Enviado com sucesso", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/login",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Login(@ModelAttribute RequestToken requestToken) {
        RequestToken user;
        Gson gson = new Gson();
        try {
            Path path = Path.of(configuration.getDatabase().get("path") + requestToken.getUser() + ".json");
            user = gson.fromJson(security.decrypt(Files.readString(path)), RequestToken.class);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Utilizador ou password errados", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.NOT_FOUND);
        }
        if (user.getPass().equals(requestToken.getPass())) {
            return new ResponseEntity<>(security.tokenBuilder(user), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Utilizador ou password errados", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.UNAUTHORIZED);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/register",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Register(@ModelAttribute RequestToken requestToken) {
        Gson gson = new Gson();
        try {
            Path path = Path.of(configuration.getDatabase().get("path") + requestToken.getUser() + ".json");
            Files.writeString(path, security.encrypt(gson.toJson(requestToken)));
            if (security.getTokenAdminStatus(requestToken.getToken())) {
                //path = Path.of(configuration.getDatabase().get("path") + requestToken.getUser() + ".json");
                Files.writeString(path, security.encrypt(gson.toJson(requestToken)));
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("sh", "/var/www/html/photuseretratus.pt/createFolder.sh", System.getenv("IMAGEKITIOPRIV"), requestToken.getUser()).start();
                return new ResponseEntity<>("Registado com sucesso", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Ação ilegal", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Erro a registar", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/verifyAdmin",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> VerifyAdmin(@RequestBody RequestToken requestToken) {

        try {
            return new ResponseEntity<>(String.valueOf(security.getTokenAdminStatus(requestToken.getToken())), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Erro a verificar direitos de admin", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }

    }

}


