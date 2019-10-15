package org.wildcat.camada.service;

import javafx.scene.control.*;
import org.wildcat.camada.controller.CheckBoxParam;
import org.wildcat.camada.entity.CamadaUser;

import java.util.Date;

public interface TableCommonService {
    TableCell<CamadaUser, Date> getDateTableCell(String pattern);
    TableCell<CamadaUser, Boolean> getBooleanTableCell(CheckBoxParam param, TableView tableView);
}
