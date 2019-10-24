package org.wildcat.camada.validator;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TextFieldValidator<T extends TextField> implements Validator {

    private T textField;
    private Integer min;
    private Integer max;
    private static TriPredicate<String, Integer> triPredicate = ValidatorPredicates.isValidTextField;

    @Override
    public Boolean validate() {
        return triPredicate.test(textField.getText(), min, max);
    }

}
