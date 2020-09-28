package org.wildcat.camada.service;

import org.wildcat.camada.service.pojo.MailToDetails;

import java.util.Map;

public interface MailService {
    boolean send(MailToDetails mailToDetails);

    boolean sendReplacingPlaceholders(MailToDetails mailToDetails, Map<String, Map<String, Object>> rowInfo);

    boolean containsPlaceholder(String message);
}
