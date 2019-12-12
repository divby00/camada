package org.wildcat.camada.common.validator.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.TriPredicate;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.BiPredicate;

@Getter
@Setter
public class AmountValidatorImpl implements Validator {

    private Double [] amounts;
    private static BiPredicate<String, Double[]> biPredicate = ValidatorPredicates.isValidAmount;

    public AmountValidatorImpl(Double ... amounts) {
        this.amounts = amounts;
    }

    @Override
    public Boolean validate(String text) {
        return biPredicate.test(text, amounts);
    }

}
