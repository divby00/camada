package org.wildcat.camada.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.controller.listener.WebViewEditorListener;
import org.wildcat.camada.persistence.entity.EmailTemplate;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.EmailTemplateService;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.pojo.MailToDetails;
import org.wildcat.camada.service.utils.AlertUtils;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Controller
public class EmailController implements Initializable {

    @FXML
    private Button saveTemplateButton;

    @FXML
    private Button deleteTemplateButton;

    @FXML
    private Button sendEmailButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private CheckBox publicCheck;

    @FXML
    private HTMLEditor htmlEditor;

    @FXML
    private TextField subjectTextField;

    @FXML
    private ComboBox<EmailTemplate> comboEmailTemplates;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextArea areaTo;

    @Lazy
    @Autowired
    protected StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final EmailTemplateService emailTemplateService;

    @Resource
    private final MailService mailService;

    public EmailController(CamadaUserService camadaUserService, EmailTemplateService emailTemplateService, MailService mailService) {
        this.camadaUserService = camadaUserService;
        this.emailTemplateService = emailTemplateService;
        this.mailService = mailService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> emails = (List<String>) stageManager.getPrimaryStage().getUserData();
        areaTo.setText(String.join(",", emails));
        setComboSelectionModel();
        // Fill combo
        Task<ObservableList<EmailTemplate>> emailTemplatesTask = getEmailTemplatesTask();
        progressIndicator.visibleProperty().bind(emailTemplatesTask.runningProperty());
        new Thread(emailTemplatesTask).start();
        emailTemplatesTask.setOnSucceeded(worker -> {
            comboEmailTemplates.setItems(emailTemplatesTask.getValue());
            comboEmailTemplates.getSelectionModel().select(null);
        });
        emailTemplatesTask.setOnCancelled(worker -> {
            AlertUtils.showError("No se ha podido obtener las plantillas de correos electrónicos.");
        });
        emailTemplatesTask.setOnFailed(worker -> {
            AlertUtils.showError("No se ha podido obtener las plantillas de correos electrónicos.");
        });

        saveTemplateButton.setDisable(true);
        deleteTemplateButton.setDisable(true);

        nameTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton(newVal, subjectTextField.getText(), nameTextField.getText()));
        });
        areaTo.textProperty().addListener((obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton(newVal, subjectTextField.getText(), nameTextField.getText()));
            sendEmailButton.setDisable(hasToDisableSendButton(newVal, subjectTextField.getText(), areaTo.getText()));
        });
        subjectTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton(newVal, subjectTextField.getText(), nameTextField.getText()));
            sendEmailButton.setDisable(hasToDisableSendButton(newVal, subjectTextField.getText(), areaTo.getText()));
        });
        WebView webView = (WebView) htmlEditor.lookup("WebView");
        new WebViewEditorListener(webView, (obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton(newVal, subjectTextField.getText(), nameTextField.getText()));
            sendEmailButton.setDisable(hasToDisableSendButton(newVal, subjectTextField.getText(), areaTo.getText()));
        });
    }

    @FXML
    public void onSendEmailButtonAction(ActionEvent event) {
        Task<Boolean> emailSendingTask = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                MailToDetails mailToDetails = MailToDetails.builder()
                        .isHtml(true)
                        .internetAddresses(getInternetAddresses(areaTo.getText()))
                        .message(htmlEditor.getHtmlText())
                        .subject(subjectTextField.getText())
                        .build();
                return mailService.send(mailToDetails);
            }
        };
        progressIndicator.visibleProperty().bind(emailSendingTask.runningProperty());
        new Thread(emailSendingTask).start();
        emailSendingTask.setOnSucceeded(worker -> {
            if (emailSendingTask.getValue()) {
                AlertUtils.showInfo("Los correos electrónicos se han envíado correctamente.");
            } else {
                AlertUtils.showError("Ha habido un error al enviar el correo electrónico.");
            }
        });
        emailSendingTask.setOnCancelled(worker -> {
            AlertUtils.showError("Ha habido un error al enviar el correo electrónico.");
        });
        emailSendingTask.setOnFailed(worker -> {
            AlertUtils.showError("Ha habido un error al enviar el correo electrónico.");
        });
    }

    @FXML
    public void onSaveTemplateButtonAction(ActionEvent event) {
        Task<EmailTemplate> saveTemplateTask = new Task<EmailTemplate>() {
            @Override
            protected EmailTemplate call() {
                String userName = camadaUserService.getUser().getName();
                EmailTemplate emailTemplate = EmailTemplate.builder()
                        .name(nameTextField.getText())
                        .content(htmlEditor.getHtmlText())
                        .isPublic(publicCheck.isSelected())
                        .creationDate(new Date())
                        .subject(subjectTextField.getText())
                        .userName(userName).build();
                EmailTemplate selectedItem = comboEmailTemplates.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    emailTemplate.setId(selectedItem.getId());
                }
                return emailTemplateService.save(emailTemplate);
            }
        };
        progressIndicator.visibleProperty().bind(saveTemplateTask.runningProperty());
        new Thread(saveTemplateTask).start();
        saveTemplateTask.setOnSucceeded(worker -> { // Save template
            Task<ObservableList<EmailTemplate>> emailTemplatesTask = getEmailTemplatesTask();
            progressIndicator.visibleProperty().bind(emailTemplatesTask.runningProperty());
            new Thread(emailTemplatesTask).start();
            emailTemplatesTask.setOnSucceeded(taskWorker -> { // Update combo
                comboEmailTemplates.setItems(emailTemplatesTask.getValue());
                comboEmailTemplates.getSelectionModel().select(null);
                AlertUtils.showInfo("La plantilla se ha guardado correctamente.");
            });
            emailTemplatesTask.setOnCancelled(taskWorker -> {
                AlertUtils.showError("Se ha producido un error al guardar la plantilla.");
            });
            emailTemplatesTask.setOnFailed(taskWorker -> {
                AlertUtils.showError("Se ha producido un error al guardar la plantilla.");
            });
        });
        saveTemplateTask.setOnFailed(worker -> {
            AlertUtils.showError("Se ha producido un error al guardar la plantilla.");
        });
        saveTemplateTask.setOnCancelled(worker -> {
            AlertUtils.showError("Se ha producido un error al guardar la plantilla.");
        });
    }

    @FXML
    public void onDeleteTemplateButtonAction(ActionEvent event) {
        Task<Boolean> deleteEmailTaks = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                boolean result = false;
                try {
                    EmailTemplate emailTemplate = comboEmailTemplates.getSelectionModel().getSelectedItem();
                    if (emailTemplate != null) {
                        emailTemplateService.delete(emailTemplate);
                    }
                    result = true;
                } catch (Exception ex) {
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
                return result;
            }
        };
        progressIndicator.visibleProperty().bind(deleteEmailTaks.runningProperty());
        new Thread(deleteEmailTaks).start();
        deleteEmailTaks.setOnSucceeded(worker -> { // Delete template
            Task<ObservableList<EmailTemplate>> emailTemplatesTask = getEmailTemplatesTask();
            progressIndicator.visibleProperty().bind(emailTemplatesTask.runningProperty());
            new Thread(emailTemplatesTask).start();
            emailTemplatesTask.setOnSucceeded(subWorker -> { // Update combo
                comboEmailTemplates.setItems(emailTemplatesTask.getValue());
                comboEmailTemplates.getSelectionModel().select(null);
                publicCheck.setSelected(false);
                nameTextField.setText(null);
                subjectTextField.setText(null);
                deleteTemplateButton.setDisable(true);
                AlertUtils.showInfo("La plantilla se ha borrado correctamente.");
            });
            emailTemplatesTask.setOnFailed(subWorker -> {
                AlertUtils.showError("Ha habido un error al borrar la plantilla.");
            });
            emailTemplatesTask.setOnCancelled(subWorker -> {
                AlertUtils.showError("Ha habido un error al borrar la plantilla.");
            });
        });
        deleteEmailTaks.setOnCancelled(worker -> {
            AlertUtils.showError("Ha habido un error al borrar la plantilla.");
        });
        deleteEmailTaks.setOnFailed(worker -> {
            AlertUtils.showError("Ha habido un error al borrar la plantilla.");
        });
    }

    private void setComboSelectionModel() {
        Callback<ListView<EmailTemplate>, ListCell<EmailTemplate>> comboCellFactory = getComboCellFactory();
        comboEmailTemplates.setCellFactory(comboCellFactory);
        comboEmailTemplates.setButtonCell(comboCellFactory.call(null));
        comboEmailTemplates.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameTextField.setText(newVal.getName());
                subjectTextField.setText(newVal.getSubject());
                publicCheck.setSelected(newVal.getIsPublic());
                htmlEditor.setHtmlText(newVal.getContent());
                boolean isNotUser = !StringUtils.equalsIgnoreCase(camadaUserService.getUser().getName(), newVal.getUserName());
                deleteTemplateButton.setDisable(isNotUser);
                publicCheck.setDisable(isNotUser);
                nameTextField.setDisable(isNotUser);
            }
        });
    }

    private Callback<ListView<EmailTemplate>, ListCell<EmailTemplate>> getComboCellFactory() {
        return new Callback<ListView<EmailTemplate>, ListCell<EmailTemplate>>() {
            @Override
            public ListCell<EmailTemplate> call(ListView<EmailTemplate> l) {
                return new ListCell<EmailTemplate>() {
                    @Override
                    protected void updateItem(EmailTemplate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        };
    }

    private Task<ObservableList<EmailTemplate>> getEmailTemplatesTask() {
        return new Task<ObservableList<EmailTemplate>>() {
            @Override
            protected ObservableList<EmailTemplate> call() {
                List<EmailTemplate> emailTemplates = emailTemplateService.findAllByOrderByName();
                return FXCollections.observableList(emailTemplates);
            }
        };
    }

    private InternetAddress[] getInternetAddresses(String emails) {
        InternetAddress[] internetAddresses = null;
        try {
            internetAddresses = InternetAddress.parse(emails);
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return internetAddresses;
    }

    private boolean isHtmlEditorEmpty() {
        boolean result = false;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(htmlEditor.getHtmlText())));
            NodeList body = document.getElementsByTagName("body");
            result = StringUtils.isEmpty(body.item(0).getTextContent());
        } catch (Exception ex) {
            log.warn(ExceptionUtils.getStackTrace(ex));
        }
        return result;
    }

    private boolean hasToDisableSaveButton(String... textFields) {
        EmailTemplate selectedItem = comboEmailTemplates.getSelectionModel().getSelectedItem();
        boolean anyBlank = StringUtils.isAnyBlank(textFields) || isHtmlEditorEmpty();
        if (selectedItem != null) {
            boolean isNotUser = !StringUtils.equalsIgnoreCase(selectedItem.getUserName(), camadaUserService.getUser().getName());
            return anyBlank || isNotUser;
        } else {
            return anyBlank;
        }
    }

    private boolean hasToDisableSendButton(String... textFields) {
        return StringUtils.isAnyBlank(textFields) || isHtmlEditorEmpty();
    }

}
