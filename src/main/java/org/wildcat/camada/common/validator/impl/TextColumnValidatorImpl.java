package org.wildcat.camada.common.validator.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;
import org.wildcat.camada.common.validator.TriPredicate;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

@Getter
@Setter
@AllArgsConstructor
public class TextColumnValidatorImpl implements Validator {

    private Integer min;
    private Integer max;
    private static TriPredicate<String, Integer> triPredicate = ValidatorPredicates.isValidTextField;

    @Override
    public Boolean validate() {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public Boolean validateString(String text) {
        return triPredicate.test(text, min, max);
    }
}
