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
import pt.photuseretratus.webApp.exceptions.EmailLevelException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {

    private JavaMailSender mailSender;
    private AppConfiguration appConfiguration;

    public void sendEmail(final FormLayout formLayout) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            ArrayList<File> filesToDelete = new ArrayList<>();

            helper.setFrom("WebsiteForm");
            helper.setTo("photuseretratus@gmail.com");

            StringBuilder stringBuilder = buildBodyIntroduction(formLayout.getName(), formLayout.getEmail(), formLayout.getPhone());

            switch (formLayout.getSubject()) {
                case "Orçamento":
                    helper.setSubject("Orçamento para um " + formLayout.getEventType());
                    stringBuilder
                            .append("<p><b>Tipo de evento: </b>")
                            .append(formLayout.getEventType())
                            .append(" ")
                            .append(formLayout.getPricingPlan())
                            .append("</p>");
                    break;
                case "Impressao":
                    helper.setSubject("Impressão de fotografias");
                    break;
                case "Outro":
                    helper.setSubject(formLayout.getOtherSubject());
                    break;
            }
            stringBuilder.append(formLayout.getText());
            helper.setText(stringBuilder.toString(), true);

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
        } catch (IOException | MessagingException e) {
            throw new EmailLevelException("Erro a validar autorização do utilizador", e);
        }
    }

    private StringBuilder buildBodyIntroduction(final String name, final String email, final int phone) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append("<p><b>Nome: </b>")
                .append(name)
                .append("</p>")
                .append("<p><b>Email: </b>")
                .append(email)
                .append("</p>")
                .append("<p><b>Nome: </b>")
                .append(phone)
                .append("</p>");

        return stringBuilder;
    }
}
