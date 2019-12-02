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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import org.wildcat.camada.persistence.PaymentFrequency;
import org.wildcat.camada.persistence.dto.PartnerView;
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
import java.util.stream.Collectors;

@Slf4j
@Controller
public class PartnerController extends BaseController<Partner, PartnerView> {

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final TableCommonService<PartnerView> tableCommonService;

    @Resource
    private final PartnerService partnerService;

    @FXML
    private TableView<PartnerView> table;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableColumn<PartnerView, String> name;

    @FXML
    private TableColumn<PartnerView, String> surnames;

    @FXML
    private TableColumn<PartnerView, Date> birthDate;

    @FXML
    private TableColumn<PartnerView, String> dni;

    @FXML
    private TableColumn<PartnerView, String> address;

    @FXML
    private TableColumn<PartnerView, String> postCode;

    @FXML
    private TableColumn<PartnerView, String> location;

    @FXML
    private TableColumn<PartnerView, String> province;

    @FXML
    private TableColumn<PartnerView, String> phone1;

    @FXML
    private TableColumn<PartnerView, String> phone2;

    @FXML
    private TableColumn<PartnerView, String> email;

    @FXML
    private TableColumn<PartnerView, String> iban;

    @FXML
    private TableColumn<PartnerView, String> bankName;

    @FXML
    private TableColumn<PartnerView, String> bankSurnames;

    public PartnerController(CamadaUserService camadaUserService, PartnerService partnerService,
                             CustomQueryService customQueryService, TableCommonService<PartnerView> tableCommonService) {
        super(customQueryService, FxmlView.NEW_PARTNER);
        this.camadaUserService = camadaUserService;
        this.partnerService = partnerService;
        this.tableCommonService = tableCommonService;
    }

    @Override
    void initTable() {
        AppTableColumn<PartnerView> userNameColumn = new AppTableColumn<>(name, "name", new NewUserValidatorImpl(camadaUserService), CustomTableColumn.PARTNER_NAME);
        tableCommonService.initTextFieldTableCell(userNameColumn, table, progressIndicator);
        AppTableColumn<PartnerView> firstNameColumn = new AppTableColumn<>(surnames, "surnames", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_SURNAMES);
        tableCommonService.initTextFieldTableCell(firstNameColumn, table, progressIndicator);
        AppTableColumn<PartnerView> dniColumn = new AppTableColumn<>(dni, "dni", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_DNI);
        tableCommonService.initTextFieldTableCell(dniColumn, table, progressIndicator);
        AppTableColumn<PartnerView> addressColumn = new AppTableColumn<>(address, "address", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_ADDRESS);
        tableCommonService.initTextFieldTableCell(addressColumn, table, progressIndicator);
        AppTableColumn<PartnerView> postCodeColumn = new AppTableColumn<>(postCode, "postCode", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_POST_CODE);
        tableCommonService.initTextFieldTableCell(postCodeColumn, table, progressIndicator);
        AppTableColumn<PartnerView> locationColumn = new AppTableColumn<>(location, "location", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_LOCATION);
        tableCommonService.initTextFieldTableCell(locationColumn, table, progressIndicator);
        AppTableColumn<PartnerView> provinceColumn = new AppTableColumn<>(province, "province", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_PROVINCE);
        tableCommonService.initTextFieldTableCell(provinceColumn, table, progressIndicator);

        // TODO: Add phone validation
        AppTableColumn<PartnerView> phone1Column = new AppTableColumn<>(phone1, "phone1", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_PHONE1);
        tableCommonService.initTextFieldTableCell(phone1Column, table, progressIndicator);
        AppTableColumn<PartnerView> phone2Column = new AppTableColumn<>(phone2, "phone2", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_PHONE2);
        tableCommonService.initTextFieldTableCell(phone2Column, table, progressIndicator);
        AppTableColumn<PartnerView> emailColumn = new AppTableColumn<>(email, "email", new EmailValidatorImpl(), CustomTableColumn.PARTNER_EMAIL);
        tableCommonService.initTextFieldTableCell(emailColumn, table, progressIndicator);

        // TODO: Add IBAN validator
        AppTableColumn<PartnerView> ibanColumn = new AppTableColumn<>(iban, "iban", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_IBAN);
        tableCommonService.initTextFieldTableCell(ibanColumn, table, progressIndicator);
        AppTableColumn<PartnerView> bankNameColumn = new AppTableColumn<>(bankName, "bankName", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_BANK_NAME);
        tableCommonService.initTextFieldTableCell(bankNameColumn, table, progressIndicator);
        AppTableColumn<PartnerView> surnamesBankColumn = new AppTableColumn<>(bankSurnames, "bankSurnames", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_BANK_SURNAMES);
        tableCommonService.initTextFieldTableCell(surnamesBankColumn, table, progressIndicator);

        birthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        birthDate.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
    }

    @Override
    void setTableItems(Task<ObservableList<PartnerView>> task) {
        FilteredList<PartnerView> filteredData = new FilteredList<>(tableData, p -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(partnerView -> {
                return newValue == null || newValue.isEmpty()
                        || StringUtils.containsIgnoreCase(partnerView.getName(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getSurnames(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getDni(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getAddress(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getLocation(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getProvince(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getPhone1(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getPhone2(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getEmail(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getIban(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getBankName(), newValue)
                        || StringUtils.containsIgnoreCase(partnerView.getBankSurnames(), newValue);
            });
            SortedList<PartnerView> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(FXCollections.observableList(sortedData));
            table.refresh();
        });
        SortedList<PartnerView> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(FXCollections.observableList(filteredData));
        table.refresh();
    }

    @Override
    List<PartnerView> findAllByCustomQuery(CustomQuery value) {
        return partnerService.findAllByCustomQuery(value);
    }

    @Override
    void delete(PartnerView item) {
        partnerService.delete(item.getId());
    }

    @Override
    void save(Partner item) {

    }

    @Override
    Partner buildEntityFromCsvRecord(CSVRecord csvRecord) {
        Partner partner = new Partner();
        try {
            PersonalData personalData = new PersonalData();
            personalData.setName(csvRecord.get(0));
            personalData.setSurnames(csvRecord.get(1));
            personalData.setBirthDate(DateUtils.parseDate(csvRecord.get(2)));
            personalData.setDni(csvRecord.get(3));
            personalData.setAddress(csvRecord.get(4));
            personalData.setPostCode(csvRecord.get(5));
            personalData.setLocation(csvRecord.get(6));
            personalData.setProvince(csvRecord.get(7));
            personalData.setPhone1(csvRecord.get(8));
            personalData.setPhone2(csvRecord.get(9));
            personalData.setEmail(csvRecord.get(10));
            BankingData bankingData = new BankingData();
            bankingData.setIban(csvRecord.get(11));
            bankingData.setName(csvRecord.get(12));
            bankingData.setSurnames(csvRecord.get(13));
            partner.setPersonalData(personalData);
            partner.setBankingData(bankingData);
            partner.setAmount(Double.valueOf(csvRecord.get(14)));
            partner.setPaymentFrequency(PaymentFrequency.valueOf(csvRecord.get(15)));
            partner.setCamadaId(csvRecord.get(16));
        } catch (Exception ex) {
            log.warn(ExceptionUtils.getStackTrace(ex));
        }
        return partner;
    }

    @Override
    Pair<Boolean, List<String>> validateDatabaseEntities(File file, int key) {
        return null;
    }

    @Override
    List<String> getEmails() {
        return table.getItems().stream()
                .map(PartnerView::getEmail)
                .collect(Collectors.toList());
    }

}
