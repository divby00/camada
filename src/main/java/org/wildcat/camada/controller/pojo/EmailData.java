package org.wildcat.camada.controller.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class EmailData {
    private List<String> emails;
    private List<String> placeholders;
}
