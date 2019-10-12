package org.wildcat.camada.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamadaUserFilterForm implements Serializable {
    private String filterUserName;
    private String filterName;
    private String filterSurname;
    private String filterEmail;
    private Boolean filterAdmin;
    private Boolean filterPartner;
    private Boolean filterVolunteer;
    private Boolean filterVirtualSponsor;
    private Boolean filterPresentialSponsor;
}
