package org.wildcat.camada.service.impl;

import javafx.beans.binding.Bindings;
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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import jfxtras.scene.control.CalendarTextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.controller.BaseController;
import org.wildcat.camada.controller.pojo.AppTableColumn;
import org.wildcat.camada.persistence.PaymentFrequency;
import org.wildcat.camada.persistence.dto.PartnerDTO;
import org.wildcat.camada.service.PersistenceService;
import org.wildcat.camada.service.TableCommonService;
import org.wildcat.camada.service.utils.AlertUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

@Slf4j
@Service
public class TableCommonServiceImpl<T> implements TableCommonService<T> {

    @Override
    public void initCheckBoxTableCell(TableColumn<T, Boolean> column, CustomTableColumn param, TableView<T> table, ProgressIndicator progressIndicator,
            BaseController baseController, PersistenceService... persistenceServices) {
        column.setCellValueFactory(c -> new SimpleBooleanProperty(param.getBoolean(c)));
        column.setCellFactory(p -> getCheckBoxTableCell(param, table, progressIndicator, baseController, persistenceServices));
    }

    @Override
    public TableCell<T, Date> getDateTableCell(String pattern) {
        return new TableCell<T, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(DateFormatUtils.format(item, pattern));
                }
            }
        };
    }

    @Override
    public void initTextFieldTableCell(AppTableColumn<T, String> tableColumn, TableView table, ProgressIndicator progressIndicator,
            PersistenceService... persistenceServices) {
        tableColumn.getColumn().setCellValueFactory(new PropertyValueFactory<>(tableColumn.getColumnName()));
        tableColumn.getColumn().setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumn.getColumn().setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            String oldValue = tableColumn.getCustomTableColumn().getOldValue(event);
            Validator validator = tableColumn.getValidator();
            boolean validates = validator.validate(newValue);
            if (newValue.equals(oldValue) || !validates) {
                if (!validates) {
                    AlertUtils.showError(validator.getErrorMessage());
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
                            tableColumn.getCustomTableColumn().setNewValue(event, newValue, persistenceServices[0]);
                            result = true;
                        } catch (Exception ex) {
                            log.error(ExceptionUtils.getStackTrace(ex));
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

    @Override
    public void initPaymentFrequencyFieldTableCell(AppTableColumn<T, String> tableColumn, TableView table, ProgressIndicator progressIndicator,
            PersistenceService... persistenceServices) {
        tableColumn.getColumn().setCellValueFactory(param -> {
            PartnerDTO partnerDTO = (PartnerDTO) param.getValue();
            return Bindings.createStringBinding(() -> partnerDTO.getPaymentFrequency().getLabel());
        });
        String[] frequencies = Stream.of(PaymentFrequency.values()).map(PaymentFrequency::getLabel).toArray(String[]::new);
        tableColumn.getColumn().setCellFactory(ComboBoxTableCell.forTableColumn(frequencies));
        tableColumn.getColumn().setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            String oldValue = getPaymentFrequencyLabel(tableColumn.getCustomTableColumn().getOldValue(event));
            if (newValue.equals(oldValue)) {
                table.refresh();
                return;
            }
            ButtonType buttonType = AlertUtils.showUpdate(oldValue, newValue);
            if (buttonType == ButtonType.YES) {
                Task<Boolean> updatePaymentTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() {
                        boolean result = false;
                        try {
                            tableColumn.getCustomTableColumn().setNewValue(event, getPaymentFrequencyName(newValue), persistenceServices[0]);
                            result = true;
                        } catch (Exception ex) {
                            log.error(ExceptionUtils.getStackTrace(ex));
                        }
                        return result;
                    }
                };
                progressIndicator.visibleProperty().bind(updatePaymentTask.runningProperty());
                new Thread(updatePaymentTask).start();
                updatePaymentTask.setOnSucceeded(worker -> {
                    boolean result = updatePaymentTask.getValue();
                    if (result) {
                        AlertUtils.showInfo("El campo se ha actualizado correctamente.");
                    } else {
                        AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
                    }
                });
                updatePaymentTask.setOnCancelled(worker -> {
                    AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
                });
                updatePaymentTask.setOnFailed(worker -> {
                    AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
                });
            } else {
                tableColumn.getCustomTableColumn().setOldValue(event, tableColumn.getCustomTableColumn().getOldValue(event));
                table.refresh();
            }
        });
    }

    private String getPaymentFrequencyLabel(String enumName) {
        return Stream.of(PaymentFrequency.values())
                .filter(frequency -> StringUtils.equalsIgnoreCase(frequency.name(), enumName))
                .map(PaymentFrequency::getLabel)
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }

    private String getPaymentFrequencyName(String label) {
        return Stream.of(PaymentFrequency.values())
                .filter(frequency -> StringUtils.equalsIgnoreCase(frequency.getLabel(), label))
                .map(PaymentFrequency::name)
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }

    private TableCell<T, Boolean> getCheckBoxTableCell(CustomTableColumn param, TableView table, ProgressIndicator progressIndicator,
            BaseController baseController, PersistenceService... persistenceServices) {
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
                    param.setBoolean(item, checkBox);
                    persistenceServices[0].saveEntity(item);
                    baseController.fetchData();
                    table.refresh();
                    result = true;
                } catch (Exception ex) {
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
                return result;
            }
        };
        progressIndicator.visibleProperty().bind(updateCheckTask.runningProperty());
        checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (validateCheckBox(checkBox, event)) {
                updateCheckBox(updateCheckTask, progressIndicator);
            }
        });
        checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (validateCheckBox(checkBox, event)) {
                    updateCheckBox(updateCheckTask, progressIndicator);
                }
            }
        });
        tableCell.setAlignment(Pos.CENTER);
        tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return tableCell;
    }

    private boolean validateCheckBox(CheckBox checkBox, Event event) {
        boolean result = false;
        event.consume();
        ButtonType buttonType = AlertUtils.showUpdate(Boolean.toString(checkBox.isSelected()), Boolean.toString(!checkBox.isSelected()));
        if (buttonType == ButtonType.YES) {
            checkBox.setSelected(!checkBox.isSelected());
            result = true;
        }
        return result;
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

    @Override
    public void initCalendarTextFieldTableCell(AppTableColumn<T, Date> tableColumn, TableView table, ProgressIndicator progressIndicator,
            PersistenceService... persistenceServices) {
        tableColumn.getColumn().setCellValueFactory(p -> {
            PartnerDTO partnerDTO = (PartnerDTO) p.getValue();
            return tableColumn.getCustomTableColumn().getDateValue(partnerDTO);
        });
        tableColumn.getColumn().setCellFactory(p -> getCalendarTextFieldTableCell(tableColumn.getCustomTableColumn(), table, progressIndicator, persistenceServices));
    }

    private TableCell<T, Date> getCalendarTextFieldTableCell(CustomTableColumn param, TableView table, ProgressIndicator progressIndicator,
            PersistenceService... persistenceServices) {
        CalendarTextField calendarTextField = new CalendarTextField();

        TableCell<T, Date> tableCell = new TableCell<T, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(item);
                calendarTextField.setCalendar(calendar);
                setGraphic(calendarTextField);
            }
        };
        Task<Date> updateDateTask = new Task<Date>() {
            @Override
            protected Date call() {
                Date result = null;
                try {
                    T item = (T) tableCell.getTableRow().getItem();
                    param.setDate(item, calendarTextField, persistenceServices[0]);
                    table.refresh();
                    result = calendarTextField.getCalendar().getTime();
                } catch (Exception ex) {
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
                return result;
            }
        };
        progressIndicator.visibleProperty().bind(updateDateTask.runningProperty());
        calendarTextField.setValueValidationCallback(calendar -> {
            if (validateDate(calendarTextField, calendar.getTime())) {
                updateCalendarTextField(updateDateTask, progressIndicator);
            }
            return false;
        });
        tableCell.setAlignment(Pos.CENTER);
        tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return tableCell;
    }

    private boolean validateDate(CalendarTextField calendarTextField, Date date) {
        boolean result = false;
        ButtonType buttonType = AlertUtils.showUpdate(DateFormatUtils.format(date, "dd/MM/yyyy"));
        if (buttonType == ButtonType.YES) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendarTextField.setCalendar(calendar);
            result = true;
        }
        return result;
    }

    private void updateCalendarTextField(Task<Date> updateDateTask, ProgressIndicator progressIndicator) {
        progressIndicator.visibleProperty().bind(updateDateTask.runningProperty());
        new Thread(updateDateTask).start();
        updateDateTask.setOnSucceeded(worker -> {
            if (updateDateTask.getValue() == null) {
                AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
            } else {
                AlertUtils.showInfo("El campo se ha actualizado correctamente.");
            }
        });
        updateDateTask.setOnFailed(worker -> {
            AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
        });
        updateDateTask.setOnCancelled(worker -> {
            AlertUtils.showError("Ha ocurrido un error al actualizar el campo.");
        });
    }

}
