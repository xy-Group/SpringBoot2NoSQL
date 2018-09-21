package xy.spring2nosql.repository.Redis;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import xy.spring2nosql.model.Redis.User;

@Repository
public class UserRepository {
	
//	@Autowired
//	RedisTemplate<String, User> redisTemplate;
//
//	@Resource(name = "redisTemplate")
//	ValueOperations<String, User> valOps;
//
//	public void save(User user) {
//		valOps.set(user.getLogin(), user);
//	}
//
//	public User get(String id) {
//		return valOps.get(id);
//	}

	
}