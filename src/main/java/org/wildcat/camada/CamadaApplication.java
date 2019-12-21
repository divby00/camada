package org.wildcat.camada;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.wildcat.camada.config.StageManager;
import org.wildcat.camada.view.FxmlView;

@SpringBootApplication
public class CamadaApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private StageManager stageManager;

    public static void main(final String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CamadaApplication.class);
        String[] args = getParameters().getRaw().toArray(new String[0]);
        springContext = builder.run(args);
    }

    @Override
    public void start(Stage stage) {
        stageManager = springContext.getBean(StageManager.class, stage);
        displayInitialScene();
    }

    @Override
    public void stop() {
        springContext.close();
    }

    /**
     * Useful to override this method by sub-classes wishing to change the first
     * Scene to be displayed on startup. Example: Functional tests on main
     * window.
     */
    protected void displayInitialScene() {
        stageManager.switchScene(FxmlView.LOGIN);
    }

}
