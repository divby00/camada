package org.wildcat.camada.persistence.entity;

import lombok.*;
import org.wildcat.camada.persistence.PaymentFrequency;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "partner")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partner implements Serializable {
    private static final long serialVersionUID = 5L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "camada_id")
    private String camadaId;
    @Column(name = "amount")
    private Double amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency")
    private PaymentFrequency paymentFrequency;
    @OneToOne(orphanRemoval = true)
    private PersonalData personalData;
    @OneToOne(orphanRemoval = true)
    private BankingData bankingData;
}
