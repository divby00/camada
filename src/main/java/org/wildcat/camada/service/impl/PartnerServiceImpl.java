package org.wildcat.camada.service.impl;

import org.springframework.stereotype.Service;
import org.wildcat.camada.common.enumerations.CamadaQuery;
import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.entity.Partner;
import org.wildcat.camada.persistence.repository.PartnerRepository;
import org.wildcat.camada.service.PartnerService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.wildcat.camada.common.enumerations.CamadaQuery.valueOf;

@Service
public class PartnerServiceImpl implements PartnerService {

    @Resource
    private final PartnerRepository partnerRepository;

    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }


    @Override
    public List<Partner> findAllByCustomQuery(CustomQuery customQuery) {
        List<Partner> partners = new ArrayList<>();
        CamadaQuery query = valueOf(customQuery.getQuery());
        switch (query) {
            case ALL_PARTNERS:
                partners = (List<Partner>) partnerRepository.findAll();
                break;
            case NEW_PARTNERS:
                partners = null;
                break;
            case ALL_FORMER_PARTNERS:
                partners = null;
                break;
            case LAST_MONTH_FORMER_PARTNERS:
                partners = null;
                break;
        }
        return partners;
    }
}
