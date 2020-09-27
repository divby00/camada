package org.wildcat.camada.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.wildcat.camada.common.enumerations.CamadaQuery;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.repository.CamadaUserRepository;
import org.wildcat.camada.service.CamadaUserService;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.wildcat.camada.common.enumerations.CamadaQuery.valueOf;

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
            String temporaryPassword = user.getTmpPassword();
            String userInputPassword = DigestUtils.sha1Hex(password);
            Date tomorrow = user.getTmpPasswordExpiration();

            if (StringUtils.isNotBlank(temporaryPassword) && tomorrow != null) {
                Instant instant = tomorrow.toInstant();
                ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
                LocalDateTime tomorrowLocalDate = zdt.toLocalDateTime();

                if (LocalDateTime.now().isBefore(tomorrowLocalDate)) {
                    if (user.getTmpPassword().equals(userInputPassword)) {
                        user.setTmpPassword(null);
                        user.setTmpPasswordExpiration(null);
                        user.setPassword(temporaryPassword);
                        user.setLastConnection(new Date());
                        this.camadaUser = user;
                        save(user);
                    }
                } else {
                    user.setTmpPassword(null);
                    user.setTmpPasswordExpiration(null);
                    save(user);
                }
            }

            if (user.getPassword().equals(userInputPassword)) {
                user.setLastConnection(new Date());
                this.camadaUser = user;
                save(user);
            }
        });
        return this.camadaUser != null;
    }

    @Override
    public List<CamadaUser> findAllByCustomQuery(CustomQuery customQuery) {
        List<CamadaUser> camadaUsers = new ArrayList<>();
        CamadaQuery query = valueOf(customQuery.getQuery());
        switch (query) {
            case ACTIVE_AND_INACTIVE_USERS:
                camadaUsers = camadaUserRepository.findAllByOrderByName();
                break;
            case ACTIVE_USERS:
                camadaUsers = camadaUserRepository.findAllByIsActiveTrueOrderByName();
                break;
            case INACTIVE_USERS:
                camadaUsers = camadaUserRepository.findAllByIsActiveFalseOrderByName();
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
    public void saveEntity(CamadaUser camadaUser) {
        save(camadaUser);
    }

    @Override
    public CamadaUser find(Long id) {
        return camadaUserRepository.findById(id).orElse(null);
    }

    @Override
    public void setUser(CamadaUser user) {
        this.camadaUser = user;
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

    @Override
    public Optional<CamadaUser> findByEmail(String email) {
        return camadaUserRepository.findByEmail(email);
    }

    @Override
    public List<CamadaUser> findAllByName(List<String> names) {
        return camadaUserRepository.findAllByName(names);
    }

}
