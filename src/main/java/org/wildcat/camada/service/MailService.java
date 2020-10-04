package org.wildcat.camada.service;

import org.wildcat.camada.service.pojo.MailResponse;
import org.wildcat.camada.service.pojo.MailRequest;

import java.util.List;
import java.util.Map;

public interface MailService {
    List<MailResponse> send(MailRequest mailRequest);

    List<MailResponse> sendReplacingPlaceholders(MailRequest mailRequest, Map<String, Map<String, Object>> rowInfo);

    boolean containsPlaceholder(String message);
}
