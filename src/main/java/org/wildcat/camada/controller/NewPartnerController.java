package org.wildcat.camada.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.wildcat.camada.common.validator.impl.DniValidatorImpl;
import org.wildcat.camada.common.validator.impl.EmailValidatorImpl;
import org.wildcat.camada.common.validator.impl.IbanValidatorImpl;
import org.wildcat.camada.common.validator.impl.PaymentFrequencyValidatorImpl;
import org.wildcat.camada.common.validator.impl.PhoneValidatorImpl;
import org.wildcat.camada.common.validator.impl.TextValidatorImpl;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.persistence.PaymentFrequency;
import org.wildcat.camada.persistence.dto.PartnerDTO;
import org.wildcat.camada.persistence.entity.BankingData;
import org.wildcat.camada.persistence.entity.Partner;
import org.wildcat.camada.persistence.entity.PersonalData;
import org.wildcat.camada.service.PartnerService;
import org.wildcat.camada.service.utils.AlertUtils;
import org.wildcat.camada.service.utils.GetUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class NewPartnerController implements Initializable {

    @FXML
    private TextField inputName;
    @FXML
    private TextField inputSurnames;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField inputDni;
    @FXML
    private TextField inputAddress;
    @FXML
    private TextField inputPostCode;
    @FXML
    private TextField inputLocation;
    @FXML
    private TextField inputProvince;
    @FXML
    private TextField inputPhone1;
    @FXML
    private TextField inputPhone2;
    @FXML
    private TextField inputEmail;
    @FXML
    private TextField inputBankName;
    @FXML
    private TextField inputBankSurnames;
    @FXML
    private TextField inputIban;
    @FXML
    private TextField inputAmount;
    @FXML
    private ComboBox<String> comboPaymentFrequency;

    @FXML
    private ImageView imageName;
    @FXML
    private ImageView imageSurnames;
    @FXML
    private ImageView imageBirthDate;
    @FXML
    private ImageView imageDni;
    @FXML
    private ImageView imageAddress;
    @FXML
    private ImageView imagePostCode;
    @FXML
    private ImageView imageLocation;
    @FXML
    private ImageView imageProvince;
    @FXML
    private ImageView imagePhone1;
    @FXML
    private ImageView imagePhone2;
    @FXML
    private ImageView imageEmail;
    @FXML
    private ImageView imageBankName;
    @FXML
    private ImageView imageBankSurnames;
    @FXML
    private ImageView imageIban;
    @FXML
    private ImageView imageAmount;
    @FXML
    private ImageView imagePaymentFrequency;

    @FXML
    private Button buttonSave;
    @FXML
    private ProgressIndicator progressIndicator;

    @Lazy
    @Autowired
    private StageManager stageManager;

    private ResourceBundle resources;
    private TextValidatorImpl nameValidator;
    private TextValidatorImpl surnamesValidator;
    private DniValidatorImpl dniValidator;
    private TextValidatorImpl addressValidator;
    private TextValidatorImpl postCodeValidator;
    private TextValidatorImpl locationValidator;
    private TextValidatorImpl provinceValidator;
    private EmailValidatorImpl emailValidator;
    private IbanValidatorImpl ibanValidator;
    private PaymentFrequencyValidatorImpl paymentFrequencyValidator;
    private PhoneValidatorImpl phoneValidator;

    @Resource
    private final PartnerService partnerService;

    public NewPartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.imageName.managedProperty().bind(imageName.visibleProperty());
        this.imageSurnames.managedProperty().bind(imageSurnames.visibleProperty());
        this.imageDni.managedProperty().bind(imageDni.visibleProperty());
        this.imageAddress.managedProperty().bind(imageAddress.visibleProperty());
        this.imagePostCode.managedProperty().bind(imagePostCode.visibleProperty());
        this.imageLocation.managedProperty().bind(imageLocation.visibleProperty());
        this.imageProvince.managedProperty().bind(imageProvince.visibleProperty());
        this.imagePhone1.managedProperty().bind(imagePhone1.visibleProperty());
        this.imagePhone2.managedProperty().bind(imagePhone2.visibleProperty());
        this.imageEmail.managedProperty().bind(imageEmail.visibleProperty());
        this.imageBankName.managedProperty().bind(imageBankName.visibleProperty());
        this.imageBankSurnames.managedProperty().bind(imageBankSurnames.visibleProperty());
        this.imageIban.managedProperty().bind(imageIban.visibleProperty());
        this.imageAmount.managedProperty().bind(imageAmount.visibleProperty());
        this.imagePaymentFrequency.managedProperty().bind(imagePaymentFrequency.visibleProperty());
        this.imageBirthDate.managedProperty().bind(imageBirthDate.visibleProperty());

        this.nameValidator = new TextValidatorImpl(3, 20);
        this.surnamesValidator = new TextValidatorImpl(5, 50);
        this.dniValidator = new DniValidatorImpl();
        this.addressValidator = new TextValidatorImpl(5, 100);
        this.postCodeValidator = new TextValidatorImpl(4, 6);
        this.locationValidator = new TextValidatorImpl(3, 200);
        this.provinceValidator = new TextValidatorImpl(3, 30);
        this.emailValidator = new EmailValidatorImpl();
        this.ibanValidator = new IbanValidatorImpl();
        this.paymentFrequencyValidator = new PaymentFrequencyValidatorImpl();
        this.phoneValidator = new PhoneValidatorImpl();
        List<String> comboValues = Stream.of(PaymentFrequency.values()).map(PaymentFrequency::getLabel).collect(Collectors.toList());
        this.comboPaymentFrequency.getItems().setAll(comboValues);
        this.comboPaymentFrequency.getSelectionModel().select(PaymentFrequency.MONTHLY.getLabel());

        Tooltip.install(imageName, new Tooltip(nameValidator.getErrorMessage()));
        Tooltip.install(imageSurnames, new Tooltip(surnamesValidator.getErrorMessage()));
        //Tooltip.install(imageBirthDate, new Tooltip(bir.getErrorMessage()));
        Tooltip.install(imageDni, new Tooltip(dniValidator.getErrorMessage()));
        Tooltip.install(imageAddress, new Tooltip(addressValidator.getErrorMessage()));
        Tooltip.install(imagePostCode, new Tooltip(postCodeValidator.getErrorMessage()));
        Tooltip.install(imageLocation, new Tooltip(locationValidator.getErrorMessage()));
        Tooltip.install(imageProvince, new Tooltip(provinceValidator.getErrorMessage()));
        Tooltip.install(imagePhone1, new Tooltip(phoneValidator.getErrorMessage()));
        Tooltip.install(imagePhone2, new Tooltip(phoneValidator.getErrorMessage()));
        Tooltip.install(imageEmail, new Tooltip(emailValidator.getErrorMessage()));
        Tooltip.install(imageBankName, new Tooltip(nameValidator.getErrorMessage()));
        Tooltip.install(imageBankSurnames, new Tooltip(surnamesValidator.getErrorMessage()));
        Tooltip.install(imageIban, new Tooltip(ibanValidator.getErrorMessage()));
        //Tooltip.install(imageAmount, new Tooltip(.getErrorMessage()));
        Tooltip.install(imagePaymentFrequency, new Tooltip(paymentFrequencyValidator.getErrorMessage()));
    }

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
        Task<Boolean> partnerExistsTask = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                List<PartnerDTO> partnersByDni = partnerService.findByDni(inputDni.getText());
                return !CollectionUtils.isEmpty(partnersByDni);
            }
        };
        progressIndicator.visibleProperty().bind(partnerExistsTask.runningProperty());
        new Thread(partnerExistsTask).start();
        partnerExistsTask.setOnSucceeded(worker -> {
            boolean result = partnerExistsTask.getValue();
            if (result) {
                AlertUtils.showError("Lo siento, ya existe un socio con el mismo DNI.");
            } else {
                Task<Partner> savePartnerTask = new Task<Partner>() {
                    @Override
                    protected Partner call() throws Exception {
                        PersonalData personalData = PersonalData.builder()
                                .name(inputName.getText())
                                .surnames(inputSurnames.getText())
                                .dni(inputDni.getText())
                                .birthDate(new SimpleDateFormat("yyyy-MM-dd").parse(birthDatePicker.getValue().toString()))
                                .address(inputAddress.getText())
                                .postCode(inputPostCode.getText())
                                .location(inputLocation.getText())
                                .province(inputProvince.getText())
                                .phone1(inputPhone1.getText())
                                .phone2(inputPhone2.getText())
                                .email(inputEmail.getText())
                                .build();
                        BankingData bankingData = BankingData.builder()
                                .name(inputBankName.getText())
                                .surnames(inputBankSurnames.getText())
                                .iban(inputIban.getText())
                                .build();
                        Partner partner = Partner.builder()
                                .active(true)
                                .amount(inputAmount.getText())
                                .subscribedFrom(new Date())
                                .personalData(personalData)
                                .bankingData(bankingData)
                                .build();
                        return partnerService.save(partner);
                    }
                };
                progressIndicator.visibleProperty().bind(savePartnerTask.runningProperty());
                new Thread(savePartnerTask).start();
                savePartnerTask.setOnSucceeded(saveWorker -> {
                    Partner partner = savePartnerTask.getValue();
                    if (partner != null) {
                        AlertUtils.showInfo("El socio se ha dado de alta correctamente");
                        this.stageManager.getModalStage().close();
                        this.stageManager.switchScene(FxmlView.PARTNER);
                    } else {
                        AlertUtils.showError("No se ha podido dar de alta al socio.");
                    }
                });
                savePartnerTask.setOnCancelled(saveWorker -> {
                    AlertUtils.showError("No se ha podido dar de alta al socio.");
                });
                savePartnerTask.setOnFailed(saveWorker -> {
                    AlertUtils.showError("No se ha podido dar de alta al socio.");
                });
            }
        });

        partnerExistsTask.setOnCancelled((worker -> {
            AlertUtils.showError("Ha habido un problema al consultar los socios.");
        }));

        partnerExistsTask.setOnFailed((worker -> {
            AlertUtils.showError("Ha habido un problema al consultar los socios.");
        }));
    }

    @FXML
    public void validate(KeyEvent event) {
        boolean isValidName = nameValidator.validate(inputName.getText());
        imageName.setVisible(!isValidName);

        boolean isValidSurnames = surnamesValidator.validate(inputSurnames.getText());
        imageSurnames.setVisible(!isValidSurnames);

        /*
        boolean isValidBirthdate = date.validate(inputSurnames.getText());
        imageSurnames.setVisible(!isValidSurnames);
        */

        boolean isValidDni = dniValidator.validate(inputDni.getText());
        imageDni.setVisible(!isValidDni);

        boolean isValidAddress = addressValidator.validate(inputAddress.getText());
        imageAddress.setVisible(!isValidAddress);

        boolean isValidPostCode = postCodeValidator.validate(inputPostCode.getText());
        imagePostCode.setVisible(!isValidPostCode);

        boolean isValidLocation = locationValidator.validate(inputLocation.getText());
        imageLocation.setVisible(!isValidLocation);

        boolean isValidProvince = provinceValidator.validate(inputProvince.getText());
        imageProvince.setVisible(!isValidProvince);

        boolean isValidPhone1 = phoneValidator.validate(inputPhone1.getText());
        imagePhone1.setVisible(!isValidPhone1);

        boolean isValidPhone2 = phoneValidator.validate(inputPhone2.getText());
        imagePhone2.setVisible(!isValidPhone2);

        boolean isValidEmail = emailValidator.validate(inputEmail.getText());
        imageEmail.setVisible(!isValidEmail);

        boolean isValidBankName = nameValidator.validate(inputBankName.getText());
        imageBankName.setVisible(!isValidBankName);

        boolean isValidBankSurnames = surnamesValidator.validate(inputBankSurnames.getText());
        imageBankSurnames.setVisible(!isValidBankSurnames);

        boolean isValidIban = ibanValidator.validate(inputIban.getText());
        imageIban.setVisible(!isValidIban);

        boolean isValidPaymentFrequency = paymentFrequencyValidator.validate(comboPaymentFrequency.getSelectionModel().getSelectedItem());

        boolean fieldsAreNotEmpty = StringUtils.isNotBlank(inputName.getText())
                && StringUtils.isNotBlank(inputSurnames.getText())
                && StringUtils.isNotBlank(GetUtils.get(() -> birthDatePicker.getValue().toString(), ""))
                && StringUtils.isNotBlank(inputDni.getText())
                && StringUtils.isNotBlank(inputAddress.getText())
                && StringUtils.isNotBlank(inputPostCode.getText())
                && StringUtils.isNotBlank(inputLocation.getText())
                && StringUtils.isNotBlank(inputProvince.getText())
                && StringUtils.isNotBlank(inputPhone1.getText())
                && StringUtils.isNotBlank(inputPhone2.getText())
                && StringUtils.isNotBlank(inputEmail.getText())
                && StringUtils.isNotBlank(inputBankName.getText())
                && StringUtils.isNotBlank(inputBankSurnames.getText())
                && StringUtils.isNotBlank(inputIban.getText())
                && StringUtils.isNotBlank(inputAmount.getText())
                && comboPaymentFrequency.getSelectionModel().getSelectedItem() != null;

        boolean result = isValidName && isValidSurnames && isValidDni && isValidAddress && isValidPostCode && isValidLocation
                && isValidProvince && isValidPhone1 && isValidPhone2 && isValidEmail && isValidBankName && isValidBankSurnames
                && isValidIban && isValidPaymentFrequency && fieldsAreNotEmpty;

        buttonSave.setDisable(!result);
    }

}
