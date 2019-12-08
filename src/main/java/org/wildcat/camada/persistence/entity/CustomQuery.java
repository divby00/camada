package org.wildcat.camada.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @Column(name = "zorder")
    private Integer zorder;
}
