package com.UserService.UserService.controller;

import com.UserService.UserService.entity.User;
import com.UserService.UserService.service.ServiceImplementUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class ControllerUser {

    @Autowired
    private ServiceImplementUser serviceUser;


    @GetMapping("/list")
    public List<User> list(){
        return serviceUser.findAll();
    }

    @GetMapping("/list/{id}")
    public ResponseEntity idList(@PathVariable Long id){
        return serviceUser.findById(id);
    }

    @GetMapping("/findActive/{id}")
    public Boolean findActive(@PathVariable Long id){
        return serviceUser.findActive(id);
    }

    @GetMapping("/disable/{id}")
    public ResponseEntity disable(@PathVariable Long id){
        return serviceUser.disable(id);
    }

    @RequestMapping(value= "/create/{name}/{secondName}/{dni}/{email}/{pass}" , method = RequestMethod.GET)
    public ResponseEntity create(@PathVariable("name") String name, @PathVariable("secondName") String secondName, @PathVariable("dni") String dni, @PathVariable("email") String email, @PathVariable("pass") String pass){
        return serviceUser.createUser(name,secondName,dni,email,pass);
    }



}
