package org.wildcat.camada.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.StringUtils;

public class AlertUtils {

    public static ButtonType showUpdate(String oldValue, String newValue) {
        if (StringUtils.equalsAnyIgnoreCase(oldValue, "true", "false")) {
            oldValue = StringUtils.equalsIgnoreCase(oldValue, "true") ? "Si" : "No";
        }
        if (StringUtils.equalsAnyIgnoreCase(newValue, "true", "false")) {
            newValue = StringUtils.equalsIgnoreCase(newValue, "true") ? "Si" : "No";
        }
        String text = "¿Quieres cambiar el valor '" + oldValue + "' por '" + newValue + "'?";
        return showConfirmation(text);
    }

    public static void showInfo(String content) {
        showDialog(Alert.AlertType.INFORMATION, "Información", content);
    }

    public static void showError(String content) {
        showDialog(Alert.AlertType.ERROR, "Error", content);
    }

    public static ButtonType showConfirmation(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
        return alert.getResult();
    }

    public static void showDialog(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
