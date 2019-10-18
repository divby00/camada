package org.wildcat.camada.service;

import javafx.scene.control.*;
import org.wildcat.camada.enumerations.CustomTableColumn;
import org.wildcat.camada.entity.CamadaUser;

import java.util.Date;

public interface TableCommonService {
    TableCell<CamadaUser, Date> getDateTableCell(String pattern);
    void initTextFieldTableCell(TableColumn<CamadaUser, String> column, String columnName, CustomTableColumn param, TableView table);
    void initCheckBoxTableCell(TableColumn<CamadaUser, Boolean> column, CustomTableColumn param, TableView<CamadaUser> listTable);
}
