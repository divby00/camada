package org.wildcat.camada.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "custom_query")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "query")
    private String query;
    @Column(name = "available_for_all")
    private Boolean availableForAll;
    @Column(name = "section")
    private String section;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "camada_user_custom_query"
            , joinColumns = {@JoinColumn(name = "custom_query_id")}
            , inverseJoinColumns = {@JoinColumn(name = "camada_user_id")})
    Set<CamadaUser> camadaUsers = new HashSet<>();
}
