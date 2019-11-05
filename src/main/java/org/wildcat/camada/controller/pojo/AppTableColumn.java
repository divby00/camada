package org.wildcat.camada.controller.pojo;

import javafx.scene.control.TableColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.common.validator.Validator;

@Getter
@Setter
@AllArgsConstructor
public class AppTableColumn<T> {
    private TableColumn<T, String> column;
    private String columnName;
    private Validator validator;
    private CustomTableColumn customTableColumn;
}
