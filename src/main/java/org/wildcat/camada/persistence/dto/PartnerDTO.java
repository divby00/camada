package org.wildcat.camada.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.wildcat.camada.persistence.PaymentFrequency;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDTO implements Serializable {
    private static final long serialVersionUID = 23425L;
    private Long id;
    private String amount;
    private PaymentFrequency paymentFrequency;
    private String camadaId;
    private Boolean active;
    private Date subscribedFrom;
    private Date subscribedTo;
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
}
