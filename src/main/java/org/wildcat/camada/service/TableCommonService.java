package org.wildcat.camada.service;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.controller.pojo.AppTableColumn;
import org.wildcat.camada.persistence.entity.CamadaUser;

import java.util.Date;

public interface TableCommonService {
    TableCell<CamadaUser, Date> getDateTableCell(String pattern);
    <T> void initTextFieldTableCell(AppTableColumn<T> tableColumn, TableView table, ProgressIndicator progressIndicator);
    void initCheckBoxTableCell(TableColumn<CamadaUser, Boolean> column, CustomTableColumn param, TableView<CamadaUser> listTable, ProgressIndicator progressIndicator);
}
