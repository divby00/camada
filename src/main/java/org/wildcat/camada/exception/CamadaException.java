package org.wildcat.camada.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CamadaException extends RuntimeException {

    protected String key;
    protected String message;

    public CamadaException(String key, String message, Throwable e) {
        super(e);
        this.key = key;
        this.message = message;
    }
}
