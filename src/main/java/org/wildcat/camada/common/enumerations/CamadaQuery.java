package org.wildcat.camada.common.enumerations;

public enum CamadaQuery {
    ACTIVE_AND_INACTIVE_USERS("user"),
    ACTIVE_USERS("user"),
    INACTIVE_USERS("user"),
    ALL_PARTNERS("partner"),
    NEW_PARTNERS("partner"),
    NEXT_PAYMENTS_PARTNERS("partner"),
    ALL_FORMER_PARTNERS("partner"),
    LAST_MONTH_FORMER_PARTNERS("partner");

    private String section;

    CamadaQuery(String section) {
        this.section = section;
    }

    public String getSection() {
        return this.section;
    }

}
