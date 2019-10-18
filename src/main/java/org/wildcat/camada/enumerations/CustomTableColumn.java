package org.wildcat.camada.enumerations;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import org.wildcat.camada.entity.CamadaUser;
import org.wildcat.camada.service.SavingService;

public enum CustomTableColumn implements TextFieldTableColumn, CheckBoxTableColumn {
    NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((CamadaUser) event.getRowValue()).getName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((CamadaUser) event.getRowValue()).setName(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((CamadaUser) event.getRowValue()).setName(oldValue);
        }
    },
    FIRST_NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((CamadaUser) event.getRowValue()).getFirstName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((CamadaUser) event.getRowValue()).setFirstName(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((CamadaUser) event.getRowValue()).setFirstName(oldValue);
        }
    },
    LAST_NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((CamadaUser) event.getRowValue()).getLastName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((CamadaUser) event.getRowValue()).setLastName(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((CamadaUser) event.getRowValue()).setLastName(oldValue);
        }
    },
    EMAIL {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((CamadaUser) event.getRowValue()).getEmail();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((CamadaUser) event.getRowValue()).setEmail(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((CamadaUser) event.getRowValue()).setEmail(oldValue);
        }
    },
    IS_ADMIN {
        @Override
        public <T> void setBooleanProperty(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsAdmin(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBooleanProperty(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsAdmin();
        }
    },
    IS_VIRTUAL_SPONSOR {
        @Override
        public <T> void setBooleanProperty(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsVirtualSponsor(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBooleanProperty(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsVirtualSponsor();
        }

    },
    IS_PRESENTIAL_SPONSOR {
        @Override
        public <T> void setBooleanProperty(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsPresentialSponsor(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBooleanProperty(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsPresentialSponsor();
        }

    },
    IS_PARTNER {
        @Override
        public <T> void setBooleanProperty(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsPartner(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBooleanProperty(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsPartner();
        }
    },
    IS_VOLUNTEER {
        @Override
        public <T> void setBooleanProperty(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsVolunteer(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBooleanProperty(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsVolunteer();
        }
    };

    @Override
    public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
        return null;
    }

    @Override
    public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {

    }

    @Override
    public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {

    }

    @Override
    public <T> void setBooleanProperty(T item, CheckBox checkBox) {

    }

    @Override
    public <T> Boolean getBooleanProperty(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
        return null;
    }

}
