package org.wildcat.camada.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.persistence.entity.PersonalData;

@Repository
public interface PersonalDataRepository extends CrudRepository<PersonalData, Long> {
}
