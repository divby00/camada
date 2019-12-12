package org.wildcat.camada.persistence;

public enum PaymentFrequency {
    MONTHLY("Mensual"),
    QUATERLY("Trimestral"),
    YEARLY("Anual");

    private String label;

    PaymentFrequency(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
