package org.wildcat.camada.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.service.CamadaUserService;
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

    @Lazy
    @Autowired
    private StageManager stageManager;

    private ResourceBundle resources;

    @Resource
    private final CamadaUserService camadaUserService;

    public LoginController(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
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

        // TODO: Remove this!
        username.setText("admin");
        password.setText("admin");
    }

}
