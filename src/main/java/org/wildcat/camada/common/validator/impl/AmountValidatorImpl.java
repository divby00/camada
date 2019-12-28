package org.wildcat.camada.common.validator.impl;

import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class AmountValidatorImpl implements Validator {

    private Double[] amounts;
    private static BiPredicate<String, Double[]> biPredicate = ValidatorPredicates.isValidAmount;

    public AmountValidatorImpl(Double... amounts) {
        this.amounts = amounts;
    }

    @Override
    public Boolean validate(String text) {
        return biPredicate.test(text, amounts);
    }

    @Override
    public String getErrorMessage() {
        String amounts = Stream.of(this.amounts).map(Object::toString).collect(Collectors.joining(", "));
        return "La cantidad debe ser alguna de las siguientes: " + amounts;
    }

}
