package org.wildcat.camada.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.pojo.MailToDetails;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
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
            InternetAddress[] internetAddresses = mailToDetails.getInternetAddresses();
            String to = mailToDetails.getTo();
            if (StringUtils.isNotBlank(to)) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            } else if (internetAddresses != null) {
                message.addRecipients(Message.RecipientType.BCC, internetAddresses);
            }

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
        } catch (Throwable ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return result;
    }
}
