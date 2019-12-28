package org.wildcat.camada.common.validator.impl;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;

import java.util.function.BiPredicate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordCheckValidatorImpl<T extends TextField> implements Validator {

    private T originalPasswordField;
    private static BiPredicate<String, String> biPredicate = ValidatorPredicates.passwordsAreTheSame;

    @Override
    public Boolean validate(String text) {
        return biPredicate.test(text, originalPasswordField.getText());
    }

    @Override
    public String getErrorMessage() {
        return "El valor no debe estar vacío y ser igual al campo contraseña.";
    }

}
