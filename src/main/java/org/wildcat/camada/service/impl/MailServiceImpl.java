package org.wildcat.camada.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.pojo.MailToDetails;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
            Session session = getSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));
            message.setSubject(mailToDetails.getSubject());
            message.setContent(getMultiPart(mailToDetails));
            InternetAddress[] internetAddresses = mailToDetails.getInternetAddresses();
            String to = mailToDetails.getTo();
            if (StringUtils.isNotBlank(to)) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            } else if (internetAddresses != null) {
                message.addRecipients(Message.RecipientType.BCC, internetAddresses);
            }
            Transport transport = session.getTransport(protocol);
            transport.connect(host, officialName, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            result = true;
        } catch (Throwable exception) {
            log.error(ExceptionUtils.getStackTrace(exception));
        }
        return result;
    }

    @Override
    public boolean sendReplacingPlaceholders(MailToDetails mailToDetails, Map<String, Map<String, Object>> rowInfo) {
        try {
            Session session = getSession();
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(userName));
            message.setSubject(mailToDetails.getSubject());
            Transport transport = session.getTransport(protocol);
            transport.connect(host, officialName, password);
            InternetAddress[] internetAddresses = mailToDetails.getInternetAddresses();
            for (InternetAddress internetAddress : internetAddresses) {
                message.setRecipient(Message.RecipientType.TO, internetAddress);
                message.setContent(getMultiPart(internetAddress.getAddress(), mailToDetails, rowInfo));
                transport.sendMessage(message, message.getAllRecipients());
            }
            transport.close();
        } catch (Throwable exception) {
            log.error(ExceptionUtils.getStackTrace(exception));
        }
        return false;
    }

    @Override
    public boolean containsPlaceholder(String message) {
        return StringUtils.containsAny(message, "{{") && StringUtils.containsAny(message, "}}");
    }

    private Session getSession() {
        Session session = Session.getInstance(getProperties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        session.setDebug(true);
        return session;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.starttls.enable", enableTls);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        return properties;
    }

    private Multipart getMultiPart(MailToDetails mailToDetails) throws MessagingException {
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(getBodyPart(mailToDetails));
        addAttachments(mailToDetails, multiPart);
        return multiPart;
    }

    private Multipart getMultiPart(String email, MailToDetails mailToDetails, Map<String, Map<String, Object>> rowInfo) throws MessagingException {
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(getBodyPart(email, mailToDetails, rowInfo));
        addAttachments(mailToDetails, multiPart);
        return multiPart;
    }

    private BodyPart getBodyPart(MailToDetails mailToDetails) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        if (mailToDetails.getIsHtml()) {
            messageBodyPart.setContent(mailToDetails.getMessage(), TEXT_HTML_CHARSET_UTF_8);
        } else {
            messageBodyPart.setText(mailToDetails.getMessage());
        }
        return messageBodyPart;
    }

    private BodyPart getBodyPart(String email, MailToDetails mailToDetails, Map<String, Map<String, Object>> rowInfo) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        String message = replacePlaceholders(email, mailToDetails.getMessage(), rowInfo);
        if (mailToDetails.getIsHtml()) {
            messageBodyPart.setContent(message, TEXT_HTML_CHARSET_UTF_8);
        } else {
            messageBodyPart.setText(message);
        }
        return messageBodyPart;
    }

    private void addAttachments(MailToDetails mailToDetails, Multipart multiPart) throws MessagingException {
        if (StringUtils.isNotBlank(mailToDetails.getAttachment())) {
            BodyPart attachFilePart = new MimeBodyPart();
            attachFilePart.setDataHandler(new DataHandler(mailToDetails.getAttachment().getBytes(StandardCharsets.UTF_8), "application/octet-stream"));
            attachFilePart.setFileName(mailToDetails.getFileName());
            multiPart.addBodyPart(attachFilePart);
        }
    }

    private String replacePlaceholders(String email, String message, Map<String, Map<String, Object>> rowInfo) {
        Map<String, Object> row = rowInfo.get(email);
        StringBuilder sb = new StringBuilder(message);
        row.entrySet().forEach(entry -> {
            String pattern = "{{" + entry.getKey() + "}}";
            Object value = entry.getValue();
            String replacement = (value != null) ? value.toString() : StringUtils.EMPTY;
            replaceString(sb, pattern, replacement);
        });
        return sb.toString();
    }

    private void replaceString(StringBuilder sb, String toReplace, String replacement) {
        int index = -1;
        while ((index = sb.lastIndexOf(toReplace)) != -1) {
            sb.replace(index, index + toReplace.length(), replacement);
        }
    }

}
