package org.wildcat.camada.common.validator.impl;

import javafx.scene.control.TextField;
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
public class TextFieldValidatorImpl<T extends TextField> implements Validator {

    private T textField;
    private Integer min;
    private Integer max;
    private static TriPredicate<String, Integer> triPredicate = ValidatorPredicates.isValidTextField;

    @Override
    public Boolean validate() {
        return triPredicate.test(textField.getText(), min, max);
    }

    @Override
    public Boolean validateString(String text) {
        throw new NotImplementedException("Not implemented");
    }

}
