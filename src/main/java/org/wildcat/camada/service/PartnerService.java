package org.wildcat.camada.service;

import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.dto.PartnerDTO;

import java.util.List;

public interface PartnerService {

    List<PartnerDTO> findAllByCustomQuery(CustomQuery customQuery);
    void delete(Long id);

}
