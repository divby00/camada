package org.wildcat.camada.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.utils.FilterUtils;
import org.wildcat.camada.service.TableCommonService;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class UserController implements Initializable {

    @FXML
    private TableView<CamadaUser> table;

    @FXML
    private TableColumn<CamadaUser, String> userName;

    @FXML
    private TableColumn<CamadaUser, String> firstName;

    @FXML
    private TableColumn<CamadaUser, String> lastName;

    @FXML
    private TableColumn<CamadaUser, String> email;

    @FXML
    private TableColumn<CamadaUser, Boolean> isAdmin;

    @FXML
    private TableColumn<CamadaUser, Boolean> isVirtualSponsor;

    @FXML
    private TableColumn<CamadaUser, Boolean> isPresentialSponsor;

    @FXML
    private TableColumn<CamadaUser, Boolean> isPartner;

    @FXML
    private TableColumn<CamadaUser, Boolean> isVolunteer;

    @FXML
    private TableColumn<CamadaUser, Date> activationDate;

    @FXML
    private TableColumn<CamadaUser, Date> lastConnection;

    @FXML
    private TextField filterUserName;

    @FXML
    private TextField filterName;

    @FXML
    private TextField filterSurname;

    @FXML
    private TextField filterEmail;

    @FXML
    private CheckBox filterAdmin;

    @FXML
    private CheckBox filterPartner;

    @FXML
    private CheckBox filterVolunteer;

    @FXML
    private CheckBox filterVirtualSponsor;

    @FXML
    private CheckBox filterPresentialSponsor;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final TableCommonService tableCommonService;

    @Resource
    private final CamadaUserService camadaUserService;

    public UserController(TableCommonService tableCommonService, CamadaUserService camadaUserService) {
        this.tableCommonService = tableCommonService;
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableCommonService.initTextFieldTableCell(userName, "name", CustomTableColumn.NAME, table);;
        tableCommonService.initTextFieldTableCell(firstName, "firstName", CustomTableColumn.FIRST_NAME, table);;
        tableCommonService.initTextFieldTableCell(lastName, "lastName", CustomTableColumn.LAST_NAME, table);;
        tableCommonService.initTextFieldTableCell(email, "email", CustomTableColumn.EMAIL, table);;
        tableCommonService.initCheckBoxTableCell(isAdmin, CustomTableColumn.IS_ADMIN, table);
        tableCommonService.initCheckBoxTableCell(isVirtualSponsor, CustomTableColumn.IS_VIRTUAL_SPONSOR, table);
        tableCommonService.initCheckBoxTableCell(isPresentialSponsor, CustomTableColumn.IS_PRESENTIAL_SPONSOR, table);
        tableCommonService.initCheckBoxTableCell(isPartner, CustomTableColumn.IS_PARTNER, table);
        tableCommonService.initCheckBoxTableCell(isVolunteer, CustomTableColumn.IS_VOLUNTEER, table);
        activationDate.setCellValueFactory(new PropertyValueFactory<>("activationDate"));
        lastConnection.setCellValueFactory(new PropertyValueFactory<>("lastConnection"));
        activationDate.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
        lastConnection.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy HH:mm:ss"));

        // Populate table
        Iterable<CamadaUser> camadaUsers = camadaUserService.findAll();
        ObservableList<CamadaUser> iterables = FXCollections.observableList((List<CamadaUser>) camadaUsers);
        table.setItems(iterables);

        // Add context menu for deleting rows
        table.setRowFactory(tableView -> {
            final TableRow<CamadaUser> row = new TableRow<CamadaUser>() {
                @Override
                protected void updateItem(CamadaUser user, boolean empty) {
                    super.updateItem(user, empty);
                }
            };

            // Add context menu to the row
            final ContextMenu rowMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Borrar usuario");
            removeItem.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Quieres borrar el usuario?'", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    CamadaUser user = row.getItem();
                    camadaUserService.delete(user);
                    table.getItems().remove(user);
                }
            });
            rowMenu.getItems().addAll(removeItem);

            // only display context menu for non-null items:
            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu) null));
            return row;
        });

    }

    public void filterChange(ActionEvent event) {
    }

    public void search(ActionEvent event) {
        Map<String, Object> filterMap = buildFilterMap();
        String query = FilterUtils.buildQuery(filterMap);
    }

    public void goToHome(ActionEvent event) {
        stageManager.switchScene(FxmlView.DASHBOARD);
    }

    private Map<String, Object> buildFilterMap() {
        Map<String, Object> map = new HashMap<>();
        FilterUtils.mapField(map, "name", filterUserName.getText());
        FilterUtils.mapField(map, "first_name", filterName.getText());
        FilterUtils.mapField(map, "last_name", filterSurname.getText());
        FilterUtils.mapField(map, "email", filterEmail.getText());
        FilterUtils.mapField(map, "is_admin", filterAdmin.isSelected());
        FilterUtils.mapField(map, "is_partner", filterPartner.isSelected());
        FilterUtils.mapField(map, "is_virtual_sponsor", filterVirtualSponsor.isSelected());
        FilterUtils.mapField(map, "is_presential_sponsor", filterPresentialSponsor.isSelected());
        FilterUtils.mapField(map, "is_volunteer", filterVolunteer.isSelected());
        return map;
    }

}
