package org.wildcat.camada.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcat.camada.entity.CamadaUser;

import java.util.List;

@Repository
public interface CamadaUserRepository extends CrudRepository<CamadaUser, Long>, CustomCamadaUserRepository {
    List<CamadaUser> findAllByIsActiveTrue();
    List<CamadaUser> findAllByIsActiveFalse();
}
