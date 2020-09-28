package org.wildcat.camada.controller.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class EmailUserData {
    private List<String> emails;
    private List<String> placeholders;
    private Map<String, Map<String, Object>> rowInfo;
}
