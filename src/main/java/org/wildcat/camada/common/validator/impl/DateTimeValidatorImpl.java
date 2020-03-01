package org.wildcat.camada.common.validator.impl;

import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.Predicate;

public class DateTimeValidatorImpl implements Validator {

    private final static Predicate<String> predicate = ValidatorPredicates.isValidDateTimeOrEmpty;

    @Override
    public Boolean validate(String text) {
        return predicate.test(text);
    }

    @Override
    public String getErrorMessage() {
        return "El valor debe ser vacío o una fecha con el formato dd/MM/yyyy HH:mm";
    }
}
