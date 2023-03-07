package com.UserService.UserService.dao;

import com.UserService.UserService.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {

     User findByName(String name);
}
