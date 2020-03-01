package org.wildcat.camada.config;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.wildcat.camada.view.FxmlView;

import java.util.Objects;

@Slf4j
public class StageManager {

    private final Stage primaryStage;
    private Stage modalStage;
    private final SpringFXMLLoader springFXMLLoader;
    private FxmlView view;

    public StageManager(SpringFXMLLoader springFXMLLoader, Stage stage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = stage;
    }

    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        this.view = view;
        if (view.isModal()) {
            showModalScene(viewRootNodeHierarchy, view);
        } else {
            show(viewRootNodeHierarchy, view);
        }
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public Stage getModalStage() {
        return this.modalStage;
    }

    public FxmlView getView() {
        return this.view;
    }

    private void show(final Parent rootnode, FxmlView view) {
        Scene scene = prepareScene(rootnode);
        primaryStage.setTitle(view.getTitle());
        primaryStage.setScene(scene);

        boolean isMaximized = view.isMaximized();
        if (isMaximized) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setMinWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
            primaryStage.setMinHeight(bounds.getHeight());
            primaryStage.setMaxWidth(bounds.getWidth());
            primaryStage.setMaxHeight(bounds.getHeight());
        } else {
            double prefWidth = ((Pane) rootnode).getPrefWidth();
            double prefHeight = ((Pane) rootnode).getPrefHeight() + 30;
            primaryStage.setWidth(prefWidth);
            primaryStage.setMaxWidth(prefWidth);
            primaryStage.setMinWidth(prefWidth);
            primaryStage.setHeight(prefHeight);
            primaryStage.setMaxHeight(prefHeight);
            primaryStage.setMinHeight(prefHeight);
        }
        primaryStage.setMaximized(isMaximized);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(view.isResizable());
        primaryStage.getIcons().add(view.getIcon());

        try {
            primaryStage.show();
        } catch (Exception exception) {
            logAndExit("Unable to show scene for title" + view.getTitle(), exception);
        }
    }

    private void showModalScene(Parent rootnode, FxmlView view) {
        modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(primaryStage);
        modalStage.setTitle(view.getTitle());
        modalStage.setResizable(view.isResizable());
        modalStage.getIcons().add(view.getIcon());
        Scene scene = new Scene(rootnode);
        modalStage.setScene(scene);
        modalStage.show();
    }

    private Scene prepareScene(Parent rootNode) {
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);
        return scene;
    }

    /**
     * Loads the object hierarchy from a FXML document and returns to root node
     * of that hierarchy.
     *
     * @return Parent root node of the FXML document hierarchy
     */
    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            rootNode = springFXMLLoader.load(fxmlFilePath);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception exception) {
            logAndExit("Unable to load FXML view: " + fxmlFilePath, exception);
        }
        return rootNode;
    }

    private void logAndExit(String errorMsg, Exception exception) {
        log.error(errorMsg + ": " + ExceptionUtils.getStackTrace(exception));
        Platform.exit();
    }

}
