package pt.photuseretratus.webApp.services;

import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Component
public class Email {

    private static final String email = "photuseretratus@gmail.com";
    Properties props;
    Session session;
    String stringBody;
    MimeBodyPart body;
    Message msg;
    Multipart multipart;

    public Email() throws MessagingException, UnsupportedEncodingException {

        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println();
                return new PasswordAuthentication(email, System.getenv("PASSE_GOOGLE"));
            }
        });

        msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(email, "Formulário Site"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        body = new MimeBodyPart();
        multipart = new MimeMultipart();
        stringBody = "";

    }

    public void setRecipientCC(String CC) throws MessagingException {
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC));
    }

    public void setSubject(String subject) throws MessagingException {
        msg.setSubject(subject);
    }

    public void setBodyIntroduction(String name, String email, int phone) {

        addToBody("<p><b>Nome: </b>" + name + "</p>");
        addToBody("<p><b>Email: </b>" + email + "</p>");
        addToBody("<p><b>Telemóvel: </b>" + phone + "</p>");

    }

    public void addToBody(String body) {
        stringBody += body;
    }

    public void addAttachment(File file) throws MessagingException, IOException {
        MimeBodyPart attach = new MimeBodyPart();
        attach.attachFile(file);
        multipart.addBodyPart(attach);
    }

    public void send() throws MessagingException {
        body.setContent(stringBody, "text/html");
        multipart.addBodyPart(body);
        msg.setContent(multipart);
        Transport.send(msg);
    }
}
