package org.wildcat.camada.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.common.validator.impl.EmailValidatorImpl;
import org.wildcat.camada.common.validator.impl.NewUserValidatorImpl;
import org.wildcat.camada.common.validator.impl.TextValidatorImpl;
import org.wildcat.camada.controller.pojo.AppTableColumn;
import org.wildcat.camada.controller.pojo.EmailData;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.CustomQueryService;
import org.wildcat.camada.service.TableCommonService;
import org.wildcat.camada.service.picture.PictureService;
import org.wildcat.camada.service.utils.CsvUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class UserController extends BaseController<CamadaUser, CamadaUser> {

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
    private TableColumn<CamadaUser, Boolean> isActive;

    @FXML
    private TableColumn<CamadaUser, Date> activationDate;

    @FXML
    private TableColumn<CamadaUser, Date> lastConnection;

    @FXML
    private TextField searchTextField;

    @FXML
    private ProgressIndicator progressIndicator;

    @Resource
    private final TableCommonService<CamadaUser> tableCommonService;

    @Resource
    private final CamadaUserService camadaUserService;

    public UserController(TableCommonService<CamadaUser> tableCommonService, PictureService pictureService, CamadaUserService camadaUserService,
                          CustomQueryService customQueryService) {
        super(customQueryService, pictureService, FxmlView.NEW_USER);
        this.tableCommonService = tableCommonService;
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initTable() {
        AppTableColumn<CamadaUser, String> userNameColumn = new AppTableColumn<>(userName, "name", new NewUserValidatorImpl(camadaUserService), CustomTableColumn.NAME);
        tableCommonService.initTextFieldTableCell(userNameColumn, table, progressIndicator, camadaUserService);
        AppTableColumn<CamadaUser, String> firstNameColumn = new AppTableColumn<>(firstName, "firstName", new TextValidatorImpl(3, 20), CustomTableColumn.FIRST_NAME);
        tableCommonService.initTextFieldTableCell(firstNameColumn, table, progressIndicator, camadaUserService);
        AppTableColumn<CamadaUser, String> lastNameColumn = new AppTableColumn<>(lastName, "lastName", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(lastNameColumn, table, progressIndicator, camadaUserService);
        AppTableColumn<CamadaUser, String> emailColumn = new AppTableColumn<>(email, "email", new EmailValidatorImpl(), CustomTableColumn.EMAIL);
        tableCommonService.initTextFieldTableCell(emailColumn, table, progressIndicator, camadaUserService);

        tableCommonService.initCheckBoxTableCell(isAdmin, CustomTableColumn.IS_ADMIN, table, progressIndicator, this, camadaUserService);
        tableCommonService.initCheckBoxTableCell(isVirtualSponsor, CustomTableColumn.IS_VIRTUAL_SPONSOR, table, progressIndicator, this, camadaUserService);
        tableCommonService.initCheckBoxTableCell(isPresentialSponsor, CustomTableColumn.IS_PRESENTIAL_SPONSOR, table, progressIndicator, this, camadaUserService);
        tableCommonService.initCheckBoxTableCell(isPartner, CustomTableColumn.IS_PARTNER, table, progressIndicator, this, camadaUserService);
        tableCommonService.initCheckBoxTableCell(isVolunteer, CustomTableColumn.IS_VOLUNTEER, table, progressIndicator, this, camadaUserService);
        tableCommonService.initCheckBoxTableCell(isActive, CustomTableColumn.IS_ACTIVE, table, progressIndicator, this, camadaUserService);

        activationDate.setCellValueFactory(new PropertyValueFactory<>("activationDate"));
        lastConnection.setCellValueFactory(new PropertyValueFactory<>("lastConnection"));
        activationDate.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
        lastConnection.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public void setTableItems(Task<ObservableList<CamadaUser>> task) {
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

    @Override
    void save(CamadaUser item) {
        camadaUserService.save(item);
    }

    @Override
    public CamadaUser buildEntityFromCsvRecord(CSVRecord csvRecord) {
        CamadaUser user = new CamadaUser();
        try {
            user.setName(csvRecord.get(0));
            user.setFirstName(csvRecord.get(1));
            user.setLastName(csvRecord.get(2));
            user.setEmail(csvRecord.get(3));
            user.setIsAdmin(CsvUtils.getBooleanFromTranslatedValue(csvRecord.get(4)));
            user.setIsPartner(CsvUtils.getBooleanFromTranslatedValue(csvRecord.get(5)));
            user.setIsPresentialSponsor(CsvUtils.getBooleanFromTranslatedValue(csvRecord.get(6)));
            user.setIsVirtualSponsor(CsvUtils.getBooleanFromTranslatedValue(csvRecord.get(7)));
            user.setIsVolunteer(CsvUtils.getBooleanFromTranslatedValue(csvRecord.get(8)));
            user.setIsActive(CsvUtils.getBooleanFromTranslatedValue(csvRecord.get(9)));
            user.setActivationDate(CsvUtils.getDateFromTranslatedValue(csvRecord.get(10)));
            user.setLastConnection(CsvUtils.getDateFromTranslatedValue(csvRecord.get(11)));
        } catch (Exception ex) {
            log.warn(ExceptionUtils.getStackTrace(ex));
        }
        return user;
    }

    @Override
    public Pair<Boolean, List<String>> validateDatabaseEntities(File file, int key) {
        List<String> errors = new LinkedList<>();
        List<String> entities = new LinkedList<>();
        for (CSVRecord csvRecord : CsvUtils.getCsvParser(file)) {
            entities.add(csvRecord.get(key));
        }
        List<CamadaUser> dbEntities = camadaUserService.findAllByName(entities);
        for (CamadaUser entity : dbEntities) {
            errors.add("La entrada " + entity + " ya existe en la base de datos.");
        }
        return Pair.of(errors.size() == 0 && entities.size() > 0, errors);
    }


    @Override
    public EmailData getEmailsData() {
        List<String> emails = table.getItems().stream()
                .map(CamadaUser::getEmail)
                .collect(Collectors.toList());
        List<String> placeholders = table.getColumns().stream()
                .filter(TableColumnBase::isVisible)
                .map(TableColumnBase::getText)
                .collect(Collectors.toList());
        return EmailData.builder().emails(emails).placeholders(placeholders).build();
    }

}
