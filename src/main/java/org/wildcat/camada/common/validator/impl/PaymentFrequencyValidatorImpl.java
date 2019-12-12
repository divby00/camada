package org.wildcat.camada.common.validator.impl;

import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;
import org.wildcat.camada.persistence.PaymentFrequency;

import java.util.function.Predicate;

@Getter
@Setter
public class PaymentFrequencyValidatorImpl implements Validator {

    private static Predicate<PaymentFrequency> predicate = ValidatorPredicates.isValidPaymentFrequency;

    @Override
    public Boolean validate(String text) {
        return predicate.test(PaymentFrequency.valueOf(text));
    }

}
