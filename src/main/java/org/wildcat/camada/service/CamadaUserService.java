package org.wildcat.camada.service;

import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.entity.CustomQuery;

import java.util.List;
import java.util.Optional;

public interface CamadaUserService extends SavingService {
    boolean authenticate(String user, String password);
    CamadaUser getUser();
    Optional<CamadaUser> findById(Long id);
    List<CamadaUser> findAllByCustomQuery(CustomQuery customQuery);
    void save(CamadaUser camadaUser);
    void delete(CamadaUser user);
}
