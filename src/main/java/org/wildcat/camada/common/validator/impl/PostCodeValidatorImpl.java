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
public class PostCodeValidatorImpl implements Validator {

    private static Predicate<String> predicate = ValidatorPredicates.isValidPostCode;

    @Override
    public Boolean validate(String text) {
        return predicate.test(text);
    }

    @Override
    public String getErrorMessage() {
        return "El código postal debe estar formador por 5 números.";
    }

}
