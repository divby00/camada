package org.wildcat.camada.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.wildcat.camada.entity.CustomQuery;
import org.wildcat.camada.enumerations.CamadaQuery;
import org.wildcat.camada.repository.CustomQueryRepository;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomQueryServiceImpl implements CustomQueryService {

    @Resource
    private final CustomQueryRepository customQueryRepository;

    public CustomQueryServiceImpl(CustomQueryRepository customQueryRepository) {
        this.customQueryRepository = customQueryRepository;
    }

    @Override
    public List<CustomQuery> findAllBySection(FxmlView view) {
        final String viewLabel = view.getLabel();
        List<String> sectionQueries = Arrays.stream(CamadaQuery.values())
                .filter(query -> StringUtils.equalsIgnoreCase(query.getSection(), (viewLabel)))
                .map(Enum::name)
                .collect(Collectors.toList());
        return customQueryRepository.findAllByQueryIn(sectionQueries);
    }
}
