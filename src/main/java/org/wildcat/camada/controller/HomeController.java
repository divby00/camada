package org.wildcat.camada.controller;

import de.androidpit.colorthief.ColorThief;
import de.androidpit.colorthief.RGBUtil;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.service.utils.AlertUtils;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import static org.wildcat.camada.service.utils.GetUtils.get;

@Slf4j
@Controller
public class HomeController implements Initializable {

    private final static String USER_CONTAINER = "userContainer";
    private final static String PARTNER_CONTAINER = "partnerContainer";
    private final static String SPONSOR_CONTAINER = "sponsorContainer";
    private final static String VOLUNTEER_CONTAINER = "volunteerContainer";

    @FXML
    private Label greeting;

    @FXML
    private Label whatToDo;

    @FXML
    private HBox actionContainer;

    @FXML
    private VBox userContainer;

    @FXML
    private VBox partnerContainer;

    @FXML
    private VBox sponsorContainer;

    @FXML
    private VBox volunteerContainer;

    @FXML
    private Hyperlink linkOpenBrowser;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private VBox contentVbox;

    @FXML
    private ImageView imageBanner;

    @FXML
    private AnchorPane backgroundPane;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    public HomeController(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Random random = new Random(System.currentTimeMillis());
        String name = "back0" + random.nextInt(8) + ".png";
        Image image = new Image(name, false);
        imageBanner.setImage(image);
        int[] color = ColorThief.getColor(SwingFXUtils.fromFXImage(image, null));
        String hexColor = Integer.toHexString(RGBUtil.packRGB(color));
        backgroundPane.setStyle(" -fx-background-color: linear-gradient(white 10%, #" + hexColor + " 90%)");

        String greetingMessage = resources.getString("home.greeting");
        String defaultGreetingMessage = resources.getString("home.greeting.default");
        final Long id = get(() -> camadaUserService.getUser().getId(), -1L);
        Task<Optional<CamadaUser>> findUserTask = new Task<Optional<CamadaUser>>() {
            @Override
            protected Optional<CamadaUser> call() {
                return camadaUserService.findById(id);
            }
        };
        progressIndicator.visibleProperty().bind(findUserTask.runningProperty());
        new Thread(findUserTask).start();
        findUserTask.setOnSucceeded(worker -> {
            Optional<CamadaUser> user = findUserTask.getValue();
            String firstName = user.map(CamadaUser::getFirstName).orElse("");
            if (StringUtils.isNotBlank(firstName)) {
                greeting.setText(MessageFormat.format(greetingMessage, get(() -> firstName, defaultGreetingMessage)));
            } else {
                greeting.setText("");
            }
            boolean showAdmin = user.map(CamadaUser::getIsAdmin).orElse(false);
            boolean showSponsorContainer = user.map(u -> u.getIsPresentialSponsor() || u.getIsVirtualSponsor()).orElse(false);
            boolean showPartnerContainer = user.map(CamadaUser::getIsPartner).orElse(false);
            boolean showVolunteerContainer = user.map(CamadaUser::getIsVolunteer).orElse(false);
            actionContainer.getChildren().clear();
            if (showAdmin) {
                actionContainer.getChildren().add(userContainer);
            }
            if (showPartnerContainer) {
                actionContainer.getChildren().add(partnerContainer);
            }
            if (showSponsorContainer) {
                actionContainer.getChildren().add(sponsorContainer);
            }
            if (showVolunteerContainer) {
                actionContainer.getChildren().add(volunteerContainer);
            }
            if (actionContainer.getChildren().size() < 1) {
                whatToDo.setText(resources.getString("home.nopermission"));
            }
            contentVbox.setVisible(true);
        });
        findUserTask.setOnFailed(worker -> {
            AlertUtils.showError("No se ha podido recuperar la información del usuario.");
            contentVbox.setVisible(false);
        });
        findUserTask.setOnCancelled(worker -> {
            AlertUtils.showError("No se ha podido recuperar la información del usuario.");
            contentVbox.setVisible(false);
        });
    }

    @FXML
    public void onManagementButtonAction(ActionEvent event) {
        Button source = (Button) event.getSource();
        String id = source.getParent().getId();
        FxmlView fxmlView;
        switch (id) {
            case USER_CONTAINER:
                fxmlView = FxmlView.USER;
                break;
            case PARTNER_CONTAINER:
                fxmlView = FxmlView.PARTNER;
                break;
            case SPONSOR_CONTAINER:
                fxmlView = FxmlView.SPONSOR;
                break;
            case VOLUNTEER_CONTAINER:
                fxmlView = FxmlView.VOLUNTEER;
                break;
            default:
                fxmlView = FxmlView.NOT_FOUND;
                break;
        }
        stageManager.switchScene(fxmlView);
    }

    @FXML
    public void onLinkOpenBrowserAction(ActionEvent event) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URL(linkOpenBrowser.getText()).toURI());
            } catch (IOException | URISyntaxException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + linkOpenBrowser.getText());
            } catch (IOException e) {
                log.warn(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
