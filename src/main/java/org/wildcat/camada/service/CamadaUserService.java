package org.wildcat.camada.service;

import org.wildcat.camada.entity.CamadaUser;

import java.util.Optional;

public interface CamadaUserService extends SavingService {
    boolean authenticate(String user, String password);
    CamadaUser getUser();
    Iterable<CamadaUser> findAll();
    void save(CamadaUser camadaUser);
    Optional<CamadaUser> findByName(String name);
    void delete(CamadaUser user);
}
