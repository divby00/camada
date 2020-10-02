package org.wildcat.camada.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.service.ErrorService;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.pojo.MailToDetails;
import org.wildcat.camada.service.utils.AlertUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ErrorController implements Initializable {

    @Value("${mail.error.report}")
    private String email;

    @FXML
    private TextArea errorMessageTextArea;

    @FXML
    private ProgressIndicator progressIndicator;

    @Resource
    private final ErrorService errorService;

    @Resource
    private final MailService mailService;

    @Lazy
    @Autowired
    protected StageManager stageManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Throwable throwable = (Throwable) stageManager.getPrimaryStage().getUserData();
        String message = errorService.getErrorMessage(throwable);
        errorMessageTextArea.setText(message);

    }

    public ErrorController(ErrorService errorService, MailService mailService) {
        this.errorService = errorService;
        this.mailService = mailService;
    }

    @FXML
    public void onSendErrorReportButtonAction(ActionEvent event) {
        Task<Boolean> sendEmailTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return mailService.send(MailToDetails.builder()
                        .to(email)
                        .subject("Informe de error de aplicación La Camada")
                        .message(errorMessageTextArea.getText())
                        .isHtml(false)
                        .build());
            }
        };
        progressIndicator.visibleProperty().bind(sendEmailTask.runningProperty());
        new Thread(sendEmailTask).start();
        sendEmailTask.setOnSucceeded(worker -> {
            boolean result = (boolean) worker.getSource().getValue();
            if (result) {
                AlertUtils.showInfo("¡Gracias!, el informe de error se ha envíado correctamente.");
                exitApplication();
            } else {
                AlertUtils.showError("No se ha podido mandar el informe de error.");
            }
        });
        sendEmailTask.setOnFailed(worker -> {
            AlertUtils.showError("No se ha podido mandar el informe de error.");
        });
        sendEmailTask.setOnCancelled(worker -> {
            AlertUtils.showError("No se ha podido mandar el informe de error.");
        });
    }

    @FXML
    public void onCloseApplicationButtonAction(ActionEvent event) {
        exitApplication();
    }

    private void exitApplication() {
        Platform.exit();
        System.exit(0);
    }
}
