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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

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
    private Double amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency")
    private PaymentFrequency paymentFrequency;
    @Column(name = "camada_id")
    private String camadaId;
    @Column(name = "active")
    private Boolean active;
    @OneToOne(orphanRemoval = true)
    private PersonalData personalData;
    @OneToOne(orphanRemoval = true)
    private BankingData bankingData;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_partner")
    private List<Subscription> subscriptions;
}

