package org.wildcat.camada.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.repository.CamadaUserRepository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

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
    public Iterable<CamadaUser> findAll() {
        return camadaUserRepository.findAll();
    }

    @Override
    public void save(CamadaUser camadaUser) {
        camadaUserRepository.save(camadaUser);
    }

    @Override
    public Optional<CamadaUser> findByName(String name) {
        return camadaUserRepository.findByName(name);
    }

    public CamadaUser getUser() {
        return camadaUser;
    }

    @Override
    public void delete(CamadaUser user) {
        camadaUserRepository.delete(user);
    }

    @Override
    public <T> void saveEntity(T camadaUser) {
        save((CamadaUser) camadaUser);
    }
}
