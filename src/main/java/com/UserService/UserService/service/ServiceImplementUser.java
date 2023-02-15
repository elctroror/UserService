package com.UserService.UserService.service;

import com.UserService.UserService.activeMQ.Receive;
import com.UserService.UserService.dao.UserDao;
import com.UserService.UserService.entity.User;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceImplementUser implements ServiceUser {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${queue.example}")
    private String  userQueue;

    private static String PATTERN_DNI = "[0-9]{7,8}";

    private static String PATTERN_Name = "[a-z]+[^\s]{1,20}";
    @Override
    public List<User> findAll() {

        List<User> userList = (List<User>) userDao.findAll();
        List<User> userActiveList=new ArrayList<>();
        for(int i=0; i< userList.size(); i++){

            if(userList.get(i).getActive()==true){
                userActiveList.add(userList.get(i));
            }
        }
        return  userActiveList;
    }

    @Override
    public ResponseEntity findById(Long id) {

        try {
            Optional<User> user = userDao.findById(id);
            if(user.isPresent() && user.get().getActive()==true){
                return new ResponseEntity(user, HttpStatus.ACCEPTED) ;
            }
            return new ResponseEntity("The user does not exist", HttpStatus.BAD_REQUEST) ;

        }catch (Exception e){
            System.out.println("Exception"+e);
            return new ResponseEntity("Something go bad", HttpStatus.BAD_REQUEST) ;
        }

    }

    @Override
    public ResponseEntity disable(Long id) {
        try {
            Optional<User> user = userDao.findById(id);
            if(user.isPresent() && user.get().getActive()==true){

                user.get().setActive(false);
                userDao.save(user.get());
              return new ResponseEntity (user, HttpStatus.ACCEPTED);
            }
            return new ResponseEntity ("cannot disable the user", HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            System.out.println("Exception: "+e);
            return new ResponseEntity ("something go bad", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public Boolean findActive(Long id) {
         try {
             Optional<User> user = userDao.findById(id);
             if(user.isPresent() && user.get().getActive()==true){

                 return true;
             }
             throw new RuntimeException("could not find the User");

         }catch (Exception e){
             System.out.println("Exception: "+e);
             return false;
         }

    }

    @Override
    public ResponseEntity createUser(String name, String secondName, String dni, String email, String pass) {
        try{


              Boolean bname =name.matches(PATTERN_Name);
              Boolean bsecondName =secondName.matches(PATTERN_Name);

            if(bname&&bsecondName){
               Boolean pdni = dni.matches(PATTERN_DNI);
               if(pdni) {
                   int intDni = Integer.parseInt(dni);
                   User user = new User(Long.parseLong("1"),name, secondName, dni, email, pass);
                   JSONObject userJson = new JSONObject(user);

                   jmsTemplate.send(userQueue, new MessageCreator() {
                       @Override
                       public Message createMessage(Session session) throws JMSException {
                            ObjectMessage objectMessage = session.createObjectMessage(userJson.toString());
                            return objectMessage;
                       }
                   });

                   return new ResponseEntity("User Created", HttpStatus.ACCEPTED);
               }
                return new ResponseEntity("dni must be 7-8 numbers", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity("Name or secondName not valid", HttpStatus.BAD_REQUEST);
        }catch(NumberFormatException n){
            return new ResponseEntity("the dni must be numeric", HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            System.out.println("Exception: "+e);
            return new ResponseEntity("something go bad", HttpStatus.BAD_REQUEST);
        }


    }


}
