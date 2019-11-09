package org.wildcat.camada.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.persistence.entity.CamadaUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface CamadaUserRepository extends CrudRepository<CamadaUser, Long> {

    Optional<CamadaUser> findById(Long id);

    Optional<CamadaUser> findByName(String name);

    List<CamadaUser> findAllByIsActiveTrue();

    List<CamadaUser> findAllByIsActiveFalse();

    @Query("SELECT cu FROM CamadaUser cu WHERE cu.name IN (:names)")
    List<CamadaUser> findAllByName(List<String> names);
}
