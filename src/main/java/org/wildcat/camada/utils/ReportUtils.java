package org.wildcat.camada.utils;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.stream.Collectors;

class ReportUtils {

    static <T> String[] getVisibleColumns(TableView<T> table) {
        List<String> visibleColumns = table.getColumns().stream()
                .filter(TableColumnBase::isVisible)
                .map(TableColumnBase::getText)
                .collect(Collectors.toList());
        String[] headers = new String[visibleColumns.size()];
        return visibleColumns.toArray(headers);
    }

    static <T> List getRecordList(TableView<T> table, T item) {
        return table.getVisibleLeafColumns().stream()
                .map(column -> column.getCellObservableValue(item).getValue())
                .collect(Collectors.toList());
    }
}
