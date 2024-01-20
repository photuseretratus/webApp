package pt.photuseretratus.webApp.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pt.photuseretratus.webApp.configuration.AppConfiguration;
import pt.photuseretratus.webApp.dtos.FormLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {

    private JavaMailSender mailSender;
    private AppConfiguration appConfiguration;

    public void sendEmail(final FormLayout formLayout) throws MessagingException, IOException {


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        ArrayList<File> filesToDelete = new ArrayList<>();

        helper.setFrom("WebsiteForm");
        helper.setTo("diogoah99@gmail.com");
        helper.setText(buildBodyIntroduction(formLayout.getName(), formLayout.getEmail(), formLayout.getPhone()), true);

        switch (formLayout.getSubject()) {
            case "Orçamento":
                helper.setSubject("Orçamento para um " + formLayout.getEventType());
                helper.setText("<p><b>Tipo de evento: </b>" + formLayout.getEventType() + " " + formLayout.getPricingPlan() + "</p>", true);
                break;
            case "Impressao":
                helper.setSubject("Impressão de fotografias");
                break;
            case "Outro":
                helper.setSubject(formLayout.getOtherSubject());
                break;
        }

        if (formLayout.getFiles() != null) {
            for (int i = 0; i < formLayout.getFiles().length; i++) {

                ZipFile zipFile = new ZipFile(appConfiguration.getDatabase().get("uploadPath") + formLayout.getName() + ".zip");
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setFileNameInZip(formLayout.getFiles()[i].getOriginalFilename());
                zipFile.addStream(formLayout.getFiles()[i].getInputStream(), zipParameters);
                helper.addAttachment(formLayout.getName(), zipFile.getFile());
                filesToDelete.add(zipFile.getFile());
            }
        }
        mailSender.send(message);

        for (File f : filesToDelete) {
            if (!f.delete()) {
                log.error("Could not delete " + f.getName());
            }
        }
    }

    private String buildBodyIntroduction(final String name, final String email, final int phone) {
        String body = "";

        body += "<p><b>Nome: </b>" + name + "</p>";
        body += "<p><b>Email: </b>" + email + "</p>";
        body += "<p><b>Telemóvel: </b>" + phone + "</p>";

        return body;
    }
}
