package org.wildcat.camada.service;

import org.springframework.stereotype.Service;
import org.wildcat.camada.repository.CustomQueryRepository;

import javax.annotation.Resource;

@Service
public class CustomQueryServiceImpl implements CustomQueryService {

    @Resource
    private final CustomQueryRepository customQueryRepository;

    public CustomQueryServiceImpl(CustomQueryRepository customQueryRepository) {
        this.customQueryRepository = customQueryRepository;
    }

}
