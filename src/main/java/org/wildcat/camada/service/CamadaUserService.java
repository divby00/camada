package org.wildcat.camada.service;

import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.persistence.entity.CustomQuery;

import java.util.List;
import java.util.Optional;

public interface CamadaUserService extends PersistenceService<CamadaUser> {
    boolean authenticate(String user, String password);

    CamadaUser getUser();

    void setUser(CamadaUser user);

    Optional<CamadaUser> findById(Long id);

    Optional<CamadaUser> findByName(String name);

    List<CamadaUser> findAllByName(List<String> names);

    List<CamadaUser> findAllByCustomQuery(CustomQuery customQuery);

    CamadaUser save(CamadaUser camadaUser);

    void delete(CamadaUser user);
}
