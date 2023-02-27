package com.UserService.UserService.service;

import brave.Tracer;
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



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceImplementUser implements ServiceUser {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Tracer tracer;

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
                tracer.currentSpan().tag("user find","HttpStatus.ACCEPTED");
                return new ResponseEntity(user, HttpStatus.ACCEPTED) ;
            }
            tracer.currentSpan().tag("he user does not exist","HttpStatus.BAD_REQUEST");
            return new ResponseEntity("The user does not exist", HttpStatus.BAD_REQUEST) ;

        }catch (Exception e){
            System.out.println("Exception"+e);
            tracer.currentSpan().tag("Something go bad","HttpStatus.BAD_REQUEST");
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
                tracer.currentSpan().tag("user created","HttpStatus.ACCEPTED");
              return new ResponseEntity (user, HttpStatus.ACCEPTED);
            }
            tracer.currentSpan().tag("cannot disable the user","HttpStatus.ACCEPTED");
            return new ResponseEntity ("cannot disable the user", HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            System.out.println("Exception: "+e);
            tracer.currentSpan().tag("something go bad","HttpStatus.ACCEPTED");
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
             tracer.currentSpan().tag("could not find the User","HttpStatus.ACCEPTED");
             throw new RuntimeException("could not find the User");

         }catch (Exception e){
             tracer.currentSpan().tag("could not find the User",e.toString());
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

                  Boolean savedCorrect = saveUser(user);
                  if(savedCorrect){
                      tracer.currentSpan().tag("User Created","HttpStatus.ACCEPTED");
                      return new ResponseEntity(user, HttpStatus.ACCEPTED);
                  }
                   tracer.currentSpan().tag("user not saved","HttpStatus.BAD_REQUEST");
                   return new ResponseEntity("user not saved", HttpStatus.BAD_REQUEST);
               }
                tracer.currentSpan().tag("dni must be 7-8 numbers","HttpStatus.BAD_REQUEST");
                return new ResponseEntity("dni must be 7-8 numbers", HttpStatus.BAD_REQUEST);
            }
            tracer.currentSpan().tag("Name or secondName not valid","HttpStatus.BAD_REQUEST");
            return new ResponseEntity("Name or secondName not valid", HttpStatus.BAD_REQUEST);
        }catch(NumberFormatException n){
            tracer.currentSpan().tag("the dni must be numeric - ",n.toString());
            return new ResponseEntity("the dni must be numeric", HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            System.out.println("Exception: "+e);
            tracer.currentSpan().tag("the dni must be numeric - ",e.toString());
            return new ResponseEntity("something go bad", HttpStatus.BAD_REQUEST);
        }


    }
    private Boolean saveUser(User user){
       try{
           user.deleteId();
           userDao.save(user);
           return true;

       }catch (Exception e){
           System.out.println("Exception: "+e);
           return false;
       }


    }


}
