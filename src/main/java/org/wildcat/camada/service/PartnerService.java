package org.wildcat.camada.service;

import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.entity.Partner;

import java.util.List;

public interface PartnerService {

    List<Partner> findAllByCustomQuery(CustomQuery customQuery);

}
