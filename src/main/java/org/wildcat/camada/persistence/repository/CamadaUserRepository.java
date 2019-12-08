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

    List<CamadaUser> findAllByOrderByName();

    List<CamadaUser> findAllByIsActiveTrueOrderByName();

    List<CamadaUser> findAllByIsActiveFalseOrderByName();

    @Query("SELECT cu FROM CamadaUser cu WHERE cu.name IN (:names) ORDER BY cu.name")
    List<CamadaUser> findAllByName(List<String> names);
}
