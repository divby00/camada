package org.wildcat.camada.enumerations;

public enum CamadaQuery {
    ACTIVE_AND_INACTIVE_USERS("user"),
    ACTIVE_USERS("user"),
    INACTIVE_USERS("user");

    private String section;

    CamadaQuery(String section) {
        this.section = section;
    }

    public String getSection() {
        return this.section;
    }

}
