package com.UserService.UserService.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Column
    private String role;
    private static final long serialVersionUID = -5297634001124676991L;

}
