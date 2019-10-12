package org.wildcat.camada.controller;

import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.service.CamadaUserService;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class SponsorController implements Initializable {

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Resource
    private final CamadaUserService camadaUserService;

    public SponsorController(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
