package org.wildcat.camada.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.stage.Screen;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.MailService;
import org.wildcat.camada.service.impl.PasswordGenerator;
import org.wildcat.camada.service.picture.PictureService;
import org.wildcat.camada.service.pojo.MailRequest;
import org.wildcat.camada.service.pojo.MailResponse;
import org.wildcat.camada.service.utils.AlertUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.EMPTY;

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

    @FXML
    private Button btnLogin;

    @Lazy
    @Autowired
    private StageManager stageManager;

    private ResourceBundle resources;

    @Resource
    private final CamadaUserService camadaUserService;

    @Resource
    private final PictureService pictureService;

    @Resource
    private final MailService mailService;

    public LoginController(CamadaUserService camadaUserService, PictureService pictureService, MailService mailService) {
        this.camadaUserService = camadaUserService;
        this.pictureService = pictureService;
        this.mailService = mailService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.camadaUserService.setUser(null);
        linkForgottenPassword.disableProperty().bind(username.textProperty().isEmpty());
        btnLogin.disableProperty().bind(username.textProperty().isEmpty().or(password.textProperty().isEmpty()));

        // TODO: Remove this!
        username.setText("admin");
        password.setText("u0U#z4Q@r4L@");
    }

    @FXML
    public void onBtnLoginAction(ActionEvent event) {
        if (btnLogin.isDisabled())
            return;
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
                //final double width = this.stageManager.getPrimaryStage().getWidth();
                Task<Boolean> loadingPicturesTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() {
                        Screen primary = Screen.getPrimary();
                        Rectangle2D visualBounds = primary.getVisualBounds();
                        pictureService.getPicturesData(visualBounds.getWidth(), 5, 100);
                        return true;
                    }
                };
                new Thread(loadingPicturesTask).start();
                progressIndicator.visibleProperty().bind(loadingPicturesTask.runningProperty());
                loadingPicturesTask.setOnSucceeded(pictureLoadingWorker -> {
                    stageManager.switchScene(FxmlView.HOME);
                });
            } else {
                AlertUtils.showError("Nombre de usuario o contraseña incorrectos.");
            }
        });
        authenticateTask.setOnFailed(worker -> {
            AlertUtils.showError("No se puede comprobar la autenticidad del usuario.");
        });
        authenticateTask.setOnCancelled(worker -> {
            AlertUtils.showError("No se puede comprobar la autenticidad del usuario.");
        });
    }

    @FXML
    public void onLinkForgottenPasswordAction(ActionEvent event) {
        String message = MessageFormat.format("Se mandará un email con la nueva contraseña a la dirección de correo asociada al usuario.", username.getText());
        ButtonType pressedButton = AlertUtils.showConfirmation(message);
        if (ButtonType.YES == pressedButton) {
            sendEmail();
        }
    }

    private void sendEmail() {
        Task<Optional<CamadaUser>> fetchUserTask = new Task<Optional<CamadaUser>>() {
            @Override
            protected Optional<CamadaUser> call() throws Exception {
                return camadaUserService.findByName(username.getText());
            }

        };
        progressIndicator.visibleProperty().bind(fetchUserTask.runningProperty());
        new Thread(fetchUserTask).start();

        fetchUserTask.setOnSucceeded(worker -> {
            Optional<CamadaUser> camadaUser = fetchUserTask.getValue();
            final String password = PasswordGenerator.generate();
            final String email = camadaUser.map(CamadaUser::getEmail).orElse(EMPTY);
            Task<List<MailResponse>> sendingEmailTask = new Task<>() {
                @Override
                protected List<MailResponse> call() {
                    String subject = "Recuperación de contraseña de La Camada";
                    String message = "Hola.\nAquí tienes una contraseña nueva para La Camada:\n"
                            + password + "\nSi no has solicitado nada ignora este email, por favor.\nGracias.\n¡Hasta luego!";
                    MailRequest mailDetails = MailRequest.builder().to(email).subject(subject).message(message).isHtml(false).build();
                    return mailService.send(mailDetails);
                }
            };
            progressIndicator.visibleProperty().bind(sendingEmailTask.runningProperty());
            new Thread(sendingEmailTask).start();
            sendingEmailTask.setOnSucceeded(emailWorker -> {
                List<MailResponse> mailResponses = sendingEmailTask.getValue();
                boolean allOk = mailResponses.stream().allMatch(MailResponse::getSuccess);
                String message = (allOk) ? "Se ha envíado correctamente el email de recuperación."
                        : "Ha ocurrido un error al enviar el email de recuperación.";
                if (allOk) {
                    camadaUser.ifPresent(user -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DATE, 1);
                        user.setTmpPassword(DigestUtils.sha1Hex(password));
                        user.setTmpPasswordExpiration(calendar.getTime());
                        camadaUserService.save(user);
                    });
                }
                AlertUtils.showInfo(message);
            });
            sendingEmailTask.setOnCancelled(emailWorker -> {
                AlertUtils.showInfo("Ha ocurrido un error al enviar el email de recuperación.");
            });
            sendingEmailTask.setOnFailed(emailWorker -> {
                AlertUtils.showInfo("Ha ocurrido un error al enviar el email de recuperación.");
            });
        });
        fetchUserTask.setOnFailed(worker -> {
            AlertUtils.showError("No se ha podido recuperar la información del usuario.");
        });
        fetchUserTask.setOnCancelled(worker -> {
            AlertUtils.showError("No se ha podido recuperar la información del usuario.");
        });
    }

}
