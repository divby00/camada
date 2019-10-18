package org.wildcat.camada.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.entity.CamadaUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface CamadaUserRepository extends CrudRepository<CamadaUser, Long> {
    Optional<CamadaUser> findByName(String name);
    Optional<CamadaUser> findById(Long id);
    List<CamadaUser> findAllByIsActiveTrue();
    List<CamadaUser> findAllByIsActiveFalse();
}
