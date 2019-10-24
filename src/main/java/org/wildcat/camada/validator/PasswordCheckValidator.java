package org.wildcat.camada.validator;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiPredicate;

@Getter
@Setter
@AllArgsConstructor
public class PasswordCheckValidator<T extends TextField> implements Validator {

    private T textField;
    private T originalPasswordField;
    private static BiPredicate<String, String> biPredicate = ValidatorPredicates.passwordsAreTheSame;

    @Override
    public Boolean validate() {
        return biPredicate.test(textField.getText(), originalPasswordField.getText());
    }
}
