package org.wildcat.camada.service.impl;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.controller.pojo.AppTableColumn;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.TableCommonService;
import org.wildcat.camada.service.utils.AlertUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TableCommonServiceImpl<T> implements TableCommonService<T> {

    private final CamadaUserService camadaUserService;

    @Autowired
    public TableCommonServiceImpl(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initCheckBoxTableCell(TableColumn<T, Boolean> column, CustomTableColumn param, TableView<T> table, ProgressIndicator progressIndicator) {
        column.setCellValueFactory(c -> new SimpleBooleanProperty(param.getBooleanProperty(c)));
        column.setCellFactory(p -> getCheckBoxTableCell(param, table, progressIndicator));
    }

    @Override
    public TableCell<T, Date> getDateTableCell(String pattern) {
        TableCell<T, Date> cell = new TableCell<T, Date>() {
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

    @Override
    public void initTextFieldTableCell(AppTableColumn<T> tableColumn, TableView table, ProgressIndicator progressIndicator) {
        tableColumn.getColumn().setCellValueFactory(new PropertyValueFactory<>(tableColumn.getColumnName()));
        tableColumn.getColumn().setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumn.getColumn().setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            String oldValue = tableColumn.getCustomTableColumn().getOldValue(event);
            boolean validates = tableColumn.getValidator().validate(newValue);
            if (newValue.equals(oldValue) || !validates) {
                if (!validates) {
                    AlertUtils.showError("El campo es incorrecto.");
                }
                table.refresh();
                return;
            }
            ButtonType buttonType = AlertUtils.showUpdate(oldValue, newValue);
            if (buttonType == ButtonType.YES) {
                Task<Boolean> updateTextFieldTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() {
                        boolean result = false;
                        try {
                            tableColumn.getCustomTableColumn().setNewValue(event, newValue, camadaUserService);
                            result = true;
                        } catch (Exception ignored) {
                        }
                        return result;
                    }
                };
                progressIndicator.visibleProperty().bind(updateTextFieldTask.runningProperty());
                new Thread(updateTextFieldTask).start();
                updateTextFieldTask.setOnSucceeded(worker -> {
                    boolean result = updateTextFieldTask.getValue();
                    if (result) {
                        AlertUtils.showInfo("El campo se ha actualizado correctamente.");
                    } else {
                        AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
                    }
                });
                updateTextFieldTask.setOnCancelled(worker -> {
                    AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
                });
                updateTextFieldTask.setOnFailed(worker -> {
                    AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
                });
            } else {
                tableColumn.getCustomTableColumn().setOldValue(event, oldValue);
                table.refresh();
            }
        });
    }

    private <T> TableCell<T, Boolean> getCheckBoxTableCell(CustomTableColumn param, TableView table, ProgressIndicator progressIndicator) {
        CheckBox checkBox = new CheckBox();
        TableCell<T, Boolean> tableCell = new TableCell<T, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(checkBox);
                checkBox.setSelected(item);
            }
        };
        Task<Boolean> updateCheckTask = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                boolean result = false;
                try {
                    T item = (T) tableCell.getTableRow().getItem();
                    param.setBooleanProperty(item, checkBox);
                    // TODO: Add proper service
                    //camadaUserService.save(item);
                    table.refresh();
                    result = true;
                } catch (Exception ignored) {
                }
                return result;
            }
        };
        progressIndicator.visibleProperty().bind(updateCheckTask.runningProperty());
        checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (validate(checkBox, event)) {
                updateCheckBox(updateCheckTask, progressIndicator);
            }
        });
        checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (validate(checkBox, event)) {
                    updateCheckBox(updateCheckTask, progressIndicator);
                }
            }
        });

        tableCell.setAlignment(Pos.CENTER);
        tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return tableCell;
    }

    private void updateCheckBox(Task<Boolean> updateCheckTask, ProgressIndicator progressIndicator) {
        progressIndicator.visibleProperty().bind(updateCheckTask.runningProperty());
        new Thread(updateCheckTask).start();
        updateCheckTask.setOnSucceeded(worker -> {
            if (updateCheckTask.getValue()) {
                AlertUtils.showInfo("El campo se ha actualizado correctamente.");
            } else {
                AlertUtils.showInfo("Ha ocurrido un error al actualizar el campo.");
            }
        });
        updateCheckTask.setOnFailed(worker -> {
            AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
        });
        updateCheckTask.setOnCancelled(worker -> {
            AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
        });
    }

    private boolean validate(CheckBox checkBox, Event event) {
        boolean result = false;
        event.consume();
        ButtonType buttonType = AlertUtils.showUpdate(Boolean.toString(checkBox.isSelected()), Boolean.toString(!checkBox.isSelected()));
        if (buttonType == ButtonType.YES) {
            checkBox.setSelected(!checkBox.isSelected());
            result = true;
        }
        return result;
    }

}
