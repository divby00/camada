package org.wildcat.camada.controller;

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
        progressIndicator.setVisible(true);
        if (camadaUserService.authenticate(username.getText(), password.getText())) {
            progressIndicator.setVisible(false);
            stageManager.switchScene(FxmlView.DASHBOARD);
        } else {
            progressIndicator.setVisible(false);
            AlertUtils.error(resources, "logic.accesserror.title", "login.accesserror.unabletocheckidentity", "login.accesserror.inputvalidusername");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // TODO: Remove this!
        username.setText("admin");
        password.setText("admin");
    }

}
