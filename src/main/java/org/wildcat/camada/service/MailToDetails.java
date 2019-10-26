package org.wildcat.camada.service;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailToDetails {

    private String to;
    private String subject;
    private String message;
    private String fileName;
    private String attachment;

}
