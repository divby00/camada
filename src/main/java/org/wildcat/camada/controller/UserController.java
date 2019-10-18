package org.wildcat.camada.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.entity.CustomQuery;
import org.wildcat.camada.enumerations.CustomTableColumn;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.CustomQueryService;
import org.wildcat.camada.service.TableCommonService;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.List;
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
    private TextField filter;

    @FXML
    private ComboBox<CustomQuery> customQueries;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final TableCommonService tableCommonService;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final CustomQueryService customQueryService;

    public UserController(TableCommonService tableCommonService, CamadaUserService camadaUserService,
                          CustomQueryService customQueryService) {
        this.tableCommonService = tableCommonService;
        this.camadaUserService = camadaUserService;
        this.customQueryService = customQueryService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Long userId = camadaUserService.getUser().getId();
        List<CustomQuery> queries = customQueryService.findAllBySection(FxmlView.USER);
        customQueries.setItems(FXCollections.observableList(queries));
        Callback<ListView<CustomQuery>, ListCell<CustomQuery>> comboCellFactory = new Callback<ListView<CustomQuery>, ListCell<CustomQuery>>() {
            @Override
            public ListCell<CustomQuery> call(ListView<CustomQuery> l) {
                return new ListCell<CustomQuery>() {
                    @Override
                    protected void updateItem(CustomQuery item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getDescription());
                        }
                    }
                };
            }
        };
        customQueries.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Populate table & prepare filtering
            table.setItems(null);
            Iterable<CamadaUser> camadaUsers = camadaUserService.findAllByCustomQuery(newVal);
            ObservableList<CamadaUser> tableData = FXCollections.observableList((List<CamadaUser>) camadaUsers);
            FilteredList<CamadaUser> filteredData = new FilteredList<>(tableData, p -> true);
            filter.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(user -> {
                    return newValue == null || newValue.isEmpty()
                            || StringUtils.containsIgnoreCase(user.getName(), newValue)
                            || StringUtils.containsIgnoreCase(user.getFirstName(), newValue)
                            || StringUtils.containsIgnoreCase(user.getLastName(), newValue)
                            || StringUtils.containsIgnoreCase(user.getEmail(), newValue);
                });
            });
            SortedList<CamadaUser> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedData);
        });
        customQueries.setButtonCell(comboCellFactory.call(null));
        customQueries.setCellFactory(comboCellFactory);

        // Init table
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

    public void goToHome(ActionEvent event) {
        stageManager.switchScene(FxmlView.DASHBOARD);
    }

}
