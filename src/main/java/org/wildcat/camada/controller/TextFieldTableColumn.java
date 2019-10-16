package org.wildcat.camada.controller;

import javafx.scene.control.TableColumn;
import org.wildcat.camada.service.SavingService;

public interface TextFieldTableColumn {
    <T> String getOldValue(TableColumn.CellEditEvent<T, String> event);
    <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service);
    <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue);
}
