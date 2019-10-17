package org.wildcat.camada.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "camada_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamadaUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "activation_date")
    private Date activationDate;
    @Column(name = "last_connection")
    private Date lastConnection;
    @Column(name = "is_admin")
    private Boolean isAdmin;
    @Column(name = "is_volunteer")
    private Boolean isVolunteer;
    @Column(name = "is_virtual_sponsor")
    private Boolean isVirtualSponsor;
    @Column(name = "is_presential_sponsor")
    private Boolean isPresentialSponsor;
    @Column(name = "is_partner")
    private Boolean isPartner;
    @Column(name = "is_active")
    private Boolean isActive;
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "camada_user_custom_query"
            , joinColumns = {@JoinColumn(name = "camada_user_id")}
            , inverseJoinColumns = {@JoinColumn(name = "custom_query_id")})
    Set<CustomQuery> customQueries = new HashSet<>();
}
