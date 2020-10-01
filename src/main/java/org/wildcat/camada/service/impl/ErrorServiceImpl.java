package org.wildcat.camada.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.wildcat.camada.service.ErrorService;

@Slf4j
@Service
public class ErrorServiceImpl implements ErrorService {

    @Override
    public String getErrorMessage(Throwable throwable) {
        return ExceptionUtils.getStackTrace(throwable);
    }
}
