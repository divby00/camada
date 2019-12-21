package org.wildcat.camada.common.enumerations;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import jfxtras.scene.control.CalendarTextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.wildcat.camada.persistence.PaymentFrequency;
import org.wildcat.camada.persistence.dto.PartnerDTO;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.persistence.entity.Partner;
import org.wildcat.camada.service.PersistenceService;

import java.util.Date;

@Slf4j
public enum CustomTableColumn implements TextFieldTableColumn, CheckBoxTableColumn, DateTableColumn {
    PARTNER_NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setName(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setName(oldValue);
        }
    },
    PARTNER_SURNAMES {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getSurnames();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setSurnames(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setSurnames(oldValue);
        }
    },
    PARTNER_BIRTHDATE {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return DateFormatUtils.format(((PartnerDTO) event.getRowValue()).getBirthDate(), "dd/MM/yyyy");
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            try {
                Date date = DateUtils.parseDate(oldValue, "dd/MM/yyyy");
                ((PartnerDTO) event.getRowValue()).setBirthDate(date);

            } catch (Exception ex) {
                log.error(ExceptionUtils.getStackTrace(ex));
            }
        }

        @Override
        public <T> void setDate(T item, CalendarTextField datePicker, PersistenceService service) {
            Date date = datePicker.getCalendar().getTime();
            ((PartnerDTO) item).setBirthDate(date);
            Long id = ((PartnerDTO) item).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                try {
                    partner.getPersonalData().setBirthDate(date);
                    service.saveEntity(partner);
                } catch (Exception ex) {
                    log.error(ExceptionUtils.getStackTrace(ex));
                }
            }
        }
    },
    PARTNER_DNI {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getDni();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setDni(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setDni(oldValue);
        }
    },
    PARTNER_ADDRESS {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getAddress();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setAddress(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setAddress(oldValue);
        }
    },
    PARTNER_POST_CODE {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getPostCode();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setPostCode(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setPostCode(oldValue);
        }
    },
    PARTNER_LOCATION {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getLocation();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setLocation(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setLocation(oldValue);
        }
    },
    PARTNER_PROVINCE {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getProvince();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setProvince(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setProvince(oldValue);
        }
    },
    PARTNER_PHONE1 {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getPhone1();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setPhone1(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setPhone1(oldValue);
        }
    },
    PARTNER_PHONE2 {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getPhone2();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setPhone2(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setPhone2(oldValue);
        }
    },
    PARTNER_EMAIL {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getEmail();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getPersonalData().setEmail(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setEmail(oldValue);
        }
    },
    PARTNER_IBAN {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getIban();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getBankingData().setIban(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setIban(oldValue);
        }
    },
    PARTNER_BANK_NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getBankName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getBankingData().setName(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setBankName(oldValue);
        }
    },
    PARTNER_BANK_SURNAMES {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getBankSurnames();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.getBankingData().setSurnames(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setBankSurnames(oldValue);
        }
    },
    PARTNER_AMOUNT {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getAmount();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.setAmount(newValue);
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setAmount(oldValue);
        }
    },
    PARTNER_PAYMENT_FREQUENCY {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((PartnerDTO) event.getRowValue()).getPaymentFrequency().name();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
            Long id = ((PartnerDTO) event.getRowValue()).getId();
            Partner partner = (Partner) service.find(id);
            if (partner != null) {
                partner.setPaymentFrequency(PaymentFrequency.valueOf(newValue));
                service.saveEntity(partner);
            }
        }

        @Override
        public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
            ((PartnerDTO) event.getRowValue()).setPaymentFrequency(PaymentFrequency.valueOf(oldValue));
        }
    },
    NAME {
        @Override
        public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
            return ((CamadaUser) event.getRowValue()).getName();
        }

        @Override
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
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
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
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
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
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
        public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
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
        public <T> void setBoolean(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsAdmin(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBoolean(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsAdmin();
        }
    },
    IS_VIRTUAL_SPONSOR {
        @Override
        public <T> void setBoolean(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsVirtualSponsor(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBoolean(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsVirtualSponsor();
        }

    },
    IS_PRESENTIAL_SPONSOR {
        @Override
        public <T> void setBoolean(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsPresentialSponsor(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBoolean(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsPresentialSponsor();
        }

    },
    IS_PARTNER {
        @Override
        public <T> void setBoolean(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsPartner(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBoolean(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsPartner();
        }
    },
    IS_VOLUNTEER {
        @Override
        public <T> void setBoolean(T item, CheckBox checkBox) {
            ((CamadaUser) item).setIsVolunteer(checkBox.isSelected());
        }

        @Override
        public <T> Boolean getBoolean(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
            return ((CamadaUser) cellDataFeatures.getValue()).getIsVolunteer();
        }
    };

    @Override
    public <T> String getOldValue(TableColumn.CellEditEvent<T, String> event) {
        return null;
    }

    @Override
    public <T> void setNewValue(TableColumn.CellEditEvent<T, String> event, String newValue, PersistenceService service) {
    }

    @Override
    public <T> void setOldValue(TableColumn.CellEditEvent<T, String> event, String oldValue) {
    }

    @Override
    public <T> void setBoolean(T item, CheckBox checkBox) {
    }

    @Override
    public <T> Boolean getBoolean(TableColumn.CellDataFeatures<T, Boolean> cellDataFeatures) {
        return null;
    }

    public <T> void setDate(T item, CalendarTextField datePicker, PersistenceService persistenceService) {
    }
}
