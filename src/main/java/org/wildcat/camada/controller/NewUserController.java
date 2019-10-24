package org.wildcat.camada.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.validator.EmailValidator;
import org.wildcat.camada.validator.PasswordCheckValidator;
import org.wildcat.camada.validator.PasswordValidator;
import org.wildcat.camada.validator.TextFieldValidator;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

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
    private ImageView imageUserName;
    @FXML
    private ImageView imagePassword;
    @FXML
    private ImageView imagePasswordCheck;
    @FXML
    private ImageView imageFirstName;
    @FXML
    private ImageView imageLastName;
    @FXML
    private ImageView imageEmail;
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
    private ProgressIndicator progressIndicator;


    @Lazy
    @Autowired
    private StageManager stageManager;

    private ResourceBundle resources;

    private TextFieldValidator<TextField> userNameValidator;
    private TextFieldValidator<TextField> firstNameValidator;
    private TextFieldValidator<TextField> lastNameValidator;
    private PasswordValidator<TextField> passwordValidator;
    private PasswordCheckValidator<TextField> passwordCheckValidator;
    private EmailValidator<TextField> emailValidator;

    @Resource
    private final CamadaUserService camadaUserService;

    public NewUserController(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.imageUserName.managedProperty().bind(imageUserName.visibleProperty());
        this.imagePassword.managedProperty().bind(imagePassword.visibleProperty());
        this.imagePasswordCheck.managedProperty().bind(imagePasswordCheck.visibleProperty());
        this.imageFirstName.managedProperty().bind(imageFirstName.visibleProperty());
        this.imageLastName.managedProperty().bind(imageLastName.visibleProperty());
        this.imageEmail.managedProperty().bind(imageEmail.visibleProperty());
        this.userNameValidator = new TextFieldValidator<>(inputUserName, 4, 20);
        this.firstNameValidator = new TextFieldValidator<>(inputFirstName, 4, 20);
        this.lastNameValidator = new TextFieldValidator<>(inputLastName, 4, 20);
        this.passwordValidator = new PasswordValidator<>(inputPassword);
        this.passwordCheckValidator = new PasswordCheckValidator<>(inputPasswordCheck, inputPassword);
        this.emailValidator = new EmailValidator<>(inputEmail);
    }

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
    }

    @FXML
    public void validate(KeyEvent event) {
        Task<Boolean> validationTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                boolean isValidUserName = userNameValidator.validate();
                imageUserName.setVisible(!isValidUserName);

                boolean isValidPassword = passwordValidator.validate();
                imagePassword.setVisible(!isValidPassword);

                boolean isValidPasswordCheck = passwordCheckValidator.validate();
                imagePasswordCheck.setVisible(!isValidPasswordCheck);

                boolean isValidFirstName = firstNameValidator.validate();
                imageFirstName.setVisible(!isValidFirstName);

                boolean isValidLastName = lastNameValidator.validate();
                imageLastName.setVisible(!isValidLastName);

                boolean isValidEmail = emailValidator.validate();
                imageEmail.setVisible(!isValidEmail);

                return isValidUserName && isValidPassword && isValidPasswordCheck
                        && isValidFirstName && isValidLastName && isValidEmail;
            }
        };
        progressIndicator.visibleProperty().bind(validationTask.runningProperty());
        new Thread(validationTask).start();
        validationTask.setOnSucceeded(workerState -> {
            buttonSave.setDisable(!validationTask.getValue());
        });
        validationTask.setOnRunning(workerState -> {
            buttonSave.setDisable(true);
        });
        validationTask.setOnCancelled(workerState -> {
            buttonSave.setDisable(true);
        });
        validationTask.setOnFailed(workerState -> {
            buttonSave.setDisable(true);
        });
    }

}