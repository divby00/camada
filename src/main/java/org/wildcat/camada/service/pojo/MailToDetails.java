package org.wildcat.camada.service.pojo;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.mail.internet.InternetAddress;
import java.util.List;

@Getter
@Setter
@Builder
public class MailToDetails {

    private String to;
    private String subject;
    private String message;
    private String fileName;
    private String attachment;
    private Boolean isHtml;
    private InternetAddress[] internetAddresses;

}
