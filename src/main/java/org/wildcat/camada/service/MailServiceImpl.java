package org.wildcat.camada.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wildcat.camada.exception.EmailCamadaException;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.username}")
    private String userName;

    @Value("${mail.official.name}")
    private String officialName;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.smtp.enabletls}")
    private String enableTls;

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private String port;

    @Value("${mail.protocol}")
    private String protocol;

    @Override
    public void send(MailToDetails mailToDetails) {
        String password = ""; // TODO: Add the password

        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", enableTls);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        session.setDebug(true);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailToDetails.getTo()));
            message.setSubject(mailToDetails.getSubject());
            // TODO: Identify if it's an HTML or a regular text message
            // TODO: Identify if it's attachments.
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mailToDetails.getMessage() + "<p>┌∩┐(◣_◢)┌∩┐</p>", "text/html; charset=utf-8");
            Multipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(messageBodyPart);
            BodyPart attachFilePart = new MimeBodyPart();
            attachFilePart.setDataHandler(new DataHandler(mailToDetails.getAttachment().getBytes(StandardCharsets.UTF_8), "application/octet-stream"));
            attachFilePart.setFileName(mailToDetails.getFileName());
            multiPart.addBodyPart(attachFilePart);
            message.setContent(multiPart);
            Transport transport = session.getTransport(protocol);
            transport.connect(host, officialName, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            throw new EmailCamadaException("Unable to send email", e);
        }
    }
}
