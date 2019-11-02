package org.wildcat.camada.view;

import javafx.scene.image.Image;

import java.util.Objects;
import java.util.ResourceBundle;

public enum FxmlView {

    LOGIN(false) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("login.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/login.fxml";
        }
    },
    HOME(true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/home.fxml";
        }
    },
    USER(true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/user.fxml";
        }
    },
    NEW_USER(false, true) {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("user.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/new_user.fxml";
        }
    },
    PARTNER(true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/partner.fxml";
        }
    },
    SPONSOR(true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/sponsor.fxml";
        }
    },
    VOLUNTEER(true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/volunteer.fxml";
        }
    },
    NOT_FOUND(true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/volunteer.fxml";
        }
    };

    public abstract String getTitle();

    public abstract String getFxmlFile();

    private boolean resizable;
    private boolean modal;
    private String label;

    FxmlView(boolean resizable) {
        this.resizable = resizable;
        this.label = name();
        this.modal = false;
    }

    FxmlView(boolean resizable, boolean modal) {
        this.resizable = resizable;
        this.label = name();
        this.modal = modal;
    }

    public boolean isModal() {
        return this.modal;
    }

    public boolean isResizable() {
        return this.resizable;
    }

    public Image getIcon() {
        return new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("camada_small.png")));
    }

    public String getLabel() {
        return this.label;
    }


    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("camada").getString(key);
    }

}