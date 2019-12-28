package org.wildcat.camada.common.validator.impl;

import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.Predicate;

@Getter
@Setter
public class EmailValidatorImpl implements Validator {

    private static Predicate<String> predicate = ValidatorPredicates.isValidEmail;

    @Override
    public Boolean validate(String text) {
        return predicate.test(text);
    }

    @Override
    public String getErrorMessage() {
        return "El email debe tener un formato de email correcto";
    }

}
