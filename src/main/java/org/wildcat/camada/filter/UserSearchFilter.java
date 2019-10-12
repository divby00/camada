package org.wildcat.camada.filter;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSearchFilter implements Serializable {
    private String userName;
    private String name;
    private String surname;
    private String email;
    private Boolean isAdmin;
    private Boolean isPartner;
    private Boolean isVolunteer;
    private Boolean isVirtualSponsor;
    private Boolean isPresentialSponsor;
    private Boolean isActive;
}
