package org.wildcat.camada.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.service.CamadaUserService;
import org.wildcat.camada.view.FxmlView;

import javax.annotation.Resource;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import static org.wildcat.camada.utils.GetUtils.get;

@Controller
public class DashboardController implements Initializable {

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

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    public DashboardController(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String greetingMessage = resources.getString("dashboard.greeting");
        String defaultGreetingMessage = resources.getString("dashboard.greeting.default");
        greeting.setText(MessageFormat.format(greetingMessage, get(() -> camadaUserService.getUser().getFirstName(), defaultGreetingMessage)));
        boolean isAdmin = camadaUserService.getUser().getIsAdmin();
        boolean showSponsorContainer = camadaUserService.getUser().getIsPresentialSponsor() || camadaUserService.getUser().getIsVirtualSponsor();
        boolean showPartnerContainer = camadaUserService.getUser().getIsPartner();
        boolean showVolunteerContainer = camadaUserService.getUser().getIsVolunteer();
        actionContainer.getChildren().clear();
        if (isAdmin) {
            actionContainer.getChildren().add(userContainer);
        }
        if (showPartnerContainer || isAdmin) {
            actionContainer.getChildren().add(partnerContainer);
        }
        if (showSponsorContainer || isAdmin) {
            actionContainer.getChildren().add(sponsorContainer);
        }
        if (showVolunteerContainer || isAdmin) {
            actionContainer.getChildren().add(volunteerContainer);
        }
        if (actionContainer.getChildren().size() < 1) {
            whatToDo.setText(resources.getString("dashboard.nopermission"));
        }
    }

    public void managementButtonClicked(ActionEvent event) {
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
}
