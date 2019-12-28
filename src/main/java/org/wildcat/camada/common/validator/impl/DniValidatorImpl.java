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
public class DniValidatorImpl implements Validator {

    private static Predicate<String> predicate = ValidatorPredicates.isValidDni;

    @Override
    public Boolean validate(String text) {
        return predicate.test(text);
    }

    @Override
    public String getErrorMessage() {
        return "El DNI debe contener ocho cifras y una letra y ser un DNI español válido.";
    }

}
