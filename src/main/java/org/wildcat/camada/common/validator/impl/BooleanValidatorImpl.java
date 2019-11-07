package org.wildcat.camada.common.validator.impl;

import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.Predicate;

public class BooleanValidatorImpl implements Validator {

    private final static Predicate<String> predicate = ValidatorPredicates.isValidBoolean;

    @Override
    public Boolean validate(String text) {
        return predicate.test(text);
    }
}
