package org.wildcat.camada.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.common.validator.impl.EmailValidatorImpl;
import org.wildcat.camada.common.validator.impl.PasswordCheckValidatorImpl;
import org.wildcat.camada.common.validator.impl.PasswordValidatorImpl;
import org.wildcat.camada.common.validator.impl.TextValidatorImpl;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.utils.AlertUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class NewUserController extends NewEntityController implements Initializable {

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
    private TextValidatorImpl userNameValidator;
    private TextValidatorImpl firstNameValidator;
    private TextValidatorImpl lastNameValidator;
    private PasswordValidatorImpl passwordValidator;
    private PasswordCheckValidatorImpl<TextField> passwordCheckValidator;
    private EmailValidatorImpl emailValidator;

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
        this.userNameValidator = new TextValidatorImpl(2, 20);
        this.firstNameValidator = new TextValidatorImpl(2, 20);
        this.lastNameValidator = new TextValidatorImpl(2, 20);
        this.passwordValidator = new PasswordValidatorImpl();
        this.passwordCheckValidator = new PasswordCheckValidatorImpl<>(inputPassword);
        this.emailValidator = new EmailValidatorImpl();
        Tooltip.install(imageUserName, new Tooltip(userNameValidator.getErrorMessage()));
        Tooltip.install(imagePassword, new Tooltip(passwordValidator.getErrorMessage()));
        Tooltip.install(imagePasswordCheck, new Tooltip(passwordCheckValidator.getErrorMessage()));
        Tooltip.install(imageFirstName, new Tooltip(firstNameValidator.getErrorMessage()));
        Tooltip.install(imageLastName, new Tooltip(lastNameValidator.getErrorMessage()));
        Tooltip.install(imageEmail, new Tooltip(emailValidator.getErrorMessage()));
    }

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
        Task<Integer> userExistsTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                Optional<CamadaUser> emailUser = camadaUserService.findByEmail(StringUtils.lowerCase(inputEmail.getText()));
                Optional<CamadaUser> camadaUser = camadaUserService.findByName(inputUserName.getText());
                int error = 0;
                if (emailUser.isPresent()) {
                    error += 1;
                }
                if (camadaUser.isPresent()) {
                    error += 2;
                }
                return error;
            }
        };
        progressIndicator.visibleProperty().bind(userExistsTask.runningProperty());
        new Thread(userExistsTask).start();
        userExistsTask.setOnSucceeded(worker -> {
            Integer exists = userExistsTask.getValue();
            if (exists != 0) {
                String message = "Lo siento, no puedes usar ese nombre de usuario porque ya está elegido.";
                if (exists == 1) {
                    message = "Lo siento, no puedes usar ese email porque el correo electrónico ya está elegido.";
                }
                AlertUtils.showError(message);
            } else {
                Task<CamadaUser> saveUserTask = new Task<CamadaUser>() {
                    @Override
                    protected CamadaUser call() throws Exception {
                        CamadaUser camadaUser = CamadaUser.builder()
                                .name(inputUserName.getText())
                                .firstName(inputFirstName.getText())
                                .lastName(inputLastName.getText())
                                .email(StringUtils.lowerCase(inputEmail.getText()))
                                .password(DigestUtils.sha1Hex(inputPassword.getText()))
                                .isAdmin(isAdmin.isSelected())
                                .isPartner(isPartner.isSelected())
                                .isVirtualSponsor(isVirtualSponsor.isSelected())
                                .isPresentialSponsor(isPresentialSponsor.isSelected())
                                .isVolunteer(isVolunteer.isSelected())
                                .isActive(true)
                                .activationDate(new Date())
                                .build();
                        CamadaUser savedUser = camadaUserService.save(camadaUser);
                        return savedUser;
                    }
                };
                progressIndicator.visibleProperty().bind(saveUserTask.runningProperty());
                new Thread(saveUserTask).start();
                saveUserTask.setOnSucceeded(saveWorker -> {
                    CamadaUser camadaUser = saveUserTask.getValue();
                    if (camadaUser != null) {
                        AlertUtils.showInfo("El usuario se ha dado de alta correctamente");
                        this.stageManager.getModalStage().close();
                        this.stageManager.switchScene(FxmlView.USER);
                    } else {
                        AlertUtils.showError("No se ha podido dar de alta al usuario");
                    }
                });
                saveUserTask.setOnCancelled(saveWorker -> {
                    AlertUtils.showError("No se ha podido dar de alta al usuario");
                });
                saveUserTask.setOnFailed(saveWorker -> {
                    AlertUtils.showError("No se ha podido dar de alta al usuario");
                });
            }
        });
        userExistsTask.setOnCancelled(worker -> {
            AlertUtils.showError("Ha habido un problema al consultar el nombre del usuario");
        });
        userExistsTask.setOnFailed(worker -> {
            AlertUtils.showError("Ha habido un problema al consultar el nombre del usuario");
        });
    }

    @FXML
    public void validate(KeyEvent event) {
        boolean isValidUserName = validate(userNameValidator, inputUserName.getText(), imageUserName);
        boolean isValidPassword = validate(passwordValidator, inputPassword.getText(), imagePassword);
        boolean isValidPasswordCheck = validate(passwordCheckValidator, inputPasswordCheck.getText(), imagePasswordCheck);
        boolean isValidFirstName = validate(firstNameValidator, inputFirstName.getText(), imageFirstName);
        boolean isValidLastName = validate(lastNameValidator, inputLastName.getText(), imageLastName);
        boolean isValidEmail = validate(emailValidator, inputEmail.getText(), imageEmail);

        boolean fieldsAreNotEmpty = StringUtils.isNotBlank(inputUserName.getText()) && StringUtils.isNotBlank(inputFirstName.getText())
                && StringUtils.isNotBlank(inputLastName.getText()) && StringUtils.isNotBlank(inputPassword.getText())
                && StringUtils.isNotBlank(inputPasswordCheck.getText()) && StringUtils.isNotBlank(inputEmail.getText());

        boolean result = isValidUserName && isValidPassword && isValidPasswordCheck
                && isValidFirstName && isValidLastName && isValidEmail && fieldsAreNotEmpty;

        buttonSave.setDisable(!result);
    }

}
