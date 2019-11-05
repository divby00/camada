package org.wildcat.camada.common.validator;

public interface Validator {
    Boolean validate();
    Boolean validateString(String text);
}
