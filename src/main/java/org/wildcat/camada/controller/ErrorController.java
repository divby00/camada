package org.wildcat.camada.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.service.ErrorService;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ErrorController implements Initializable {

    @FXML
    private Button sendErrorReportButton;

    @FXML
    private Button closeApplicationButton;

    @FXML
    private TextArea errorMessageTextArea;

    @Resource
    private final ErrorService errorService;

    @Lazy
    @Autowired
    protected StageManager stageManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Throwable throwable = (Throwable) stageManager.getPrimaryStage().getUserData();
        String message = errorService.getErrorMessage(throwable);
        errorMessageTextArea.setText(message);
    }

    public ErrorController(ErrorService errorService) {
        this.errorService = errorService;
    }
}
