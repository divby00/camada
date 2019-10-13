package org.wildcat.camada.utils;

import javafx.scene.control.TableCell;
import org.wildcat.camada.entity.CamadaUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TableUtils {

    public static TableCell<CamadaUser, Boolean> getBooleanTableCell() {
        TableCell<CamadaUser, Boolean> cell = new TableCell<CamadaUser, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    String text = (item == Boolean.TRUE) ? "Si" : "No";
                    setText(text);
                }
            }
        };
        return cell;
    }

    public static TableCell<CamadaUser, Date> getDateTableCell(String pattern) {
        TableCell<CamadaUser, Date> cell = new TableCell<CamadaUser, Date>() {
            private SimpleDateFormat format = new SimpleDateFormat(pattern);

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        };
        return cell;
    }


}
