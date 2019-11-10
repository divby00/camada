package org.wildcat.camada.service.impl;

import org.springframework.stereotype.Service;
import org.wildcat.camada.persistence.entity.EmailTemplate;
import org.wildcat.camada.persistence.repository.EmailTemplateRepository;
import org.wildcat.camada.service.EmailTemplateService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    @Resource
    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplateServiceImpl(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
    }

    public List<EmailTemplate> findAllByOrderByNameAsc() {
        return (List<EmailTemplate>) this.emailTemplateRepository.findAllByOrderByNameAsc();
    }

    public EmailTemplate save(EmailTemplate emailTemplate) {
        return this.emailTemplateRepository.save(emailTemplate);
    }

}
