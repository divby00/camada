package org.wildcat.camada.service;

import org.wildcat.camada.persistence.entity.EmailTemplate;

import java.util.List;

public interface EmailTemplateService {
    EmailTemplate save(EmailTemplate emailTemplate);
    void delete(EmailTemplate emailTemplate);
    List<EmailTemplate> findAllByOrderByName();
}
