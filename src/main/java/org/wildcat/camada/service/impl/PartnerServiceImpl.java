package org.wildcat.camada.service.impl;

import org.springframework.stereotype.Service;
import org.wildcat.camada.persistence.repository.PartnerRepository;
import org.wildcat.camada.service.PartnerService;

import javax.annotation.Resource;

@Service
public class PartnerServiceImpl implements PartnerService {

    @Resource
    private final PartnerRepository partnerRepository;

    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }
}
