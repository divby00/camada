package org.wildcat.camada.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

public class FilterUtils {

    public static void mapField(Map<String, Object> map, String key, java.io.Serializable value) {
        if (!Objects.isNull(value)) {
            if (value instanceof String) {
                if (StringUtils.isNotBlank(value.toString())) {
                    map.put(key, value);
                }
            } else {
                map.put(key, value);
            }
        }
    }

    public static String buildQuery(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> {
            sb.append(" and ").append(k);
            if (v instanceof Boolean) {
                if (Boolean.valueOf(v.toString())) {
                    sb.append(" and ").append(v).append(" = 1 ");
                }
            }
            if (v instanceof String) {
                sb.append(" like '%").append(v).append("%'");
            }
        });
        return sb.toString();
    }

}
