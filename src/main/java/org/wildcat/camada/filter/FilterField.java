package org.wildcat.camada.filter;


public enum FilterField {

    STRING("as"),
    BOOLEAN,
    NUMBER,
    DATE;

    private String value;

    FilterField() {
        this.value = name();
    }

    FilterField(String value) {
        this.value = value;
    }

}
