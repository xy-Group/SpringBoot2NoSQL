package xy.spring2nosql.controller.Redis;

import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.BatchOptions;
import org.redisson.api.BatchResult;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RBatch;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RBucket;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLongAdder;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.redisson.api.map.event.EntryCreatedListener;
import org.redisson.api.map.event.EntryEvent;
import org.redisson.api.map.event.EntryExpiredListener;
import org.redisson.api.map.event.EntryRemovedListener;
import org.redisson.api.map.event.EntryUpdatedListener;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.netty.util.concurrent.Future;
import xy.spring2nosql.config.RedissonConfig;
import xy.spring2nosql.model.Redis.User;
import xy.spring2nosql.utils.RedissonUtil;


@RestController
@RequestMapping("/redisson")
public class RedissonController {
	
	// 单Redis节点模式,默认连接地址 127.0.0.1:6379
	RedissonClient redisson = Redisson.create();
	
	
	/**
     * Map 获取
     * @param name
     * @param key
     * @return
     */
    @RequestMapping("/getMap/{name}/{key}")
    public String get(@PathVariable("name") String name,@PathVariable("key") String key){

    	String result = (String) redisson.getMap(name).get(key);

        return result;
    }

    /**
     * 本地缓存（Local Cache）也叫就近缓存（Near Cache）。
     * 这类映射的使用主要用于在特定的场景下，映射缓存（MapCache）上的高度频繁的读取操作，
     * 使网络通信都被视为瓶颈的情况。Redisson与Redis通信的同时，还将部分数据保存在本地内存里。这样的设计的好处是它能将读取速度提高最多 45倍 
     * @param name
     * @param key
     * @return
     */
    @RequestMapping("/getLocalCachedMap/{name}/{key}")
    public String getLocalCachedMap(@PathVariable("name") String name,@PathVariable("key") String key){
    	
    	LocalCachedMapOptions<Object, Object> options = LocalCachedMapOptions.defaults()
    			.evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LFU)
    			.cacheSize(100)
    			.syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE) //同步缓存
    			.timeToLive(10, TimeUnit.SECONDS)
    			.maxIdle(10, TimeUnit.SECONDS);
    	
    	RLocalCachedMap<Object, Object> map = redisson.getLocalCachedMap(name,options);
    	
    	map.put(key, "测试值");

    	String result = (String)map.get(key);

        return result;
    }
    
    /**
     * 元素淘汰功能（Eviction）和事件监听
     * 带有元素淘汰（Eviction）机制的映射类允许针对一个映射中每个元素单独设定 有效时间 和 最长闲置时间 。
     * @param name
     * @param key
     * @return
     */
    @RequestMapping("/getMapCache/{name}/{key}")
    public int getMapCache(@PathVariable("name") String name,@PathVariable("key") String key){

    	RMapCache<String, Integer> map = redisson.getMapCache(name);
    	//LRU有界映射设置
    	// 将该映射的最大容量限制设定或更改为10
    	map.setMaxSize(10);
    	
       	map.put(key, 999);
       	map.put(key+"_TimeTtoLive", 888, 1, TimeUnit.SECONDS);//有效时间1秒

    	int updateListener = map.addListener(new EntryUpdatedListener<Integer, Integer>() {
    	     @Override
    	     public void onUpdated(EntryEvent<Integer, Integer> event) {
//    	          event.getKey(); // 字段名
//    	          event.getValue(); // 新值
//    	          event.getOldValue(); // 旧值
    	     }
    	});

    	int createListener = map.addListener(new EntryCreatedListener<Integer, Integer>() {
    	     @Override
    	     public void onCreated(EntryEvent<Integer, Integer> event) {
//    	          event.getKey(); // 字段名
//    	          event.getValue(); // 值
    	     }
    	});

    	int expireListener = map.addListener(new EntryExpiredListener<Integer, Integer>() {
    	     @Override
    	     public void onExpired(EntryEvent<Integer, Integer> event) {
//    	          event.getKey(); // 字段名
//    	          event.getValue(); // 值
    	          
    	     }
    	});

    	int removeListener = map.addListener(new EntryRemovedListener<Integer, Integer>() {
    	     @Override
    	     public void onRemoved(EntryEvent<Integer, Integer> event) {
//    	          event.getKey(); // 字段名
//    	          event.getValue(); // 值
    	     }
    	});

    	map.removeListener(updateListener);
    	map.removeListener(createListener);
    	map.removeListener(expireListener);
    	map.removeListener(removeListener);
    	try {
			Thread.sleep(200);//如果改成1000以上，则无法取出映射的值，因为已删除
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	int result = map.get(key+"_TimeTtoLive");
    	return result;
    }

    
	/**
	 * 设置分布式对象桶（Object Bucket）
	 * @param key
	 * @param name
	 * @param value
	 * @return
	 */
    @RequestMapping("/setObjectBucket/{key}")
    public String setObjectBucket(@PathVariable("key") String key){
    	
    	RBucket<User> bucket = redisson.getBucket(key);
    	bucket.set(new User("wangxin","王昕"));

        return "objectBucket set success.";
    }
    
    @RequestMapping("/getBucket/{key}")
    public User getBucket(@PathVariable("key") String key){
    	
    	User user = (User) RedissonUtil.getRBucket(redisson, key).get();

        return user;
    }
    
    
    /**
     * 分布式基数估计算法
     * @param key
     * @return
     */
    @RequestMapping("/hyperLogLog/{key}")
    public Long hyperLogLog(@PathVariable("key") String key){
    	
    	RHyperLogLog<Integer> log = redisson.getHyperLogLog(key);
    	
    	log.add(1);
    	log.add(2);
    	log.add(3);

    	Long count =log.count();

        return count;
    }


    /**
     * 分布式整长型累加器
     * 基于Redis的Redisson分布式整长型累加器采用了与java.util.concurrent.atomic.LongAdder类似的接口。
     * 通过利用客户端内置的LongAdder对象，为分布式环境下递增和递减操作提供了很高得性能。
     * 据统计其性能最高比分布式AtomicLong对象快 12000 倍。完美适用于分布式统计计量场景。
     * @param key
     * @return
     */
    @RequestMapping("/longAdder/{key}")
    public Long longAdder(@PathVariable("key") String key){
    	
    	RLongAdder atomicLong = redisson.getLongAdder(key);
    	atomicLong.add(12);
    	atomicLong.increment();
    	atomicLong.increment();
    	atomicLong.decrement();

    	Long count =atomicLong.sum();

        return count;
    }

    /**
     * 分布式原子双精度浮点
     * 分布式原子双精度浮点RAtomicDouble，弥补了Java自身的不足
     * @param key
     * @return
     */
    @RequestMapping("/atomicDouble/{key}")
    public double atomicDouble(@PathVariable("key") String key){
    	
    	RAtomicDouble atomicDouble = redisson.getAtomicDouble(key);
    	atomicDouble.set(123.11);
    	atomicDouble.addAndGet(4.25);

    	double value = atomicDouble.get();

        return value;
    }

    /**
     * 异步批量执行
     * @param key
     * @return
     */
    @RequestMapping("/batch")
    public String batch(){
    	
    	RBatch batch = redisson.createBatch(BatchOptions.defaults());
    	//RTransaction RTransaction = redisson.createTransaction(TransactionOptions.defaults())
    	batch.getMap("test").fastPutAsync("1", "2");
    	batch.getMap("test").fastPutAsync("2", "3");
    	batch.getMap("test").putAsync("2", "新的值");

    	// 将写入操作同步到从节点
    	// 同步到2个从节点，等待时间为1秒钟
    	//batch.syncSlaves(2, 1, TimeUnit.SECONDS);

    	BatchResult<?> res = batch.execute();

        return res.getResponses().toString();
    }

    
    
   /**
    * 计分排序集（ScoredSortedSet）
    * @return
    */
    @RequestMapping("/scoredSortedSet")
    public String ScoredSortedSet(){
    	
    	RScoredSortedSet<User> set = redisson.getScoredSortedSet("scoredSortedSet");
    	set.clear();	

    	set.add(3.13, new User("Tom","Tom name"));
    	set.addAsync(0.123, new User("Dog","Dog name"));
    	
    	User user = new User("Cat","Cat name");
    	set.add(4.67, user);
    	set.addScore(user, 0.1); //加分

    	int index = set.rank(user); // 获取元素在集合中的位置

    	Double score = set.getScore(user); // 获取元素的评分

        return  "位置:"+index +"，分数:"+score;
    }
    

    /**
     * 有界阻塞队列（Bounded Blocking Queue）
     * @return
     */
    @RequestMapping("/boundedBlockingQueue")
    public String BoundedBlockingQueue(){
    	
    	RBoundedBlockingQueue<User> queue = redisson.getBoundedBlockingQueue("anyQueue");
    	queue.clear();
    	// 如果初始容量（边界）设定成功则返回`真（true）`，
    	// 如果初始容量（边界）已近存在则返回`假（false）`。
    	queue.trySetCapacity(2);

    	queue.offer(new User("Tom","Tom name"));
    	queue.offer(new User("Cat","Cat name"));
    	//此时容量已满，add方法会直接阻塞
    	//queue.add(new User("Cat2","Cat2 name"));
    	// 此时容量已满，下面代码将会被阻塞，直到有空闲为止。
    	try {
			queue.put(new User("Dog","Dog name"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	User obj = queue.peek();//获取队列第一个元素，但并不从队列中删除
    	User obj2 = null;
		try {
			obj2 = queue.poll(3, TimeUnit.SECONDS);//获取队列第一个元素，并删除
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		User obj3 = queue.poll();
        return obj.getLogin() +","+ obj2.getLogin()+","+ obj3.getLogin();
    }

    

}
