package org.wildcat.camada.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.entity.Collaborator;

@Repository
public interface CollaboratorRepository extends CrudRepository<Collaborator, Long> {
}
