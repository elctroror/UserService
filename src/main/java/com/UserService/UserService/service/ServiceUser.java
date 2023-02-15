package com.UserService.UserService.service;

import com.UserService.UserService.dao.UserDao;
import com.UserService.UserService.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ServiceUser {

    public List<User> findAll();
    public ResponseEntity findById(Long id);
    public ResponseEntity disable(Long id);
    public Boolean findActive(Long id);
    public ResponseEntity createUser(String name,String secondName,String dni,String email,String pass);

}
