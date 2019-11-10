package org.wildcat.camada.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.persistence.entity.EmailTemplate;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.EmailTemplateService;
import org.wildcat.camada.service.utils.AlertUtils;

import javax.annotation.Resource;
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
    private Button sendEmailButton;

    @FXML
    private HTMLEditor htmlEditor;

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

    public EmailController(CamadaUserService camadaUserService, EmailTemplateService emailTemplateService) {
        this.camadaUserService = camadaUserService;
        this.emailTemplateService = emailTemplateService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> emails = (List<String>) stageManager.getPrimaryStage().getUserData();
        areaTo.setText(String.join(",", emails));
        Task<ObservableList<EmailTemplate>> getEmailTemplatesTask = new Task<ObservableList<EmailTemplate>>() {
            @Override
            protected ObservableList<EmailTemplate> call() {
                List<EmailTemplate> emailTemplates = emailTemplateService.findAllByOrderByNameAsc();
                return FXCollections.observableList(emailTemplates);
            }
        };
        progressIndicator.visibleProperty().bind(getEmailTemplatesTask.runningProperty());
        new Thread(getEmailTemplatesTask).start();
        getEmailTemplatesTask.setOnSucceeded(worker -> {
            ObservableList<EmailTemplate> items = getEmailTemplatesTask.getValue();
            comboEmailTemplates.setItems(items);
        });
        getEmailTemplatesTask.setOnCancelled(worker -> {
            AlertUtils.showError("No se ha podido obtener las plantillas de correos electrónicos.");
        });
        getEmailTemplatesTask.setOnFailed(worker -> {
            AlertUtils.showError("No se ha podido obtener las plantillas de correos electrónicos.");
        });
    }

    @FXML
    public void onSendEmailButtonAction(ActionEvent event) {
    }

    @FXML
    public void onSaveTemplateButtonAction(ActionEvent event) {
        Task<EmailTemplate> saveTemplateTask = new Task<EmailTemplate>() {
            @Override
            protected EmailTemplate call() {
                String name = "plantilla" + "_" + new Date();
                String userName = camadaUserService.getUser().getName();
                EmailTemplate emailTemplate = EmailTemplate.builder()
                        .name(name)
                        .content(htmlEditor.getHtmlText())
                        .creationDate(new Date())
                        .userName(userName).build();
                return emailTemplateService.save(emailTemplate);
            };
        };
        progressIndicator.visibleProperty().bind(saveTemplateTask.runningProperty());
        new Thread(saveTemplateTask).start();
        saveTemplateTask.setOnSucceeded(worker -> {
            AlertUtils.showInfo("La plantilla se ha guardado correctamente.");
            // TODO: Update combo
        });
        saveTemplateTask.setOnFailed(worker -> {
            AlertUtils.showError("Se ha producido un error al guardar la plantilla.");
        });
        saveTemplateTask.setOnCancelled(worker -> {
            AlertUtils.showError("Se ha producido un error al guardar la plantilla.");
        });
    }
}
