package org.wildcat.camada.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
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
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Controller
public class UserController extends BaseGridController<CamadaUser> {

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

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final TableCommonService tableCommonService;

    @Resource
    private final CamadaUserService camadaUserService;

    public UserController(TableCommonService tableCommonService, CamadaUserService camadaUserService,
            CustomQueryService customQueryService) {
        super(customQueryService);
        this.tableCommonService = tableCommonService;
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initTable() {
        tableCommonService.initTextFieldTableCell(userName, "name", CustomTableColumn.NAME, table, progressIndicator);
        tableCommonService.initTextFieldTableCell(firstName, "firstName", CustomTableColumn.FIRST_NAME, table, progressIndicator);
        tableCommonService.initTextFieldTableCell(lastName, "lastName", CustomTableColumn.LAST_NAME, table, progressIndicator);
        tableCommonService.initTextFieldTableCell(email, "email", CustomTableColumn.EMAIL, table, progressIndicator);
        tableCommonService.initCheckBoxTableCell(isAdmin, CustomTableColumn.IS_ADMIN, table, progressIndicator);
        tableCommonService.initCheckBoxTableCell(isVirtualSponsor, CustomTableColumn.IS_VIRTUAL_SPONSOR, table, progressIndicator);
        tableCommonService.initCheckBoxTableCell(isPresentialSponsor, CustomTableColumn.IS_PRESENTIAL_SPONSOR, table, progressIndicator);
        tableCommonService.initCheckBoxTableCell(isPartner, CustomTableColumn.IS_PARTNER, table, progressIndicator);
        tableCommonService.initCheckBoxTableCell(isVolunteer, CustomTableColumn.IS_VOLUNTEER, table, progressIndicator);
        activationDate.setCellValueFactory(new PropertyValueFactory<>("activationDate"));
        lastConnection.setCellValueFactory(new PropertyValueFactory<>("lastConnection"));
        activationDate.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
        lastConnection.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public void setTableItems(Task<ObservableList<CamadaUser>> task) {
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
            table.refresh();
        });
        SortedList<CamadaUser> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(FXCollections.observableList(filteredData));
        table.refresh();
    }

    @Override
    List<CamadaUser> findAllByCustomQuery(CustomQuery value) {
        return camadaUserService.findAllByCustomQuery(value);
    }

    @Override
    void delete(CamadaUser item) {
        camadaUserService.delete(item);
    }

    public void onHomeButtonAction(ActionEvent event) {
        stageManager.switchScene(FxmlView.DASHBOARD);
    }

    public void onExportCsvButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar fichero CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("*.csv");
        File file = fileChooser.showSaveDialog(this.stageManager.getPrimaryStage());
        if (file != null) {
            CsvUtils.export(table, file.getAbsolutePath());
        }
    }

    public void onExportPdfButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar fichero PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("*.pdf");
        File file = fileChooser.showSaveDialog(this.stageManager.getPrimaryStage());
        if (file != null) {
            ObservableList<CamadaUser> items = table.getItems();
            Task<Boolean> pdfTask = new Task<Boolean>() {
                @Override
                protected Boolean call() {
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
        this.stageManager.switchScene(FxmlView.NEW_USER);
    }

}
