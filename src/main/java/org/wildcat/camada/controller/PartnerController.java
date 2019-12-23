package org.wildcat.camada.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
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
import org.wildcat.camada.common.validator.impl.AmountValidatorImpl;
import org.wildcat.camada.common.validator.impl.DateValidatorImpl;
import org.wildcat.camada.common.validator.impl.DniValidatorImpl;
import org.wildcat.camada.common.validator.impl.EmailValidatorImpl;
import org.wildcat.camada.common.validator.impl.IbanValidatorImpl;
import org.wildcat.camada.common.validator.impl.NewUserValidatorImpl;
import org.wildcat.camada.common.validator.impl.PaymentFrequencyValidatorImpl;
import org.wildcat.camada.common.validator.impl.TextValidatorImpl;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.controller.pojo.AppTableColumn;
import org.wildcat.camada.persistence.PaymentFrequency;
import org.wildcat.camada.persistence.dto.PartnerDTO;
import org.wildcat.camada.persistence.entity.BankingData;
import org.wildcat.camada.persistence.entity.CustomQuery;
import org.wildcat.camada.persistence.entity.Partner;
import org.wildcat.camada.persistence.entity.PersonalData;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.CustomQueryService;
import org.wildcat.camada.service.PartnerService;
import org.wildcat.camada.service.TableCommonService;
import org.wildcat.camada.service.utils.CsvUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class PartnerController extends BaseController<Partner, PartnerDTO> {

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final TableCommonService<PartnerDTO> tableCommonService;

    @Resource
    private final PartnerService partnerService;

    @FXML
    private TableView<PartnerDTO> table;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableColumn<PartnerDTO, String> name;

    @FXML
    private TableColumn<PartnerDTO, String> surnames;

    @FXML
    private TableColumn<PartnerDTO, Date> birthDate;

    @FXML
    private TableColumn<PartnerDTO, Date> subscribedFrom;

    @FXML
    private TableColumn<PartnerDTO, Date> subscribedTo;

    @FXML
    private TableColumn<PartnerDTO, String> dni;

    @FXML
    private TableColumn<PartnerDTO, String> address;

    @FXML
    private TableColumn<PartnerDTO, String> postCode;

    @FXML
    private TableColumn<PartnerDTO, String> location;

    @FXML
    private TableColumn<PartnerDTO, String> province;

    @FXML
    private TableColumn<PartnerDTO, String> phone1;

    @FXML
    private TableColumn<PartnerDTO, String> phone2;

    @FXML
    private TableColumn<PartnerDTO, String> email;

    @FXML
    private TableColumn<PartnerDTO, String> iban;

    @FXML
    private TableColumn<PartnerDTO, String> bankName;

    @FXML
    private TableColumn<PartnerDTO, String> bankSurnames;

    @FXML
    private TableColumn<PartnerDTO, String> amount;

    @FXML
    private TableColumn<PartnerDTO, String> paymentFrequency;

    @FXML
    private Button nextPaymentsButton;

    public PartnerController(CamadaUserService camadaUserService, PartnerService partnerService,
            CustomQueryService customQueryService, TableCommonService<PartnerDTO> tableCommonService) {
        super(customQueryService, FxmlView.NEW_PARTNER);
        this.camadaUserService = camadaUserService;
        this.partnerService = partnerService;
        this.tableCommonService = tableCommonService;
    }

    @Override
    void initTable() {
        AppTableColumn<PartnerDTO, String> userNameColumn = new AppTableColumn<>(name, "name", new NewUserValidatorImpl(camadaUserService),
                CustomTableColumn.PARTNER_NAME);
        tableCommonService.initTextFieldTableCell(userNameColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> firstNameColumn = new AppTableColumn<>(surnames, "surnames", new TextValidatorImpl(3, 50),
                CustomTableColumn.PARTNER_SURNAMES);
        tableCommonService.initTextFieldTableCell(firstNameColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> dniColumn = new AppTableColumn<>(dni, "dni", new DniValidatorImpl(), CustomTableColumn.PARTNER_DNI);
        tableCommonService.initTextFieldTableCell(dniColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> addressColumn = new AppTableColumn<>(address, "address", new TextValidatorImpl(3, 50), CustomTableColumn.PARTNER_ADDRESS);
        tableCommonService.initTextFieldTableCell(addressColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> postCodeColumn = new AppTableColumn<>(postCode, "postCode", new TextValidatorImpl(5, 6), CustomTableColumn.PARTNER_POST_CODE);
        tableCommonService.initTextFieldTableCell(postCodeColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> locationColumn = new AppTableColumn<>(location, "location", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_LOCATION);
        tableCommonService.initTextFieldTableCell(locationColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> provinceColumn = new AppTableColumn<>(province, "province", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_PROVINCE);
        tableCommonService.initTextFieldTableCell(provinceColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> phone1Column = new AppTableColumn<>(phone1, "phone1", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_PHONE1);
        tableCommonService.initTextFieldTableCell(phone1Column, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> phone2Column = new AppTableColumn<>(phone2, "phone2", new TextValidatorImpl(3, 20), CustomTableColumn.PARTNER_PHONE2);
        tableCommonService.initTextFieldTableCell(phone2Column, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> emailColumn = new AppTableColumn<>(email, "email", new EmailValidatorImpl(), CustomTableColumn.PARTNER_EMAIL);
        tableCommonService.initTextFieldTableCell(emailColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> ibanColumn = new AppTableColumn<>(iban, "iban", new IbanValidatorImpl(), CustomTableColumn.PARTNER_IBAN);
        tableCommonService.initTextFieldTableCell(ibanColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> bankNameColumn = new AppTableColumn<>(bankName, "bankName", new TextValidatorImpl(3, 20),
                CustomTableColumn.PARTNER_BANK_NAME);
        tableCommonService.initTextFieldTableCell(bankNameColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> surnamesBankColumn = new AppTableColumn<>(bankSurnames, "bankSurnames", new TextValidatorImpl(3, 20),
                CustomTableColumn.PARTNER_BANK_SURNAMES);
        tableCommonService.initTextFieldTableCell(surnamesBankColumn, table, progressIndicator, partnerService);

        AppTableColumn<PartnerDTO, String> amountColumn = new AppTableColumn<>(amount, "amount", new AmountValidatorImpl(3.0, 6.0, 9.0, 12.0),
                CustomTableColumn.PARTNER_AMOUNT);
        tableCommonService.initTextFieldTableCell(amountColumn, table, progressIndicator, partnerService);
        AppTableColumn<PartnerDTO, String> paymentFrequencyColumn = new AppTableColumn<>(paymentFrequency, "paymentFrequency", new PaymentFrequencyValidatorImpl(),
                CustomTableColumn.PARTNER_PAYMENT_FREQUENCY);
        tableCommonService.initPaymentFrequencyFieldTableCell(paymentFrequencyColumn, table, progressIndicator, partnerService);

        AppTableColumn<PartnerDTO, Date> birthDateColumn = new AppTableColumn<>(birthDate, "birthDate", new DateValidatorImpl(), CustomTableColumn.PARTNER_BIRTHDATE);
        tableCommonService.initCalendarTextFieldTableCell(birthDateColumn, table, progressIndicator, partnerService);

        subscribedFrom.setCellValueFactory(new PropertyValueFactory<>("subscribedFrom"));
        subscribedFrom.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
        subscribedTo.setCellValueFactory(new PropertyValueFactory<>("subscribedTo"));
        subscribedTo.setCellFactory(column -> tableCommonService.getDateTableCell("dd/MM/yyyy"));
    }

    @Override
    void setTableItems(Task<ObservableList<PartnerDTO>> task) {
        FilteredList<PartnerDTO> filteredData = new FilteredList<>(tableData, p -> true);
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
            SortedList<PartnerDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(FXCollections.observableList(sortedData));
            table.refresh();
        });
        SortedList<PartnerDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(FXCollections.observableList(filteredData));
        table.refresh();
    }

    @Override
    List<PartnerDTO> findAllByCustomQuery(CustomQuery value) {
        return partnerService.findAllByCustomQuery(value);
    }

    @Override
    void delete(PartnerDTO item) {
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
            partner.setAmount(csvRecord.get(14));
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
                .map(PartnerDTO::getEmail)
                .collect(Collectors.toList());
    }

    @FXML
    void onNextPaymentsButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar fichero CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now());
        fileChooser.setInitialFileName(formattedDate + "_pagos_socios_camada.csv");
        File file = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
        if (file != null) {
            List<PartnerDTO> nextPaymentsPartners = partnerService.generateNextPayments();
            String[] headers = new String[] {
                    "Nombre", "Apellidos", "Nombre del titular", "Apellidos del titular", "IBAN", "Cantidad"
            };
            List<String[]> nextPaymentPartners = nextPaymentsPartners.stream().map(partner -> new String[] {
                    partner.getName(), partner.getSurnames(), partner.getBankName(), partner.getBankSurnames(), partner.getIban(), partner.getAmount()
            }).collect(Collectors.toList());
            CsvUtils.export(headers, nextPaymentPartners, file.getAbsolutePath());
        }
    }

}

