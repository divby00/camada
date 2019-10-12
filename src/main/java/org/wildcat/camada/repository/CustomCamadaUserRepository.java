package org.wildcat.camada.repository;

import org.springframework.data.jpa.repository.Query;
import org.wildcat.camada.entity.CamadaUser;

import java.util.Optional;

public interface CustomCamadaUserRepository {

    @Query("SELECT c FROM CamadaUser c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<CamadaUser> findByName(String name);

}
