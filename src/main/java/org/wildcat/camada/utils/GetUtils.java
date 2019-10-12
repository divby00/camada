package org.wildcat.camada.utils;

import org.slf4j.Logger;

import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

public class GetUtils {

    private static final Logger logger = getLogger(GetUtils.class);

    @SuppressWarnings("unchecked")
    public static <T> T get(Supplier supplier, T ... defaultValues) {
        try {
            T t = (T)supplier.get();
            if (t != null) {
                return t;
            }
        } catch (NullPointerException | IllegalArgumentException ex) {
            logger.debug(ex.getMessage());
        }
        return defaultValues[0];
    }
}
