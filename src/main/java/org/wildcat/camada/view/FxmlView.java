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
    HOME(true, false, true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/home.fxml";
        }
    },
    USER(true, false, true) {
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
            return "Crear un usuario nuevo";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/new_user.fxml";
        }
    },
    NEW_PARTNER(false, true) {
        @Override
        public String getTitle() {
            return "Crear un socio nuevo";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/new_partner.fxml";
        }
    },
    EMAIL(false, true) {
        @Override
        public String getTitle() {
            return "Envío de correo electrónico";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/email.fxml";
        }
    },
    PARTNER(true, false, true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/partner.fxml";
        }
    },
    SPONSOR(true, false, true) {
        @Override
        public String getTitle() {
            return "La Camada - " + getStringFromResourceBundle("home.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/sponsor.fxml";
        }
    },
    VOLUNTEER(true, false, true) {
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
    private boolean maximized;
    private String label;

    FxmlView(boolean resizable) {
        this.resizable = resizable;
        this.label = name();
        this.modal = false;
        this.maximized = false;
    }

    FxmlView(boolean resizable, boolean modal) {
        this.resizable = resizable;
        this.label = name();
        this.modal = modal;
        this.maximized = false;
    }

    FxmlView(boolean resizable, boolean modal, boolean maximized) {
        this.resizable = resizable;
        this.label = name();
        this.modal = modal;
        this.maximized = maximized;
    }

    public boolean isModal() {
        return this.modal;
    }

    public boolean isResizable() {
        return this.resizable;
    }

    public boolean isMaximized() {
        return this.maximized;
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