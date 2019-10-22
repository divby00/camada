package org.wildcat.camada.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.service.CamadaUserService;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class NewUserController implements Initializable {

    @FXML
    private TextField inputUserName;
    @FXML
    private TextField inputPassword;
    @FXML
    private TextField inputPasswordCheck;
    @FXML
    private TextField inputFirstName;
    @FXML
    private TextField inputLastName;
    @FXML
    private TextField inputEmail;
    @FXML
    private CheckBox isAdmin;
    @FXML
    private CheckBox isVirtualSponsor;
    @FXML
    private CheckBox isPresentialSponsor;
    @FXML
    private CheckBox isPartner;
    @FXML
    private CheckBox isVolunteer;
    @FXML
    private Button buttonSave;
    @FXML
    private ToggleButton toggleView;
    @FXML
    private ProgressIndicator progressIndicator;


    @Lazy
    @Autowired
    private StageManager stageManager;

    private ResourceBundle resources;
    private Pattern passwordPattern;
    private Pattern emailPattern;

    @Resource
    private final CamadaUserService camadaUserService;

    public NewUserController(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.passwordPattern = Pattern.compile("(?=.*[a-z])(?=.*d)(?=.*[@#$%])(?=.*[A-Z]).{6,16}");
        this.emailPattern = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");
    }

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
    }

    @FXML
    public void validate(KeyEvent event) {
        Task<Boolean> validationTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                String userName = inputUserName.getText();
                String password = inputPassword.getText();
                String secondPassword = inputPasswordCheck.getText();
                String firstName = inputFirstName.getText();
                String lastName = inputLastName.getText();
                String email = inputEmail.getText();
                boolean isValidUserName = StringUtils.isNotBlank(userName) && StringUtils.isAlphanumeric(userName) && userName.length() > 4 && userName.length() < 20;
                boolean isValidPassword = StringUtils.isNotBlank(password) && validatePassword(password);
                boolean isValidSecondPassword = StringUtils.isNotBlank(secondPassword) && StringUtils.equals(secondPassword, password);
                boolean isValidFirstName = StringUtils.isNotBlank(firstName) && StringUtils.isAlphanumeric(firstName) && firstName.length() > 4 && firstName.length() < 20;
                boolean isValidLastName = StringUtils.isNotBlank(lastName) && StringUtils.isAlphanumeric(lastName) && lastName.length() > 4 && lastName.length() < 20;
                boolean isValidEmail = StringUtils.isNotBlank(email) && validateEmail(email);
                boolean result = isValidUserName && isValidPassword && isValidSecondPassword
                        && isValidFirstName && isValidLastName && isValidEmail;
                boolean userExists = false;
                if (result) {
                    Optional<CamadaUser> user = camadaUserService.findByName(userName);
                    userExists = user.isPresent();
                }
                toggleView.setDisable(StringUtils.isBlank(password));
                buttonSave.setDisable(!(result && !userExists));
                return result && !userExists;
            }
        };
        progressIndicator.visibleProperty().bind(validationTask.runningProperty());
        new Thread(validationTask).start();
        validationTask.setOnSucceeded(workerState -> {
            if (validationTask.getValue()) {

            } else {
                // User exists
            }
        });
    }

    private boolean validatePassword(String password) {
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    private boolean validateEmail(String email) {
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

}
