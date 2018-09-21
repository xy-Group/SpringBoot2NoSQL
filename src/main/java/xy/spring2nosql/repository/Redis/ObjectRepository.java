package xy.spring2nosql.repository.Redis;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import xy.spring2nosql.model.Redis.User;

@Repository
public class ObjectRepository {
	@Autowired
	RedisTemplate<Object, Object> redisTemplate;

	@Resource(name = "redisTemplate")
	ValueOperations<Object, Object> valOps;

	public void save(User user) {
		valOps.set(user.getLogin(), user);
	}

	public User get(String id) {
		return (User) valOps.get(id);
	}
}
