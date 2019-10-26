package org.wildcat.camada.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.MailToDetails;
import org.wildcat.camada.utils.AlertUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class LoginController implements Initializable {

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private Hyperlink linkForgottenPassword;

    @Lazy
    @Autowired
    private StageManager stageManager;

    private ResourceBundle resources;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final MailService mailService;

    public LoginController(CamadaUserService camadaUserService, MailService mailService) {
        this.camadaUserService = camadaUserService;
        this.mailService = mailService;
    }

    @FXML
    public void login(ActionEvent event) {
        Task<Boolean> authenticateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return camadaUserService.authenticate(username.getText(), password.getText());
            }
        };
        progressIndicator.visibleProperty().bind(authenticateTask.runningProperty());
        new Thread(authenticateTask).start();
        authenticateTask.setOnSucceeded(worker -> {
            if (authenticateTask.getValue()) {
                stageManager.switchScene(FxmlView.DASHBOARD);
            } else {
                AlertUtils.showError("Nombre de usuario o contraseña incorrectos");
            }
        });
        authenticateTask.setOnFailed(worker -> {
            AlertUtils.showError("No se puede comprobar la autenticidad del usuario");
        });
        authenticateTask.setOnCancelled(worker -> {
            AlertUtils.showError("No se puede comprobar la autenticidad del usuario");
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        BooleanProperty booleanProperty = new SimpleBooleanProperty(StringUtils.isNotBlank(username.getText()));
        //linkForgottenPassword.visibleProperty().bind(booleanProperty);
        // TODO: Remove this!
        username.setText("admin");
        password.setText("admin");
    }

    public void onLinkForgottenPasswordClicked(ActionEvent event) {
        MailToDetails mailDetails = MailToDetails.builder()
                .to("") // TODO: Add a valid email here
                .message("<html><head></head><body><h1>Testing HTML mail with attachment</h1><ul><li>one</li><li>two</li></ul></body></html>")
                .fileName("file.txt")
                .attachment("ˁ(OᴥO)ˀ")
                .build();
        mailService.send(mailDetails);
    }

}
