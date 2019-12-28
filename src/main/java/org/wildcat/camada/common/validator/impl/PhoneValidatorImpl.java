package org.wildcat.camada.common.validator.impl;

import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.Predicate;

public class PhoneValidatorImpl implements Validator {

    private Predicate<String> predicate = ValidatorPredicates.isValidPhone;

    @Override
    public Boolean validate(String text) {
        return predicate.test(text);
    }

    @Override
    public String getErrorMessage() {
        return "El teléfono debe tener 9 cifras, puede contener opcionalmente el prefijo de España (+34) y debe comenzar por 6, 7, 8 o 9";
    }
}
