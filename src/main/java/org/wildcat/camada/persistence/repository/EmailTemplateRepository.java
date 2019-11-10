package org.wildcat.camada.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.persistence.entity.EmailTemplate;

import java.util.List;

@Repository
public interface EmailTemplateRepository extends CrudRepository<EmailTemplate, Long> {
    List<EmailTemplate> findAllByOrderByNameAsc();
}

