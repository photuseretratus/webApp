package pt.photuseretratus.webApp;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class eMail {

    Properties props;
    Session session;
    String stringBody;
    MimeBodyPart body;
    Message msg;
    Multipart multipart;

    public eMail() throws MessagingException, UnsupportedEncodingException {

        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println();
                return new PasswordAuthentication("photuseretratus@gmail.com", System.getenv("PASSE_GOOGLE"));
            }
        });

        msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress("photuseretratus@gmail.com", "Formulário Site"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("photuseretratus@gmail.com"));
        body = new MimeBodyPart();
        multipart = new MimeMultipart();
        stringBody = "";

    }

    public void setRecipientCC(String CC) throws MessagingException {
        msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(CC));
    }

    public void setSubject(String subject) throws MessagingException {
        msg.setSubject(subject);
    }

    public void setBodyIntroduction(String name, String email, int phone) throws MessagingException, IOException {

        addToBody("<p><b>Nome: </b>" + name + "</p>");
        addToBody("<p><b>Email: </b>" + email + "</p>");
        addToBody("<p><b>Telemóvel: </b>" + phone + "</p>");

    }

    public void addToBody(String body) throws MessagingException, IOException {
        stringBody += body;
    }

    public void addAtachment(File file) throws MessagingException, IOException {
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

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
