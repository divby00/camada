package org.wildcat.camada.controller;

import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.WebPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.controller.listener.WebViewEditorListener;
import org.wildcat.camada.controller.pojo.EmailUserData;
import org.wildcat.camada.persistence.entity.EmailTemplate;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.EmailTemplateService;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.pojo.AttachmentDetails;
import org.wildcat.camada.service.pojo.MailRequest;
import org.wildcat.camada.service.pojo.MailResponse;
import org.wildcat.camada.service.utils.AlertUtils;
import org.wildcat.camada.service.utils.FileSizeUtils;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.net.URL;
import java.util.*;

@Slf4j
@Controller
public class EmailController implements Initializable {

    @Value("${threshold.email.placeholders.warning}")
    private Integer threshold;

    @FXML
    private Button saveTemplateButton;

    @FXML
    private Button deleteTemplateButton;

    @FXML
    private Button sendEmailButton;

    @FXML
    private Button addAttachmentButton;

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

    @FXML
    private ListView<String> placeholdersList;

    @FXML
    private HBox attachmentsHbox;

    @Lazy
    @Autowired
    protected StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final EmailTemplateService emailTemplateService;

    @Resource
    private final MailService mailService;

    private Set<AttachmentDetails> attachments;

    public EmailController(CamadaUserService camadaUserService, EmailTemplateService emailTemplateService, MailService mailService) {
        this.camadaUserService = camadaUserService;
        this.emailTemplateService = emailTemplateService;
        this.mailService = mailService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attachments = new HashSet<>();
        EmailUserData emailUserData = (EmailUserData) stageManager.getPrimaryStage().getUserData();
        areaTo.setText(String.join(",", emailUserData.getEmails()));
        placeholdersList.getItems().setAll(FXCollections.observableList(emailUserData.getPlaceholders()));
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
        addAttachmentButton.setDisable(true);

        nameTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton());
        });
        areaTo.textProperty().addListener((obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton());
            sendEmailButton.setDisable(hasToDisableSendButton());
            addAttachmentButton.setDisable(hasToDisableSendButton());
        });
        subjectTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton());
            sendEmailButton.setDisable(hasToDisableSendButton());
            addAttachmentButton.setDisable(hasToDisableSendButton());
        });
        WebView webView = (WebView) htmlEditor.lookup("WebView");
        new WebViewEditorListener(webView, (obs, oldVal, newVal) -> {
            saveTemplateButton.setDisable(hasToDisableSaveButton());
            sendEmailButton.setDisable(hasToDisableSendButton());
            addAttachmentButton.setDisable(hasToDisableSendButton());
        });
    }

    private boolean checkPlaceholdersThreshold() {
        boolean result = true;
        int to = Arrays.asList(areaTo.getText().split(",")).size();
        if (mailService.containsPlaceholder(htmlEditor.getHtmlText()) && to >= threshold) {
            ButtonType buttonType = AlertUtils.showConfirmation("Enviar un correo con marcadores a " + to + " destinatarios\npuede ser lento. ¿Quieres continuar?");
            result = ButtonType.YES.equals(buttonType);
        }
        return result;
    }

    @FXML
    public void onAddAttachmentButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un fichero adjunto");
        File file = fileChooser.showOpenDialog(stageManager.getPrimaryStage());
        if (file != null) {
            byte[] attachmentBytes = getAttachmentBytes(file);
            long size = FileUtils.sizeOf(file);
            String filename = file.getName();
            if (attachmentBytes != null && attachmentBytes.length > 0) {
                attachments.add(AttachmentDetails.builder()
                        .bytes(attachmentBytes)
                        .filename(filename)
                        .size(size)
                        .build());
                Button button = new Button();
                button.setText(filename + ", " + FileSizeUtils.getFormattedBytes(size));
                button.setOnAction(this::onRemoveAttachmentsButtonAction);
                attachmentsHbox.getChildren().add(button);
            } else {
                AlertUtils.showError("Ha ocurrido un error al leer el fichero " + file.getName());
            }
        }
    }

    private void onRemoveAttachmentsButtonAction(ActionEvent event) {
        String filename = StringUtils.substringBefore(((Button) event.getSource()).getText(), ",");
        attachments.removeIf(attachment -> StringUtils.equals(filename, attachment.getFilename()));
        attachmentsHbox.getChildren().remove(event.getSource());
    }

    private byte[] getAttachmentBytes(File file) {
        byte[] result = null;
        try {
            result = FileUtils.readFileToByteArray(file);
        } catch (Exception exception) {
            log.error(ExceptionUtils.getStackTrace(exception));
        }
        return result;
    }

    @FXML
    public void onSendEmailButtonAction(ActionEvent event) {
        if (checkPlaceholdersThreshold()) {
            EmailUserData emailUserData = (EmailUserData) stageManager.getPrimaryStage().getUserData();
            Map<String, Map<String, Object>> rowInfo = emailUserData.getRowInfo();
            Task<List<MailResponse>> emailSendingTask = new Task<>() {
                @Override
                protected List<MailResponse> call() {
                    MailRequest mailRequest = MailRequest.builder()
                            .isHtml(true)
                            .internetAddresses(getInternetAddresses(areaTo.getText()))
                            .message(htmlEditor.getHtmlText())
                            .subject(subjectTextField.getText())
                            .attachments(new ArrayList<>(attachments))
                            .build();
                    if (mailService.containsPlaceholder(htmlEditor.getHtmlText())) {
                        return mailService.sendReplacingPlaceholders(mailRequest, rowInfo);
                    } else {
                        return mailService.send(mailRequest);
                    }
                }
            };
            progressIndicator.visibleProperty().bind(emailSendingTask.runningProperty());
            new Thread(emailSendingTask).start();
            emailSendingTask.setOnSucceeded(worker -> {
                List<MailResponse> mailResponses = emailSendingTask.getValue();
                boolean allOk = mailResponses.stream().allMatch(MailResponse::getSuccess);
                if (allOk) {
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
                deleteTemplateButton.setDisable(true);
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
    public void onPlaceholdersListMouseClicked(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            String placeholder = placeholdersList.getSelectionModel().getSelectedItem();
            WebView webView = (WebView) htmlEditor.lookup("WebView");
            WebPage webPage = Accessor.getPageFor(webView.getEngine());
            webPage.executeCommand("insertText", "{{" + placeholder + "}}");
        }
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
                deleteTemplateButton.setDisable(hasToDisableDeleteButton());
                subjectTextField.setText(null);
                publicCheck.setSelected(false);
                nameTextField.setText(null);
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
                saveTemplateButton.setDisable(hasToDisableSaveButton());
                sendEmailButton.setDisable(hasToDisableSendButton());
                addAttachmentButton.setDisable(hasToDisableSendButton());
                deleteTemplateButton.setDisable(hasToDisableDeleteButton());
                publicCheck.setDisable(hasToDisableDeleteButton());
                nameTextField.setDisable(hasToDisableDeleteButton());
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

    private boolean hasToDisableSaveButton() {
        EmailTemplate selectedItem = comboEmailTemplates.getSelectionModel().getSelectedItem();
        boolean anyBlank = StringUtils.isAnyBlank(htmlEditor.getHtmlText(), subjectTextField.getText(), nameTextField.getText());
        boolean isNotUser;
        if (selectedItem != null) {
            isNotUser = !StringUtils.equalsIgnoreCase(selectedItem.getUserName(), camadaUserService.getUser().getName());
            return anyBlank || isEmptyHtml(htmlEditor.getHtmlText()) || isNotUser;
        } else {
            return anyBlank || isEmptyHtml(htmlEditor.getHtmlText());
        }
    }

    private boolean hasToDisableSendButton() {
        return StringUtils.isAnyBlank(htmlEditor.getHtmlText(), subjectTextField.getText(), areaTo.getText())
                || isEmptyHtml(htmlEditor.getHtmlText());
    }

    private boolean hasToDisableDeleteButton() {
        EmailTemplate selectedItem = comboEmailTemplates.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            return !StringUtils.equalsIgnoreCase(camadaUserService.getUser().getName(), selectedItem.getUserName());
        } else {
            return true;
        }
    }

    private boolean isEmptyHtml(String htmlText) {
        final String[] emptyHtml = new String[]{
                "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>",
                "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><p></p></body></html>",
                "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><p><br></p></body></html>",
        };
        return StringUtils.equalsAny(htmlText, emptyHtml);
    }

}
