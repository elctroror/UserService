package com.UserService.UserService.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String role;
    private static final long serialVersionUID = -5297634001124676991L;

}
