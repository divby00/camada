package org.wildcat.camada.common.enumerations;

import javafx.scene.control.TableColumn;
import org.wildcat.camada.service.PersistenceService;

public interface TextFieldTableColumn {
    <T> String getOldValue(TableColumn.CellEditEvent<T, String> event);

    <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service);

    <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue);
}
