package org.wildcat.camada.persistence.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "email_template")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate implements Serializable {
    private static final long serialVersionUID = 2L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "content", length = 16000)
    private String content;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "is_public")
    private Boolean isPublic;
}
