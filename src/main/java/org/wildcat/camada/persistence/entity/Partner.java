package org.wildcat.camada.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.wildcat.camada.persistence.PaymentFrequency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

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
    @Column(name = "amount")
    private String amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency")
    private PaymentFrequency paymentFrequency;
    @Column(name = "camada_id")
    private String camadaId;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "subscribed_from")
    private Date subscribedFrom;
    @Column(name = "subscribed_to")
    private Date subscribedTo;
    @OneToOne(orphanRemoval = true)
    private PersonalData personalData;
    @OneToOne(orphanRemoval = true)
    private BankingData bankingData;
}

