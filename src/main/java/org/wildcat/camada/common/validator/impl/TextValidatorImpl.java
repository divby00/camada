package org.wildcat.camada.common.validator.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.TriPredicate;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

@Getter
@Setter
@AllArgsConstructor
public class TextValidatorImpl implements Validator {

    private Integer min;
    private Integer max;
    private static TriPredicate<String, Integer> triPredicate = ValidatorPredicates.isValidTextField;

    public TextValidatorImpl(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Boolean validate(String text) {
        return triPredicate.test(text, min, max);
    }

}
