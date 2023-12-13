package pt.photuseretratus.webApp.apis;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.photuseretratus.webApp.services.Email;
import pt.photuseretratus.webApp.services.ImageKitService;
import pt.photuseretratus.webApp.services.Security;
import pt.photuseretratus.webApp.dtos.FormLayout;
import pt.photuseretratus.webApp.dtos.RequestToken;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class Controller {

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
            ImageKitService imageKitService = new ImageKitService();
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
            ImageKitService imageKitService = new ImageKitService();
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
            requestToken.setPath("Reportagens/" + Security.getTokenSubject(requestToken.getToken()) + "/");
            ImageKitService imageKitService = new ImageKitService();
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
            ArrayList<File> filesToDelete = new ArrayList<>();
            Email eMail = new Email();

            eMail.setRecipientCC(formLayout.getEmail());

            eMail.setBodyIntroduction(formLayout.getName(), formLayout.getEmail(), formLayout.getPhone());

            switch (formLayout.getSubject()) {
                case "Orçamento":
                    eMail.setSubject("Orçamento para um " + formLayout.getEventType());
                    eMail.addToBody("<p><b>Tipo de evento: </b>" + formLayout.getEventType() + " " + formLayout.getPricingPlan() + "</p>");
                    break;

                case "Impressao":
                    eMail.setSubject("Impressão de fotografias");
                    break;
                case "Outro":
                    eMail.setSubject(formLayout.getOtherSubject());
                    break;
            }

            eMail.addToBody("<p><b>Corpo da mensagem: </b>" + formLayout.getText() + "</p>");
            if (formLayout.getFiles() != null) {
                for (int i = 0; i < formLayout.getFiles().length; i++) {

                    ZipFile zipFile = new ZipFile("/var/www/html/photuseretratus.pt/uploads/" + formLayout.getSize() + ".zip");
                    ZipParameters zipParameters = new ZipParameters();
                    zipParameters.setFileNameInZip(formLayout.getFiles()[i].getOriginalFilename());
                    zipFile.addStream(formLayout.getFiles()[i].getInputStream(), zipParameters);
                    eMail.addAttachment(zipFile.getFile());
                    filesToDelete.add(zipFile.getFile());

                }
            }
            eMail.send();

            for (File f : filesToDelete) {
                if (!f.delete()) {
                    System.out.println("Could not delete " + f.getName());
                }
            }
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
            Path path = Path.of("/var/www/html/photuseretratus.pt/contas/" + requestToken.getUser() + ".json");
            user = gson.fromJson(Security.decrypt(Files.readString(path)), RequestToken.class);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Utilizador ou password errados", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.NOT_FOUND);
        }
        if (user.getPass().equals(requestToken.getPass())) {
            return new ResponseEntity<>(Security.tokenBuilder(user), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
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
            if (Security.getTokenAdminStatus(requestToken.getToken())) {
                Path path = Path.of("/var/www/html/photuseretratus.pt/contas/" + requestToken.getUser() + ".json");
                Files.write(path, Security.encrypt(gson.toJson(requestToken)).getBytes(StandardCharsets.UTF_8));
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
            return new ResponseEntity<>(String.valueOf(Security.getTokenAdminStatus(requestToken.getToken())), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Erro a verificar direitos de admin", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }

    }

}


