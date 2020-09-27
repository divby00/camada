package org.wildcat.camada.service;

import org.wildcat.camada.service.pojo.MailToDetails;

public interface MailService {
    boolean send(MailToDetails mailToDetails);
    boolean containsPlaceholder(String message);
}
