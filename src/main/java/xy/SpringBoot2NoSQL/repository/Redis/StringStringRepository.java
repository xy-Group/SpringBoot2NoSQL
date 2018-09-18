package xy.SpringBoot2NoSQL.repository.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StringStringRepository {
	
	@Autowired
	private RedisTemplate<String, String> template;	
	
	public void add(String key, String value) {
		template.opsForValue().set(key, value);
	}
	
	public String getValue(String key) {
		return template.opsForValue().get(key);
	}
	
	public void delete(String key) {
		template.opsForValue().getOperations().delete(key);
	}
	
}