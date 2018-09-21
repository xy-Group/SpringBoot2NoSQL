package xy.spring2nosql.controller.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xy.spring2nosql.model.Redis.User;
import xy.spring2nosql.repository.Redis.ObjectRepository;
import xy.spring2nosql.repository.Redis.StringStringRepository;
import xy.spring2nosql.repository.Redis.UserRepository;

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
