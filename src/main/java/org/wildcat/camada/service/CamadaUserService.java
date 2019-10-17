package org.wildcat.camada.service;

import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.entity.CustomQuery;

import java.util.Optional;
import java.util.Set;

public interface CamadaUserService extends SavingService {
    boolean authenticate(String user, String password);
    CamadaUser getUser();
    Iterable<CamadaUser> findAll();
    void save(CamadaUser camadaUser);
    void delete(CamadaUser user);
    Optional<CamadaUser> findById(Long id);
    Optional<CamadaUser> findByName(String name);
    Set<CustomQuery> getCustomQueriesByUserId(Long id);
}
