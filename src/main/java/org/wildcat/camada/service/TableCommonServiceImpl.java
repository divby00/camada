package org.wildcat.camada.service;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.enumerations.CustomTableColumn;
import org.wildcat.camada.utils.AlertUtils;
import org.wildcat.camada.validator.ValidatorPredicates;

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

    @Override
    public void initTextFieldTableCell(TableColumn<CamadaUser, String> column, String columnName, CustomTableColumn param, TableView table, ProgressIndicator progressIndicator) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            String oldValue = param.getOldValue(event);
            boolean validates = ValidatorPredicates.isValidTextField.test(newValue, 4, 20);
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
                            param.setNewValue(event, newValue, camadaUserService);
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
                param.setOldValue(event, oldValue);
                table.refresh();
            }
        });
    }

    @Override
    public void initCheckBoxTableCell(TableColumn<CamadaUser, Boolean> column, CustomTableColumn param, TableView<CamadaUser> table, ProgressIndicator progressIndicator) {
        column.setCellValueFactory(c -> new SimpleBooleanProperty(param.getBooleanProperty(c)));
        column.setCellFactory(p -> getCheckBoxTableCell(param, table, progressIndicator));
    }

    private TableCell<CamadaUser, Boolean> getCheckBoxTableCell(CustomTableColumn param, TableView table, ProgressIndicator progressIndicator) {
        CheckBox checkBox = new CheckBox();
        TableCell<CamadaUser, Boolean> tableCell = new TableCell<CamadaUser, Boolean>() {
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
                    CamadaUser item = (CamadaUser) tableCell.getTableRow().getItem();
                    param.setBooleanProperty(item, checkBox);
                    camadaUserService.save(item);
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
                updateCheckBox(updateCheckTask);
            }
        });
        checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (validate(checkBox, event)) {
                    updateCheckBox(updateCheckTask);
                }
            }
        });

        tableCell.setAlignment(Pos.CENTER);
        tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return tableCell;
    }

    private void updateCheckBox(Task<Boolean> updateCheckTask) {
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
