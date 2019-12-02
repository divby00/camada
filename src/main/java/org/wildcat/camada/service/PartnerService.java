package org.wildcat.camada.service;

import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.dto.PartnerView;

import java.util.List;

public interface PartnerService {

    List<PartnerView> findAllByCustomQuery(CustomQuery customQuery);
    void delete(Long id);

}
