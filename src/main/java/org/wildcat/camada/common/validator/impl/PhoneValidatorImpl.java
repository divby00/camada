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
}
