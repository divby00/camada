package org.wildcat.camada.exception;

public class EmailCamadaException extends CamadaException {

    public EmailCamadaException(String message, Throwable e) {
        super("EMAIL", message, e);
    }
}
