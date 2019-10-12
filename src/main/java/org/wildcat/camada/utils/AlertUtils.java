package org.wildcat.camada.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ResourceBundle;

public class AlertUtils {

    public static void error(ResourceBundle resources, String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resources.getString(title));
        alert.setHeaderText(resources.getString(header));
        alert.setContentText(resources.getString(content));
        alert.showAndWait();
    }

    public static ButtonType showUpdateAlert(String oldValue, String newValue) {
        String text = "¿Quieres cambiar el valor '" + oldValue + "' por '" + newValue + "'?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
        return alert.getResult();
    }

}
