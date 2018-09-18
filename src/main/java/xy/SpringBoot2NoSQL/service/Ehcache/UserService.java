package xy.SpringBoot2NoSQL.service.Ehcache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import xy.SpringBoot2NoSQL.model.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@CacheConfig(cacheNames="users")
public class UserService {
	
	Logger logger = LogManager.getLogger(getClass());
	
    @CacheEvict(allEntries = true)
    public void clearCache(){}

	public User getUser(String login) {
		// 获取缓存实例
		Cache userCache = CacheManager.getInstance().getCache("users");
		
		if (userCache.get(login)!=null){
			
			User user = (User) userCache.get(login).getObjectValue();			
			logger.info("从缓存获取user用户: {}", login);
			logger.info("从缓存JVM内存中获取获取user用户: {}-{}",login, userCache.isElementInMemory(login));
			logger.info("从缓存OffHeap内存中获取获取user用户: {}-{}",login, userCache.isElementOffHeap(login));
			logger.info("已持久化磁盘，用户: {}-{}",login, userCache.isElementOnDisk(login));
			
			return user;
		}
		else{
			User user = new User(login,login+"的姓名");
			logger.info("新user用户: {}", login);
			// 写入缓存,注意我们故意设置maxElementsInMemory内存最大值是5
			Element element = new Element(login, user);
			userCache.put(element);
			
			return user;
		}
		
	}

}
