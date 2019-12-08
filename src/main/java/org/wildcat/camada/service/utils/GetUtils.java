package org.wildcat.camada.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.function.Supplier;

@Slf4j
public class GetUtils {

    @SuppressWarnings("unchecked")
    public static <T> T get(Supplier supplier, T... defaultValues) {
        try {
            T t = (T) supplier.get();
            if (t != null) {
                return t;
            }
        } catch (NullPointerException | IllegalArgumentException ex) {
            log.debug(ExceptionUtils.getStackTrace(ex));
        }
        return defaultValues[0];
    }
}
