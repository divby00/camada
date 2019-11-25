package org.wildcat.camada.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.common.enumerations.CustomTableColumn;
import org.wildcat.camada.common.validator.impl.EmailValidatorImpl;
import org.wildcat.camada.common.validator.impl.NewUserValidatorImpl;
import org.wildcat.camada.common.validator.impl.TextValidatorImpl;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.controller.pojo.AppTableColumn;
import org.wildcat.camada.persistence.entity.*;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.CustomQueryService;
import org.wildcat.camada.service.PartnerService;
import org.wildcat.camada.service.TableCommonService;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Controller
public class PartnerController extends BaseController<Partner> {

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final TableCommonService<Partner> tableCommonService;

    @Resource
    private final PartnerService partnerService;

    @FXML
    private TableView<Partner> table;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableColumn<Partner, String> name;

    @FXML
    private TableColumn<Partner, String> surnames;

    @FXML
    private TableColumn<Partner, Date> birthDate;

    @FXML
    private TableColumn<Partner, String> dni;

    @FXML
    private TableColumn<Partner, String> address;

    @FXML
    private TableColumn<Partner, String> postCode;

    @FXML
    private TableColumn<Partner, String> location;

    @FXML
    private TableColumn<Partner, String> province;

    @FXML
    private TableColumn<Partner, String> phone1;

    @FXML
    private TableColumn<Partner, String> phone2;

    @FXML
    private TableColumn<Partner, String> email;

    @FXML
    private TableColumn<Partner, String> iban;

    @FXML
    private TableColumn<Partner, String> bankName;

    @FXML
    private TableColumn<Partner, String> bankSurnames;

    public PartnerController(CamadaUserService camadaUserService, PartnerService partnerService,
                             CustomQueryService customQueryService, TableCommonService<Partner> tableCommonService) {
        super(customQueryService, FxmlView.NEW_PARTNER);
        this.camadaUserService = camadaUserService;
        this.partnerService = partnerService;
        this.tableCommonService = tableCommonService;
    }

    @Override
    void initTable() {
        AppTableColumn<Partner> userNameColumn = new AppTableColumn<>(name, "name", new NewUserValidatorImpl(camadaUserService), CustomTableColumn.NAME);
        tableCommonService.initTextFieldTableCell(userNameColumn, table, progressIndicator);
        AppTableColumn<Partner> firstNameColumn = new AppTableColumn<>(surnames, "surnames", new TextValidatorImpl(3, 20), CustomTableColumn.FIRST_NAME);
        tableCommonService.initTextFieldTableCell(firstNameColumn, table, progressIndicator);
        AppTableColumn<Partner> dniColumn = new AppTableColumn<>(dni, "dni", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(dniColumn, table, progressIndicator);
        AppTableColumn<Partner> addressColumn = new AppTableColumn<>(address, "address", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(addressColumn, table, progressIndicator);
        AppTableColumn<Partner> postCodeColumn = new AppTableColumn<>(postCode, "postCode", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(postCodeColumn, table, progressIndicator);
        AppTableColumn<Partner> locationColumn = new AppTableColumn<>(location, "location", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(locationColumn, table, progressIndicator);
        AppTableColumn<Partner> provinceColumn = new AppTableColumn<>(province, "province", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(provinceColumn, table, progressIndicator);

        // TODO: Add phone validation
        AppTableColumn<Partner> phone1Column = new AppTableColumn<>(phone1, "phone1", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(phone1Column, table, progressIndicator);
        AppTableColumn<Partner> phone2Column = new AppTableColumn<>(phone2, "phone2", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(phone2Column, table, progressIndicator);
        AppTableColumn<Partner> emailColumn = new AppTableColumn<>(email, "email", new EmailValidatorImpl(), CustomTableColumn.EMAIL);
        tableCommonService.initTextFieldTableCell(emailColumn, table, progressIndicator);

        // TODO: Add IBAN validator
        AppTableColumn<Partner> ibanColumn = new AppTableColumn<>(iban, "IBAN", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(ibanColumn, table, progressIndicator);
        AppTableColumn<Partner> bankNameColumn = new AppTableColumn<>(bankName, "bankName", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(bankNameColumn, table, progressIndicator);
        AppTableColumn<Partner> surnamesBankColumn = new AppTableColumn<>(bankSurnames, "bankSurnames", new TextValidatorImpl(3, 20), CustomTableColumn.LAST_NAME);
        tableCommonService.initTextFieldTableCell(surnamesBankColumn, table, progressIndicator);

        birthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        birthDate.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
    }

    @Override
    void setTableItems(Task<ObservableList<Partner>> task) {
        FilteredList<Partner> filteredData = new FilteredList<>(tableData, p -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(partner -> {
                PersonalData personalData = partner.getPersonalData();
                BankingData bankingData = partner.getBankingData();
                return newValue == null || newValue.isEmpty()
                        || StringUtils.containsIgnoreCase(personalData.getName(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getSurnames(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getDni(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getAddress(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getLocation(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getProvince(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getPhone1(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getPhone2(), newValue)
                        || StringUtils.containsIgnoreCase(personalData.getEmail(), newValue)
                        || StringUtils.containsIgnoreCase(bankingData.getIban(), newValue)
                        || StringUtils.containsIgnoreCase(bankingData.getName(), newValue)
                        || StringUtils.containsIgnoreCase(bankingData.getSurnames(), newValue);
            });
            SortedList<Partner> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(FXCollections.observableList(sortedData));
            table.refresh();
        });
        SortedList<Partner> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(FXCollections.observableList(filteredData));
        table.refresh();
    }

    @Override
    List<Partner> findAllByCustomQuery(CustomQuery value) {
        return partnerService.findAllByCustomQuery(value);
    }

    @Override
    void delete(Partner item) {

    }

    @Override
    void save(Partner item) {

    }

    @Override
    Partner buildEntityFromCsvRecord(CSVRecord csvRecord) {
        return null;
    }

    @Override
    Pair<Boolean, List<String>> validateDatabaseEntities(File file, int key) {
        return null;
    }

    @Override
    List<String> getEmails() {
        return null;
    }

}
