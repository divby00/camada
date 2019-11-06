package org.wildcat.camada.common.validator.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.Predicate;

@Getter
@Setter
@AllArgsConstructor
public class PasswordValidatorImpl implements Validator {

    static Predicate<String> predicate = ValidatorPredicates.isValidPassword;

    @Override
    public Boolean validate(String text) {
        return predicate.test(text);
    }

}
