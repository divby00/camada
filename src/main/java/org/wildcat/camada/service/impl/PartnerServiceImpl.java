package org.wildcat.camada.service.impl;

import org.springframework.stereotype.Service;
import org.wildcat.camada.common.enumerations.CamadaQuery;
import org.wildcat.camada.persistence.dto.PartnerDTO;
import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.entity.Partner;
import org.wildcat.camada.persistence.entity.Subscription;
import org.wildcat.camada.persistence.repository.PartnerRepository;
import org.wildcat.camada.service.PartnerService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.wildcat.camada.common.enumerations.CamadaQuery.valueOf;

@Service
public class PartnerServiceImpl implements PartnerService {

    @Resource
    private final PartnerRepository partnerRepository;

    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public List<PartnerDTO> findAllByCustomQuery(CustomQuery customQuery) {
        List<PartnerDTO> partners = new ArrayList<>();
        CamadaQuery query = valueOf(customQuery.getQuery());
        switch (query) {
            case ALL_PARTNERS:
                partners = getPartnersDTO();
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

    @Override
    public void delete(Long id) {
        partnerRepository.deleteById(id);
    }

    private List<PartnerDTO> getPartnersDTO() {
        List<Partner> partnerList = partnerRepository.findAll();
        List<PartnerDTO> partnersDTO = partnerRepository.findAllPartnerDtos();
        partnersDTO.forEach(partnerDTO -> {
            List<Subscription> subscriptions = partnerList.stream()
                    .filter(partner -> Objects.equals(partner.getId(), partnerDTO.getId()))
                    .findFirst()
                    .map(Partner::getSubscriptions)
                    .orElse(new LinkedList<>());
            partnerDTO.setSubscriptions(subscriptions);
        });
        return partnersDTO;
    }
}
