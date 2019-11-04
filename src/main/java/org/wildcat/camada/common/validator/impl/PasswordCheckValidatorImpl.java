package org.wildcat.camada.common.validator.impl;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.BiPredicate;

@Getter
@Setter
@AllArgsConstructor
public class PasswordCheckValidatorImpl<T extends TextField> implements Validator {

    private T textField;
    private T originalPasswordField;
    private static BiPredicate<String, String> biPredicate = ValidatorPredicates.passwordsAreTheSame;

    @Override
    public Boolean validate() {
        return biPredicate.test(textField.getText(), originalPasswordField.getText());
    }
}
