package org.wildcat.camada.service;

import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wildcat.camada.controller.CheckBoxParam;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.utils.AlertUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TableCommonServiceImpl implements TableCommonService {

    private final CamadaUserService camadaUserService;

    @Autowired
    public TableCommonServiceImpl(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    public TableCell<CamadaUser, Date> getDateTableCell(String pattern) {
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

    public TableCell<CamadaUser, Boolean> getBooleanTableCell(CheckBoxParam param, TableView tableView) {
        CheckBox checkBox = new CheckBox();
        TableCell<CamadaUser, Boolean> tableCell = new TableCell<CamadaUser, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) return;
                setGraphic(checkBox);
                checkBox.setSelected(item);
            }
        };
        checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (validate(checkBox, event)) {
                CamadaUser item = (CamadaUser) tableCell.getTableRow().getItem();
                param.apply(item, checkBox);
                camadaUserService.save(item);
                tableView.refresh();
            }
        });
        checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (validate(checkBox, event)) {
                    CamadaUser item = (CamadaUser) tableCell.getTableRow().getItem();
                    param.apply(item, checkBox);
                    camadaUserService.save(item);
                    tableView.refresh();
                }
            }
        });

        tableCell.setAlignment(Pos.CENTER);
        tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return tableCell;
    }


    private boolean validate(CheckBox checkBox, Event event) {
        boolean result = false;
        event.consume();
        ButtonType buttonType = AlertUtils.showUpdateAlert(Boolean.toString(checkBox.isSelected()), Boolean.toString(!checkBox.isSelected()));
        if (buttonType == ButtonType.YES) {
            checkBox.setSelected(!checkBox.isSelected());
            result = true;
        }
        return result;
    }
}
