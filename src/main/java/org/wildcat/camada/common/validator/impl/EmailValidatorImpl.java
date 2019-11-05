package org.wildcat.camada.common.validator.impl;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.Predicate;

@Getter
@Setter
@AllArgsConstructor
public class EmailValidatorImpl<T extends TextField> implements Validator {

    private T textField;
    private static Predicate<String> predicate = ValidatorPredicates.isValidEmail;

    @Override
    public Boolean validate() {
        return predicate.test(textField.getText());
    }

    @Override
    public Boolean validateString(String text) {
        throw new NotImplementedException("Not implemented");
    }

}
