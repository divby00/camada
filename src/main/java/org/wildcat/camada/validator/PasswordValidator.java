package org.wildcat.camada.validator;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

@Getter
@Setter
@AllArgsConstructor
public class PasswordValidator<T extends TextField> implements Validator {

    private T passwordField;
    static Predicate<String> predicate = ValidatorPredicates.isValidPassword;

    @Override
    public Boolean validate() {
        return predicate.test(passwordField.getText());
    }
}
