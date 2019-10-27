package org.wildcat.camada.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html; charset=utf-8";

    @Value("${mail.username}")
    private String userName;

    @Value("${mail.password}")
    private String password;

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
    public boolean send(MailToDetails mailToDetails) {
        boolean result = false;
        try {
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
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailToDetails.getTo()));
            message.setSubject(mailToDetails.getSubject());
            BodyPart messageBodyPart = new MimeBodyPart();
            if (mailToDetails.getIsHtml()) {
                messageBodyPart.setContent(mailToDetails.getMessage(), TEXT_HTML_CHARSET_UTF_8);
            } else {
                messageBodyPart.setText(mailToDetails.getMessage());
            }
            Multipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(messageBodyPart);
            if (StringUtils.isNotBlank(mailToDetails.getAttachment())) {
                BodyPart attachFilePart = new MimeBodyPart();
                attachFilePart.setDataHandler(new DataHandler(mailToDetails.getAttachment().getBytes(StandardCharsets.UTF_8), "application/octet-stream"));
                attachFilePart.setFileName(mailToDetails.getFileName());
                multiPart.addBodyPart(attachFilePart);
            }
            message.setContent(multiPart);
            Transport transport = session.getTransport(protocol);
            transport.connect(host, officialName, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            result = true;
        } catch (Throwable ignored) {
        }
        return result;
    }
}
