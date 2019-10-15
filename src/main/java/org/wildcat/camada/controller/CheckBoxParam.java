package org.wildcat.camada.controller;

import javafx.scene.control.CheckBox;
import org.wildcat.camada.entity.CamadaUser;

public enum CheckBoxParam {

    IS_ADMIN {
        @Override
        public void apply(CamadaUser item, CheckBox checkBox) {
            item.setIsAdmin(checkBox.isSelected());
        }
    },
    IS_PARTNER {
        @Override
        public void apply(CamadaUser item, CheckBox checkBox) {
            item.setIsPartner(checkBox.isSelected());
        }
    },
    IS_VIRTUAL_SPONSOR {
        @Override
        public void apply(CamadaUser item, CheckBox checkBox) {
            item.setIsVirtualSponsor(checkBox.isSelected());
        }
    },
    IS_PRESENTIAL_SPONSOR {
        @Override
        public void apply(CamadaUser item, CheckBox checkBox) {
            item.setIsPresentialSponsor(checkBox.isSelected());
        }
    },
    IS_VOLUNTEER {
        @Override
        public void apply(CamadaUser item, CheckBox checkBox) {
            item.setIsVolunteer(checkBox.isSelected());
        }
    };

    public abstract void apply(CamadaUser item, CheckBox checkBox);
}
