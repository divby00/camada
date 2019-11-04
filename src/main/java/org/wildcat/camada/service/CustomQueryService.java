package org.wildcat.camada.service;

import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.view.FxmlView;

import java.util.List;

public interface CustomQueryService {
    List<CustomQuery> findAllBySection(FxmlView view);
}
