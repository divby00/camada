package org.wildcat.camada.service;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.controller.pojo.AppTableColumn;

import java.util.Date;

public interface TableCommonService<T> {
    TableCell<T, Date> getDateTableCell(String pattern);

    void initTextFieldTableCell(AppTableColumn<T> tableColumn, TableView table, ProgressIndicator progressIndicator, PersistenceService persistenceService);

    void initPaymentFrequencyFieldTableCell(AppTableColumn<T> tableColumn, TableView table, ProgressIndicator progressIndicator, PersistenceService persistenceService);

    void initCheckBoxTableCell(TableColumn<T, Boolean> column, CustomTableColumn param, TableView<T> table, ProgressIndicator progressIndicator,
            PersistenceService persistenceService);
}
