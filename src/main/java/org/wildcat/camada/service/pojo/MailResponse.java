package org.wildcat.camada.service.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
public class MailResponse {
    private Boolean success;
    private String email;
    private String errorMessage;
}
