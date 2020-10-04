package org.wildcat.camada.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.pojo.AttachmentDetails;
import org.wildcat.camada.service.pojo.MailRequest;
import org.wildcat.camada.service.pojo.MailResponse;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<MailResponse> send(MailRequest mailRequest) {
        boolean result = false;
        StringBuilder sb = new StringBuilder();
        String emails = Stream.of(mailRequest.getInternetAddresses())
                .map(InternetAddress::getAddress)
                .collect(Collectors.joining(", "));
        try {
            Session session = getSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));
            message.setSubject(mailRequest.getSubject());
            message.setContent(getMultiPart(mailRequest));
            InternetAddress[] internetAddresses = mailRequest.getInternetAddresses();
            String to = mailRequest.getTo();
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
            sb.append(exception.getMessage());
        }
        return List.of(MailResponse.builder()
                .success(result)
                .email(emails)
                .errorMessage(sb.toString())
                .build());
    }

    @Override
    public List<MailResponse> sendReplacingPlaceholders(MailRequest mailRequest, Map<String, Map<String, Object>> rowInfo) {
        List<MailResponse> mailResponses = new ArrayList<>();
        try {
            Session session = getSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));
            message.setSubject(mailRequest.getSubject());
            Transport transport = session.getTransport(protocol);
            transport.connect(host, officialName, password);
            InternetAddress[] internetAddresses = mailRequest.getInternetAddresses();
            for (InternetAddress internetAddress : internetAddresses) {
                message.setRecipient(Message.RecipientType.TO, internetAddress);
                message.setContent(getMultiPart(internetAddress.getAddress(), mailRequest, rowInfo));
                message.saveChanges();
                mailResponses.add(sendMessage(transport, message, internetAddress.getAddress()));
            }
            transport.close();
        } catch (Throwable exception) {
            log.error(ExceptionUtils.getStackTrace(exception));
        }
        return mailResponses;
    }

    private MailResponse sendMessage(Transport transport, Message message, String email) {
        try {
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            log.info("Email sent to " + email + " successfully.");
            return MailResponse.builder()
                    .success(true)
                    .email(email)
                    .errorMessage(StringUtils.EMPTY)
                    .build();
        } catch (MessagingException exception) {
            log.error("Sending email to " + email + " failed!");
            log.error(ExceptionUtils.getStackTrace(exception));
            return MailResponse.builder()
                    .success(false)
                    .email(email)
                    .errorMessage(exception.getMessage())
                    .build();
        }
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

    private Multipart getMultiPart(MailRequest mailRequest) throws MessagingException {
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(getBodyPart(mailRequest));
        addAttachments(mailRequest, multiPart);
        return multiPart;
    }

    private Multipart getMultiPart(String email, MailRequest mailRequest, Map<String, Map<String, Object>> rowInfo) throws MessagingException {
        Multipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(getBodyPart(email, mailRequest, rowInfo));
        addAttachments(mailRequest, multiPart);
        return multiPart;
    }

    private BodyPart getBodyPart(MailRequest mailRequest) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        if (mailRequest.getIsHtml()) {
            messageBodyPart.setContent(mailRequest.getMessage(), TEXT_HTML_CHARSET_UTF_8);
        } else {
            messageBodyPart.setText(mailRequest.getMessage());
        }
        return messageBodyPart;
    }

    private BodyPart getBodyPart(String email, MailRequest mailRequest, Map<String, Map<String, Object>> rowInfo) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        String message = replacePlaceholders(email, mailRequest.getMessage(), rowInfo);
        if (mailRequest.getIsHtml()) {
            messageBodyPart.setContent(message, TEXT_HTML_CHARSET_UTF_8);
        } else {
            messageBodyPart.setText(message);
        }
        return messageBodyPart;
    }

    private void addAttachments(MailRequest mailRequest, Multipart multiPart) throws MessagingException {
        List<AttachmentDetails> attachments = mailRequest.getAttachments();
        for (AttachmentDetails attachment : attachments) {
            BodyPart attachFilePart = new MimeBodyPart();
            attachFilePart.setDataHandler(new DataHandler(attachment.getBytes(), "application/octet-stream"));
            attachFilePart.setFileName(attachment.getFilename());
            multiPart.addBodyPart(attachFilePart);
        }
    }

    private String replacePlaceholders(String email, String message, Map<String, Map<String, Object>> rowInfo) {
        Map<String, Object> row = rowInfo.get(email);
        StringBuilder sb = new StringBuilder(message);
        if (row != null) {
            row.entrySet().forEach(entry -> {
                String pattern = "{{" + entry.getKey() + "}}";
                Object value = entry.getValue();
                String replacement = (value != null) ? value.toString() : StringUtils.EMPTY;
                replaceString(sb, pattern, replacement);
            });
        } else {
            // TODO: Notify the user that placeholder replacement won't work with this email.
        }
        return sb.toString();
    }

    private void replaceString(StringBuilder sb, String toReplace, String replacement) {
        int index = -1;
        while ((index = sb.lastIndexOf(toReplace)) != -1) {
            sb.replace(index, index + toReplace.length(), replacement);
        }
    }

}
