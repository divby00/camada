package org.wildcat.camada.validator;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

@Getter
@Setter
@AllArgsConstructor
public class EmailValidator<T extends TextField> implements Validator {

    private T textField;
    private static Predicate<String> predicate = ValidatorPredicates.isValidEmail;

    @Override
    public Boolean validate() {
        return predicate.test(textField.getText());
    }
}
