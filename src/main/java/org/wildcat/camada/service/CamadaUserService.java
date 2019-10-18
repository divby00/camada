package org.wildcat.camada.service;

import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.entity.CustomQuery;

import java.util.List;

public interface CamadaUserService extends SavingService {
    boolean authenticate(String user, String password);
    CamadaUser getUser();
    List<CamadaUser> findAllByCustomQuery(CustomQuery customQuery);
    void save(CamadaUser camadaUser);
    void delete(CamadaUser user);
}
