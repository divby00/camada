package org.wildcat.camada.common.validator;

public interface Validator {
    Boolean validate(String text);
    String getErrorMessage();
}
