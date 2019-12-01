package org.wildcat.camada.persistence.dto;

import lombok.*;
import org.wildcat.camada.persistence.PaymentFrequency;

import java.io.Serializable;
import java.util.Date;

//@Entity
//@Table(name = "partner_view")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerView implements Serializable {
    private static final long serialVersionUID = 23425L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")

    private Long id;
//    @Column(name = "amount")
    private Double amount;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "payment_frequency")
    private PaymentFrequency paymentFrequency;
//    @Column(name = "camada_id")
    private Long camadaId;
//    @Column(name = "personal_data_id")
    private Long personalDataId;
//    @Column(name = "dni")
    private String dni;
//    @Column(name = "name")
    private String name;
//    @Column(name = "surnames")
    private String surnames;
//    @Column(name = "birth_date")
    private Date birthDate;
//    @Column(name = "address")
    private String address;
//    @Column(name = "location")
    private String location;
//    @Column(name = "province")
    private String province;
//    @Column(name = "post_code")
    private String postCode;
//    @Column(name = "phone1")
    private String phone1;
//    @Column(name = "phone2")
    private String phone2;
//    @Column(name = "email")
    private String email;
//    @Column(name = "banking_data_id")
    private Long bankingDataId;
//    @Column(name = "iban")
    private String iban;
//    @Column(name = "bank_name")
    private String bankName;
//    @Column(name = "bank_surnames")
    private String bankSurnames;
}
