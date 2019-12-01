package org.wildcat.camada.common.enumerations;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.persistence.dto.PartnerView;
import org.wildcat.camada.service.SavingService;

public enum CustomTableColumn implements TextFieldTableColumn, CheckBoxTableColumn {
    PARTNER_NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setName(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setName(oldValue);
        }
    },
    PARTNER_SURNAMES {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getSurnames();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setSurnames(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setSurnames(oldValue);
        }
    },
    PARTNER_DNI {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getDni();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setDni(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setDni(oldValue);
        }
    },
    PARTNER_ADDRESS {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getAddress();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setAddress(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setAddress(oldValue);
        }
    },
    PARTNER_POST_CODE {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getPostCode();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setPostCode(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setPostCode(oldValue);
        }
    },
    PARTNER_LOCATION {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getLocation();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setLocation(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setLocation(oldValue);
        }
    },
    PARTNER_PROVINCE {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getProvince();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setProvince(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setProvince(oldValue);
        }
    },
    PARTNER_PHONE1 {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getPhone1();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setPhone1(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setPhone1(oldValue);
        }
    },
    PARTNER_PHONE2 {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getPhone2();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setPhone2(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setPhone2(oldValue);
        }
    },
    PARTNER_EMAIL {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getEmail();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setEmail(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setEmail(oldValue);
        }
    },
    PARTNER_IBAN {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getIban();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setIban(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setIban(oldValue);
        }
    },
    PARTNER_BANK_NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getBankName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setBankName(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setBankName(oldValue);
        }
    },
    PARTNER_BANK_SURNAMES {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerView) event.getRowValue()).getBankSurnames();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, SavingService service) {
            ((PartnerView) event.getRowValue()).setBankSurnames(newValue);
            service.saveEntity(event.getRowValue());
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerView) event.getRowValue()).setBankSurnames(oldValue);
        }
    },

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
