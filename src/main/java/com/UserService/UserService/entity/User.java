package com.UserService.UserService.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;


@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String secondName;

    @Column
    private String dni;

    @Column
    private String email;

    @Column
    private String pass;

    @Column
    private Boolean active=true;

    @ManyToMany(fetch =FetchType.LAZY)
    private List<Role> roleList;

    private static final long serialVersionUID = -6702360514758892391L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void deleteId() {
        this.id = null;
    }



    public User(){}
    public User(String name, String secondName, String dni, String email, String pass ){

       this.name=name;
       this.secondName=secondName;
       this.dni=dni;
       this.email=email;
       this.pass=pass;


   }

    public User(Long id,String name, String secondName, String dni, String email, String pass ){
        this.id=id;
        this.name=name;
        this.secondName=secondName;
        this.dni=dni;
        this.email=email;
        this.pass=pass;


    }



}
