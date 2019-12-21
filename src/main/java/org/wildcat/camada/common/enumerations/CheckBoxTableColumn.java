package org.wildcat.camada.common.enumerations;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;

public interface CheckBoxTableColumn {
    <T> void setBoolean(T item, CheckBox checkBox);

    <T> Boolean getBoolean(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures);
}
