package xy.SpringBoot2NoSQL.controller.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xy.SpringBoot2NoSQL.model.Redis.User;
import xy.SpringBoot2NoSQL.repository.Redis.ObjectRepository;
import xy.SpringBoot2NoSQL.repository.Redis.StringStringRepository;
import xy.SpringBoot2NoSQL.repository.Redis.UserRepository;

@RestController
@RequestMapping("/redis")
public class RedisDataController {

    @Autowired
    ObjectRepository objRepository;
    @Autowired
    StringStringRepository stringStringRepository;

    @RequestMapping("/add/{name}")
    public String getRecognition(@PathVariable("name") String name){
    	User user = new User(name,name);
    	objRepository.save(user);

        return "add success.";
    }
    
    @RequestMapping("/user/{name}")
    public User getUser(@PathVariable("name") String name){
    	return (User)objRepository.get(name);
    	
    }
}
