package org.wildcat.camada.common.enumerations;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;

public interface CheckBoxTableColumn {
    <T> void setBooleanProperty(T item, CheckBox checkBox);
    <T> Boolean getBooleanProperty(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures);
}