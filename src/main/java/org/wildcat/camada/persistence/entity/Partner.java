package org.wildcat.camada.persistence.entity;

import org.wildcat.camada.persistence.PaymentFrequency;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "partner")
public class Partner implements Serializable {
    private static final long serialVersionUID = 5L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "camada_id")
    private Long camadaId;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "payment_frequency")
    private PaymentFrequency paymentFrequency;
    @OneToOne
    private PersonalData personalData;
    @OneToOne
    private BankingData bankingData;
}
