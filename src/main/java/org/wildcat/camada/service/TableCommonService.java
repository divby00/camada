package org.wildcat.camada.service;

import javafx.scene.control.*;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.persistence.entity.CamadaUser;

import java.util.Date;

public interface TableCommonService {
    TableCell<CamadaUser, Date> getDateTableCell(String pattern);
    void initTextFieldTableCell(TableColumn<CamadaUser, String> column, String columnName, CustomTableColumn param, TableView table, ProgressIndicator progressIndicator);
    void initCheckBoxTableCell(TableColumn<CamadaUser, Boolean> column, CustomTableColumn param, TableView<CamadaUser> listTable, ProgressIndicator progressIndicator);
}
