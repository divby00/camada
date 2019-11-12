package org.wildcat.camada.service.impl;

import org.springframework.stereotype.Service;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.persistence.entity.EmailTemplate;
import org.wildcat.camada.persistence.repository.EmailTemplateRepository;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.EmailTemplateService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    @Resource
    private final EmailTemplateRepository emailTemplateRepository;

    @Resource
    private final CamadaUserService camadaUserService;

    public EmailTemplateServiceImpl(EmailTemplateRepository emailTemplateRepository, CamadaUserService camadaUserService) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.camadaUserService = camadaUserService;
    }

    public List<EmailTemplate> findAllByOrderByName() {
        CamadaUser user = camadaUserService.getUser();
        return this.emailTemplateRepository.findAllByIsPublicTrueOrUserNameEqualsOrderByNameAsc(user.getName());
    }

    public EmailTemplate save(EmailTemplate emailTemplate) {
        return this.emailTemplateRepository.save(emailTemplate);
    }

    public void delete(EmailTemplate emailTemplate) {
        this.emailTemplateRepository.delete(emailTemplate);
    }

}
