package org.wildcat.camada.persistence.dto;

import lombok.*;
import org.wildcat.camada.persistence.PaymentFrequency;
import org.wildcat.camada.persistence.entity.Subscription;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDTO implements Serializable {
    private static final long serialVersionUID = 23425L;
    private Long id;
    private Double amount;
    private PaymentFrequency paymentFrequency;
    private String camadaId;
    private Boolean active;
    private Long personalDataId;
    private String dni;
    private String name;
    private String surnames;
    private Date birthDate;
    private String address;
    private String location;
    private String province;
    private String postCode;
    private String phone1;
    private String phone2;
    private String email;
    private Long bankingDataId;
    private String iban;
    private String bankName;
    private String bankSurnames;
    private List<Subscription> subscriptions;

    public PartnerDTO(Long id, Double amount, PaymentFrequency paymentFrequency, String camadaId, Boolean active, Long personalDataId, String dni, String name,
            String surnames, Date birthDate, String address, String location, String province, String postCode, String phone1, String phone2, String email,
            Long bankingDataId, String iban, String bankName, String bankSurnames) {
        this.id = id;
        this.amount = amount;
        this.paymentFrequency = paymentFrequency;
        this.camadaId = camadaId;
        this.active = active;
        this.personalDataId = personalDataId;
        this.dni = dni;
        this.name = name;
        this.surnames = surnames;
        this.birthDate = birthDate;
        this.address = address;
        this.location = location;
        this.province = province;
        this.postCode = postCode;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
        this.bankingDataId = bankingDataId;
        this.iban = iban;
        this.bankName = bankName;
        this.bankSurnames = bankSurnames;
    }

}
