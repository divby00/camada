package org.wildcat.camada.controller;

import javafx.scene.image.ImageView;
import org.wildcat.camada.common.validator.Validator;

public class NewEntityController {

    protected boolean validate(Validator validator, String text, ImageView imageView) {
        boolean result = validator.validate(text);
        imageView.setVisible(!result);
        return result;
    }

}
