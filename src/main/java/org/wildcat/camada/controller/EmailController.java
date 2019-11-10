package org.wildcat.camada.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Controller
public class EmailController implements Initializable {

    @FXML
    private Button saveTemplateButton;

    @FXML
    private Button sendEmailButton;

    @FXML
    private HTMLEditor htmlEditor;

    @FXML
    private ComboBox comboEmailTemplates;

    @FXML
    private TextArea areaTo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void onSendEmailButtonAction(ActionEvent event) {

    }

    @FXML
    public void onSaveTemplateButtonAction(ActionEvent event) {

    }
}
