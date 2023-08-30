package pt.photuseretratus.webApp;

import com.google.gson.Gson;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
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
            ImageKitIO imageKitIO = new ImageKitIO();
            return new ResponseEntity<String>(imageKitIO.getURL(requestToken), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Error", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/getFolder",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getFolder(@RequestBody RequestToken requestToken) {
        try {
            ImageKitIO imageKitIO = new ImageKitIO();
            return new ResponseEntity<List<String>>(imageKitIO.getFolder(requestToken), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<List<String>>(new ArrayList<String>(), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/getUserFolders",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getUserFolders(@RequestBody RequestToken requestToken) throws IOException {
        try {
            requestToken.setPath("Reportagens/" + Security.getTokenSubject(requestToken.getToken()) + "/");
            ImageKitIO imageKitIO = new ImageKitIO();
            return new ResponseEntity<List<String>>(imageKitIO.getFoldersNames(requestToken), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<List<String>>(new ArrayList<String>(), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/contact",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Contact(@ModelAttribute FormLayout formLayout) {

        try {
            ArrayList<File> filesToDelete = new ArrayList<File>();
            eMail eMail = new eMail();

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
            if (formLayout.getFicheiro() != null) {
                for (int i = 0; i < formLayout.getFicheiro().length; i++) {

                    ZipFile zipFile = new ZipFile("/var/www/html/photuseretratus.pt/uploads/" + formLayout.getSize() + ".zip");
                    ZipParameters zipParameters = new ZipParameters();
                    zipParameters.setFileNameInZip(formLayout.getFicheiro()[i].getOriginalFilename());
                    zipFile.addStream(formLayout.getFicheiro()[i].getInputStream(), zipParameters);
                    eMail.addAtachment(zipFile.getFile());
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
            e.printStackTrace();
            return new ResponseEntity<String>("Erro a submeter por favor tente novamente", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>("Enviado com sucesso", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
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
            e.printStackTrace();
            return new ResponseEntity<String>("Utilizador ou password errados", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.NOT_FOUND);
        }
        if (user.getPass().equals(requestToken.getPass())) {
            return new ResponseEntity<String>(Security.tokenBuilder(user), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Utilizador ou password errados", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.UNAUTHORIZED);
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
                return new ResponseEntity<String>("Registado com sucesso", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("Ação ilegal", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Erro a registar", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/verifyAdmin",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> VerifyAdmin(@RequestBody RequestToken requestToken) {

        try {
            return new ResponseEntity<String>(String.valueOf(Security.getTokenAdminStatus(requestToken.getToken())), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Erro a verificar direitos de admin", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.BAD_REQUEST);
        }

    }

}


