package org.wildcat.camada.service;

import org.wildcat.camada.persistence.dto.PartnerDTO;
import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.entity.Partner;

import java.util.List;

public interface PartnerService extends PersistenceService<Partner> {

    List<PartnerDTO> findAllByCustomQuery(CustomQuery customQuery);

    void delete(Long id);

    Partner save(Partner partner);

}
