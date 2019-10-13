package org.wildcat.camada.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "collaborator")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Collaborator implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "surname1")
    private String surname1;
    @Column(name = "surname2")
    private String surname2;
    @Column(name = "dni")
    private String dni;
    @Column(name = "address")
    private String address;
    @Column(name = "post_code")
    private Long postCode;
    @Column(name = "location")
    private String location;
    @Column(name = "province")
    private String province;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "entry_date")
    private Date entryDate;
    @Column(name = "leaving_date")
    private Date leavingDate;
    @Column(name = "observations")
    private String observations;
    @Column(name = "birth_date")
    private Date birthDate;
}
