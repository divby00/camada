package org.wildcat.camada.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.persistence.entity.EmailTemplate;

import java.util.List;

@Repository
public interface EmailTemplateRepository extends CrudRepository<EmailTemplate, Long> {
    List<EmailTemplate> findAllByIsPublicTrueOrUserNameEqualsOrderByNameAsc(String userName);
    @Query(value = "SELECT * FROM email_template et where et.user_name = :userName OR et.is_public = true ORDER BY et.name ASC", nativeQuery = true)
    List<EmailTemplate> findEmailTemplates(@Param("userName") String userName);
}

