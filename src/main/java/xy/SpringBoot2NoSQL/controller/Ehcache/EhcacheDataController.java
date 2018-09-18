package xy.SpringBoot2NoSQL.controller.Ehcache;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.ehcache.CacheManager;
import xy.SpringBoot2NoSQL.model.User;
import xy.SpringBoot2NoSQL.service.Ehcache.UserService;

@RestController
@RequestMapping("/ehcache")
public class EhcacheDataController {
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	UserService userService;
	
	@Autowired
    private CacheManager cacheManager;
	
	/**
	 * 创建100个测试用户
	 * @return
	 */
    @RequestMapping("/users")
    public List<User> getUsers(){
    	
    	logger.info("using cache manager: " + cacheManager.getClass().getName());
    	
    	userService.clearCache();
    	
    	//新建对象
    	List<User> users = new ArrayList<User>();
    	for (int i=0;i<100;i++){
	    	users.add(userService.getUser("test"+i));
    	}
    	
    	for (int i=0;i<100;i++){
	    	//测试获取对象是否是经过缓存
	    	userService.getUser("test"+i);
    	}
    	
    	return users;   	
    }

	/**
	 * 获取测试用户，看看是否经过缓存
	 * @return
	 */
    @RequestMapping("/user/{name}")
    public User getUser(@PathVariable("name") String name){
    	return userService.getUser(name);
    	
    }
    	
}
