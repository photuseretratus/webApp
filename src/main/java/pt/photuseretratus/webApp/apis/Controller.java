package pt.photuseretratus.webApp.apis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.photuseretratus.webApp.dtos.FormLayout;
import pt.photuseretratus.webApp.dtos.RequestToken;
import pt.photuseretratus.webApp.services.AuthenticationService;
import pt.photuseretratus.webApp.services.EmailService;
import pt.photuseretratus.webApp.services.ImageKitService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class Controller {

    private ImageKitService imageKitService;
    private EmailService email;
    private AuthenticationService authenticationService;

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

        return new ResponseEntity<>(imageKitService.getURL(requestToken), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/getFolder",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getFolder(@RequestBody RequestToken requestToken) {

        return new ResponseEntity<>(imageKitService.getFolder(requestToken), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/getUserFolders",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getUserFolders(@RequestBody RequestToken requestToken) {

        return new ResponseEntity<>(imageKitService.getFoldersNames(requestToken), getHttpHeader(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/contact",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Contact(@ModelAttribute FormLayout formLayout) {

        email.sendEmail(formLayout);
        return new ResponseEntity<>("Enviado com sucesso", getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/login",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Login(@ModelAttribute RequestToken requestToken) {

        return new ResponseEntity<>(authenticationService.login(requestToken), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/register",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Register(@ModelAttribute RequestToken requestToken) {

        return new ResponseEntity<>(authenticationService.register(requestToken), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(
            path = "/imagekitio/verifyAdmin",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> VerifyAdmin(@RequestBody RequestToken requestToken) {

        return new ResponseEntity<>(authenticationService.validateAuthority(requestToken), getHttpHeader(MediaType.TEXT_PLAIN), HttpStatus.OK);

    }

}


