package org.wildcat.camada.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
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
import org.wildcat.camada.utils.AlertUtils;
import org.wildcat.camada.utils.CsvUtils;
import org.wildcat.camada.utils.PdfUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
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
    private TextField searchTextField;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ComboBox<CustomQuery> customQueriesComboBox;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final NewUserController newUserController;

    @Resource
    private final TableCommonService tableCommonService;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final CustomQueryService customQueryService;

    private ObservableList<CamadaUser> tableData;

    public UserController(NewUserController newUserController, TableCommonService tableCommonService,
                          CamadaUserService camadaUserService, CustomQueryService customQueryService) {
        this.newUserController = newUserController;
        this.tableCommonService = tableCommonService;
        this.camadaUserService = camadaUserService;
        this.customQueryService = customQueryService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Task<ObservableList<CustomQuery>> taskCustomQueries = new Task<ObservableList<CustomQuery>>() {
            @Override
            protected ObservableList<CustomQuery> call() throws Exception {
                List<CustomQuery> queries = customQueryService.findAllBySection(FxmlView.USER);
                return FXCollections.observableList(queries);
            }
        };
        progressIndicator.visibleProperty().bind(taskCustomQueries.runningProperty());
        new Thread(taskCustomQueries).start();

        taskCustomQueries.setOnSucceeded(workerStateEvent -> {
            ObservableList<CustomQuery> queries = taskCustomQueries.getValue();
            customQueriesComboBox.setItems(queries);
            initCustomQueriesCombo();

            Task<ObservableList<CamadaUser>> taskCamadaUsers = getObservableListTask(customQueriesComboBox.getValue());
            progressIndicator.visibleProperty().bind(taskCamadaUsers.runningProperty());
            new Thread(taskCamadaUsers).start();
            taskCamadaUsers.setOnSucceeded(workerState -> {
                setTableItems(taskCamadaUsers);
            });

            customQueriesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                Task<ObservableList<CamadaUser>> taskCamadaUsersListener = getObservableListTask(newVal);
                progressIndicator.visibleProperty().bind(taskCamadaUsersListener.runningProperty());
                new Thread(taskCamadaUsersListener).start();
                taskCamadaUsersListener.setOnSucceeded(workerState -> {
                    setTableItems(taskCamadaUsersListener);
                });
            });
        });

        initTable();
    }

    public void onHomeButtonAction(ActionEvent event) {
        stageManager.switchScene(FxmlView.DASHBOARD);
    }

    public void onExportCsvButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar fichero CSV");
        File file = fileChooser.showSaveDialog(this.stageManager.getPrimaryStage());
        if (file != null) {
            ObservableList<CamadaUser> items = table.getItems();
            String[] csvHeader = {
                    "id", "nombreUsuario", "nombre", "apellido", "email", "administrador",
                    "padrinoVirtual", "padrinoPresencial", "socio", "voluntario", "fechaActivacion",
                    "ultimaConexion", "activo"
            };
            CsvUtils.export(items, csvHeader, file.getAbsolutePath());
        }
    }

    public void onExportPdfButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar fichero CSV");
        File file = fileChooser.showSaveDialog(this.stageManager.getPrimaryStage());
        if (file != null) {
            ObservableList<CamadaUser> items = table.getItems();
            Task<Boolean> pdfTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return PdfUtils.export(items, file.getAbsolutePath());
                }
            };
            progressIndicator.visibleProperty().bind(pdfTask.runningProperty());
            new Thread(pdfTask).start();
            pdfTask.setOnSucceeded(workerState -> {
                if (pdfTask.getValue()) {
                    AlertUtils.showInfo(MessageFormat.format("El fichero {0} se ha guardado correctamente.", file.getName()));
                } else {
                    AlertUtils.showError(MessageFormat.format("Ha habido un problema al guardar el fichero {0}.", file.getName()));
                }
            });
            pdfTask.setOnFailed(workerState -> {
                AlertUtils.showError(MessageFormat.format("Ha habido un problema al guardar el fichero {0}.", file.getName()));
            });
        }
    }

    public void onNewButtonAction(ActionEvent event) {
        newUserController.setParentData(tableData, table);
        this.stageManager.switchScene(FxmlView.NEW_USER);
    }

    private void initCustomQueriesCombo() {
        Callback<ListView<CustomQuery>, ListCell<CustomQuery>> comboCellFactory = getComboCellFactory();
        customQueriesComboBox.setCellFactory(comboCellFactory);
        customQueriesComboBox.setButtonCell(comboCellFactory.call(null));
        customQueriesComboBox.getSelectionModel().selectFirst();
    }

    private void initTable() {
        tableCommonService.initTextFieldTableCell(userName, "name", CustomTableColumn.NAME, table);
        tableCommonService.initTextFieldTableCell(firstName, "firstName", CustomTableColumn.FIRST_NAME, table);
        tableCommonService.initTextFieldTableCell(lastName, "lastName", CustomTableColumn.LAST_NAME, table);
        tableCommonService.initTextFieldTableCell(email, "email", CustomTableColumn.EMAIL, table);
        tableCommonService.initCheckBoxTableCell(isAdmin, CustomTableColumn.IS_ADMIN, table);
        tableCommonService.initCheckBoxTableCell(isVirtualSponsor, CustomTableColumn.IS_VIRTUAL_SPONSOR, table);
        tableCommonService.initCheckBoxTableCell(isPresentialSponsor, CustomTableColumn.IS_PRESENTIAL_SPONSOR, table);
        tableCommonService.initCheckBoxTableCell(isPartner, CustomTableColumn.IS_PARTNER, table);
        tableCommonService.initCheckBoxTableCell(isVolunteer, CustomTableColumn.IS_VOLUNTEER, table);
        activationDate.setCellValueFactory(new PropertyValueFactory<>("activationDate"));
        lastConnection.setCellValueFactory(new PropertyValueFactory<>("lastConnection"));
        activationDate.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
        lastConnection.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy HH:mm:ss"));
        addContextMenu();
    }

    private Callback<ListView<CustomQuery>, ListCell<CustomQuery>> getComboCellFactory() {
        return new Callback<ListView<CustomQuery>, ListCell<CustomQuery>>() {
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
    }

    private void setTableItems(Task<ObservableList<CamadaUser>> task) {
        tableData = task.getValue();
        FilteredList<CamadaUser> filteredData = new FilteredList<>(tableData, p -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                return newValue == null || newValue.isEmpty()
                        || StringUtils.containsIgnoreCase(user.getName(), newValue)
                        || StringUtils.containsIgnoreCase(user.getFirstName(), newValue)
                        || StringUtils.containsIgnoreCase(user.getLastName(), newValue)
                        || StringUtils.containsIgnoreCase(user.getEmail(), newValue);
            });
            SortedList<CamadaUser> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(FXCollections.observableList(sortedData));
        });
        SortedList<CamadaUser> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(FXCollections.observableList(filteredData));
    }

    private Task<ObservableList<CamadaUser>> getObservableListTask(CustomQuery value) {
        return new CamadaUserTask<ObservableList<CamadaUser>>(value) {
            @Override
            protected ObservableList<CamadaUser> call() throws Exception {
                List<CamadaUser> allByCustomQuery = camadaUserService.findAllByCustomQuery(value);
                return FXCollections.observableList(allByCustomQuery);
            }
        };
    }

    private void addContextMenu() {
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
                ButtonType buttonType = AlertUtils.showConfirmation("¿Quieres borrar el usuario?");
                if (buttonType == ButtonType.YES) {
                    CamadaUser user = row.getItem();
                    camadaUserService.delete(user);
                    tableData.remove(user);
                    table.refresh();
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

    abstract static class CamadaUserTask<V> extends Task<V> {
        CustomQuery customQuery;
        CamadaUserTask(CustomQuery customQuery) {
            this.customQuery = customQuery;
        }
    }

}
