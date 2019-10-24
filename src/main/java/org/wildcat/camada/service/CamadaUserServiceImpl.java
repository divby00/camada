package org.wildcat.camada.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.entity.CustomQuery;
import org.wildcat.camada.enumerations.CamadaQuery;
import org.wildcat.camada.repository.CamadaUserRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.wildcat.camada.enumerations.CamadaQuery.valueOf;

@Service
public class CamadaUserServiceImpl implements CamadaUserService {

    private CamadaUser camadaUser = null;

    @Resource
    private final CamadaUserRepository camadaUserRepository;

    public CamadaUserServiceImpl(CamadaUserRepository camadaUserRepository) {
        this.camadaUserRepository = camadaUserRepository;
    }

    @Override
    public boolean authenticate(String name, String password) {
        Optional<CamadaUser> camadaUser = camadaUserRepository.findByName(name);
        camadaUser.ifPresent(user -> {
            String userInputPassword = DigestUtils.sha1Hex(password);
            if (user.getPassword().equals(userInputPassword)) {
                this.camadaUser = user;
                user.setLastConnection(new Date());
                camadaUserRepository.save(user);
            }
        });
        return this.camadaUser != null;
    }

    @Override
    public List<CamadaUser> findAllByCustomQuery(CustomQuery customQuery) {
        List<CamadaUser> camadaUsers = new ArrayList<>();
        CamadaQuery query = valueOf(customQuery.getQuery());
        switch (query) {
            case ACTIVE_USERS:
                camadaUsers = camadaUserRepository.findAllByIsActiveTrue();
                break;
            case INACTIVE_USERS:
                camadaUsers = camadaUserRepository.findAllByIsActiveFalse();
                break;
        }
        return camadaUsers;
    }

    @Override
    public CamadaUser save(CamadaUser camadaUser) {
        return camadaUserRepository.save(camadaUser);
    }

    @Override
    public void delete(CamadaUser user) {
        camadaUserRepository.delete(user);
    }

    @Override
    public <T> void saveEntity(T camadaUser) {
        save((CamadaUser) camadaUser);
    }

    public CamadaUser getUser() {
        return camadaUser;
    }

    public Optional<CamadaUser> findById(Long id) {
        return camadaUserRepository.findById(id);
    }

    @Override
    public Optional<CamadaUser> findByName(String name) {
        return camadaUserRepository.findByName(name);
    }

}
