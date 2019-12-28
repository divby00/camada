package org.wildcat.camada.common.validator.impl;

import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;
import org.wildcat.camada.persistence.PaymentFrequency;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class PaymentFrequencyValidatorImpl implements Validator {

    private static Predicate<PaymentFrequency> predicate = ValidatorPredicates.isValidPaymentFrequency;
    private static List<String> labels = Stream.of(PaymentFrequency.values()).map(PaymentFrequency::getLabel).collect(Collectors.toList());

    @Override
    public Boolean validate(String text) {
        PaymentFrequency paymentFrequency = null;
        if (labels.contains(text)) {
            paymentFrequency = Stream.of(PaymentFrequency.values()).filter(pf -> pf.getLabel().equals(text)).findFirst().orElse(null);
        }
        PaymentFrequency finalPaymentFrequency = paymentFrequency;
        boolean b = Stream.of(PaymentFrequency.values()).anyMatch(freq -> finalPaymentFrequency == freq);
        return predicate.test(paymentFrequency);
    }

    @Override
    public String getErrorMessage() {
        String values = Stream.of(PaymentFrequency.values()).map(PaymentFrequency::getLabel).collect(Collectors.joining(", "));
        return "La frecuencia de pago debe ser alguno de los siguientes valores: " + values;
    }

}
