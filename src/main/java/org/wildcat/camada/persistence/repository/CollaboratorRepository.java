package org.wildcat.camada.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.persistence.entity.Collaborator;

@Repository
public interface CollaboratorRepository extends CrudRepository<Collaborator, Long> {
}
