package com.UserService.UserService.activeMQ;
import com.UserService.UserService.dao.UserDao;
import com.UserService.UserService.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
@Service
public class Receive {

 @Autowired
 UserDao userDao;

    @JmsListener(destination = "UserQueue")
    public void receiveMessage(String message){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JSONObject userJson = new JSONObject(message);
            byte[] jsonData = userJson.toString().getBytes();

            User user = mapper.readValue(jsonData, User.class);
            user.deleteId();
            System.out.println("USER: "+user);
           userDao.save(user);
        } catch (IOException e) {
            System.out.println(e);
        }
        catch (Exception f){
            System.out.println(f);
          throw f;
        }

        /*try (Reader reader = new FileReader("C:\\Users\\joel.favero\\Downloads\\UserService\\src\\main\\java\\com\\UserService\\UserService\\entity\\User.java")) {
            userDao.save(message.fromJson(reader, User.class));
        }catch (Exception e){

        }*/
    }
}
