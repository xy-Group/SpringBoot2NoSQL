SpringBoot2.0 + NoSQL使用教程，项目名称：“SpringBoot2NoSQL”
## 项目介绍
All in one一站式SpringBoot for NoSQL开发教程学习手册。

含SpringBoot2.0 +:

1. Redis
2. Ehcache
3. MongoDB
4. ElasticSearch
5. Cassandra
6. CouchBase
7. Solr
8. Neo4j
9. Gemfire

共10种常用NoSQL数据库操作、工具类、演示代码。用于整理日常常用的开发模式，一是作为开发笔记以备我自己日后使用，二是分享出来供大家参考。

重点介绍Redis、MongoDB、ElasticSeach、Cassandra四种数据库，因为它们是各自领域的领先者（分别是KV缓存、文档数据库、搜索数据库、列数据库）。

另还准备新开一个项目用于介绍Hadoop家族的大数据开发，将命名为“SpringBoot2Hadoop”

## 项目运行帮助

项目导入，请使用Gradle进行项目导入：

![](http://111.230.157.216/img/nosql/run1.png)

运行方式1，使用SpringBoot2的启动类启动，类名：SpringBoot2NoSqlApplication：

![](http://111.230.157.216/img/nosql/run2.png)

运行方式2，使用Gradle的启动工程，即Gradle Task》SpringBoot2NoSql》application》bootRun

![](http://111.230.157.216/img/nosql/run3.png)

运行成功
![](http://111.230.157.216/img/nosql/run4.png)

测试效果：
Redis Bucket对象桶Controller操作：

![](http://111.230.157.216/img/nosql/redisBucket.png)

## Redis for SpringBoot 开发介绍

内容：

- SpringBoot配置、控制器、Repository Crud；
- string、list、set、zset、hash操作；
- 同步、异步操作；
- 管道批处理；
- 分布式对象操作：对象桶Object Bucket、二进制流Binary Stream、原子类型、发布订阅、HyperLogLog分布式基数估计算法等；分布式锁；
- 分布式集合操作：哈希、多值哈希、集合、排序集合、队列（双端、阻塞、有界、公平、延迟、优先）
- 性能测试、内存监控
- 注意：在启动SpringBoot前要先启动Redis服务。

##### application.properties配置：

	# Redis
	# Redis数据库索引（默认为0）
	spring.redis.database=0
	spring.redis.host=127.0.0.1
	spring.redis.port=6379
	#spring.redis.password=123
	# 连接池最大连接数（使用负值表示没有限制）
	spring.redis.pool.max-active=60
	# 连接池中的最大空闲连接
	spring.redis.pool.max-idle=30
	# 连接池最大阻塞等待时间（使用负值表示没有限制）
	spring.redis.pool.max-wait=-1
	# 连接池中的最小空闲连接
	spring.redis.pool.min-idle=0

	#redisson配置
	#redis链接地址
	spring.redisson.address=redis://127.0.0.1:6379
	...

因为我们还使用了Redisson作为客户端，还需RedissonConfig

	@ConfigurationProperties(prefix = "spring.redisson")
	@Configuration
	public class RedissonConfig{
	...

Spring官方默认支持Lettuce、Jedis客户端，Redis官方的推荐客户端是Redisson，因为Redison提供了诸多分布式的集合工具（这对单机到分布式的扩展非常有益）以及优异的性能，所以非常值得使用Redisson客户端。Spring Boot是一个平台，不必受限于它。我们在代码中配置Redisson连接的模式是单机模式，如想配置集群模式和哨兵模式，请参考官方wiki： https://github.com/redisson/redisson/wiki/

##### 模型Model：
见xy.SpringBoot2NoSQL.model.Redis.User

	public class User implements Serializable{
	
		private String login;		
		private String fullName;
		...


这完全是一个简单POJO java类.使用登录名（login）作为关键字段。

##### 数据层repository：
xy.SpringBoot2NoSQL.repository.Redis.**ObjectRepository**

以及

xy.SpringBoot2NoSQL.repository.Redis.**UserRepository**

分别是Object类型转换操作的数据类，和泛型User数据操作。
CRUD操作是RedisTemplate中提供了几个常用的单例对象：

![](http://111.230.157.216/img/nosql/redisRedisTemplate.png)

两者都是扩展自org.springframework.data.redis.core.**ValueOperations<K, V>**
，全面满足Redis的5大数据结构外，还提供了如地理位置、计数估计HyperLogLog操作。如：
	
	private @Nullable ValueOperations<K, V> valueOps;//KV操作
	private @Nullable ListOperations<K, V> listOps;//列表
	private @Nullable SetOperations<K, V> setOps;//无排序集合
	private @Nullable ZSetOperations<K, V> zSetOps;//计分排序集合
	private @Nullable GeoOperations<K, V> geoOps;//用于地理位置
	private @Nullable HyperLogLogOperations<K, V> hllOps;//基数估值
	...
	@Override
	public ValueOperations<K, V> opsForValue() {

		if (valueOps == null) {
			valueOps = new DefaultValueOperations<>(this);
		}
		return valueOps;
	}
	...
	@Override
	public ZSetOperations<K, V> opsForZSet() {

		if (zSetOps == null) {
			zSetOps = new DefaultZSetOperations<>(this);
		}
		return zSetOps;
	}
	...

在RedisTemplate中，已经提供了一个工厂方法:opsForValue()。这个方法会返回一个默认的操作类。另外，我们可以直接通过注解@Resource(name = “redisTemplate”)来进行注入。


##### 控制器controller:
见xy.SpringBoot2NoSQL.controller.Redis.**RedisDataController**

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

具体代码不再赘述。
另外，提供了Redisson的控制器：

xy.SpringBoot2NoSQL.controller.Redis.**RedissonController**

RedissonController演示了同步\异步操作、分布式集合（哈希、多值哈希、集合、排序集合、队列）、本地缓存、对象桶Object Bucket、二进制流Binary Stream、原子类型、发布订阅、HyperLogLog分布式基数估计算法、累加器、元素淘汰、事件监听、分布式锁、异步批量操作等。

比如本地缓存操作：

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


##### 运行效果

Bucket对象桶操作：

![](http://111.230.157.216/img/nosql/redisBucket.png)


批量操作：

![](http://111.230.157.216/img/nosql/redisBatch.png)

有界阻塞队列操作：

![](http://111.230.157.216/img/nosql/redisBoundedBlockingQueue.png)



##### 更多详细介绍

正在写作...

## Ehcache for SpringBoot 开发介绍

##### application.properties配置：

	# ehcache
	spring.cache.type=ehcache
	spring.cache.ehcache.config=classpath:ehcache.xml

ehcache.xml

	<?xml version="1.0" encoding="UTF-8"?>
	<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
	         updateCheck="false"
	         >
	    <diskStore path="myCache.ehcache"/>
	    <defaultCache
	            eternal="true"
	            maxElementsInMemory="1000"
	            overflowToDisk="true"
	            diskPersistent="true"
	            timeToIdleSeconds="0"
	            timeToLiveSeconds="600"
	            memoryStoreEvictionPolicy="LRU" />
	 
	    <!-- 这里的 users 缓存空间是为了下面的 demo 做准备 -->
	    <!-- maxElementsInMemory ：cache 中最多可以存放的元素的数量。如果放入cache中的元素超过这个数值，有两种情况：
	     1、若overflowToDisk的属性值为true，会将cache中多出的元素放入磁盘文件中。
	     2、若overflowToDisk的属性值为false，会根据memoryStoreEvictionPolicy的策略替换cache中原有的元素。-->
	    
	    <cache
	            name="users"
	            eternal="true"
	            maxElementsInMemory="1000"
	            overflowToDisk="true"
	            diskPersistent="true"
	            timeToIdleSeconds="0"
	            timeToLiveSeconds="300"
	            memoryStoreEvictionPolicy="LRU" />
	</ehcache>

##### 模型Model：
见xy.SpringBoot2NoSQL.model.User
这完全是一个简单POJO java类.使用登录名（login）作为关键字段。

	public class User implements Serializable{
	
		private String login;		
		private String fullName;
		...


##### 数据服务层Service：
见xy.SpringBoot2NoSQL.service.Ehcache.UserService

演示简单直接，并没有repository。 getUser() 方法演示了缓存根据配置存在于JVM堆、堆外、磁盘持久化的各种情况。

##### 控制器controller:
见xy.SpringBoot2NoSQL.controller.Ehcache.EhcacheDataController

##### 运行效果

运行控制器操作：

![](http://111.230.157.216/img/nosql/ehcacheService.png)

持久化到本地磁盘：

![](http://111.230.157.216/img/nosql/ehcacheToDisk.png)


##### 更多详细介绍

正在写作...


## MongoDB for SpringBoot 开发介绍
##### application.properties配置：

	# MONGODB 
	spring.data.mongodb.host=127.0.0.1
	spring.data.mongodb.port=27017
	spring.data.mongodb.database=test

##### 模型Model：
见xy.SpringBoot2NoSQL.model.Mongo.Person

	@Document 
	public class Person {
		@Id
		private String id;
		private String name;
		private Integer age;
		@Field("locs")
		private Collection<Location> locations =  new LinkedHashSet<Location>();
		@DBRef	
		Department department;

@Document是org.springframework.data.mongodb.core.mapping.Document的注解。

@Id是定义主键。

@Field("locs")是定义文档内部对象，在这里是Collection<Location>

@DBRef是链接到外表，Department是链接的表，有点像关系数据库的Inner Join，以Department的id关联。


##### 数据层repository：
见xy.SpringBoot2NoSQL.repository.Mongo.PersonRepository

继承MongoRepository<T, TD>接口，其中T为仓库保存的bean类，TD为该bean的唯一标识的类型，一般为ObjectId。之后在service中注入该接口就可以使用，无需实现里面的方法，spring会根据定义的规则自动生成。


	import org.springframework.data.domain.Page;
	import org.springframework.data.domain.Pageable;
	import org.springframework.data.mongodb.repository.MongoRepository;
	import org.springframework.data.mongodb.repository.Query;
	import xy.SpringBoot2NoSQL.model.Mongo.Person;
	import java.util.List;
	
	public interface PersonRepository extends MongoRepository<Person, String> {
	
		Person findByName(String name);
	
		@Query("{'age': { '$lt' : ?0}}")
		List<Person> withQueryFindByAge(Integer age);
		
		Page<Person> findAll(Pageable pageable);
	
	}

自定义查询方法，还可以使用格式为“findBy+字段名+方法后缀”，方法传进的参数即字段的值，此外还支持分页查询，通过传进一个Pageable对象，返回Page集合。
如查询大于age的数据：

       public Page<Product> findByAgeGreaterThan(int age,Pageable page) ;

也可以使用mongodb原生查询语句，使用@Query注解，如：

		@Query("{'age': { '$lt' : ?0}}")
		List<Person> withQueryFindByAge(Integer age);

	//大于
	{"age" : {"$gt" : age}} 	
	//小于
	{"age" : {"$lt" : age}}
	//之间
	findByAgeBetween(int from, int to) 
	{"age" : {"$gt" : from, "$lt" : to}}
	//非空
	findByFirstnameNotNull() 
	{"age" : {"$ne" : null}}
	//模糊查询
	findByFirstnameLike(String name) 
	{"age" : age} ( age as regex)
	//非
	findByFirstnameNot(String name) 
	{"age" : {"$ne" : name}}

##### 控制器controller:
见xy.SpringBoot2NoSQL.controller.Mongo.MongoDataController

	@RestController
	@RequestMapping("/mongo")
	public class MongoDataController {
	
	    @Autowired
	    PersonRepository personRepository;
	    @Autowired
	    DepartmentRepository departmentRepository;
	
	    @RequestMapping("/persons/{name}")
	    public Person getPerson(@PathVariable("name") String name){
	        return personRepository.findByName(name);
	    }
	    
	    @GetMapping("findAll")
	    public List<Person> getUserList() {
	        List<Person> userInfoList = personRepository.findAll();
	        return userInfoList;
	    }

	    @GetMapping("insert")
	    public Person insert(Long id, String username, String password) {
	        Person user = new Person("test2",22);       
	        return personRepository.insert(user);
	    }

	    @DeleteMapping("/colleagues/{name}")
	    public ResponseEntity<String> deleteColleague(@PathVariable  String name){
	        Person person = personRepository.findByName(name);
	        if(person!=null) {
	        	personRepository.delete(person);
	            return new ResponseEntity<>(HttpStatus.ACCEPTED);
	        }
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
		...
具体CRUD不再赘述。

##### 运行效果

保存操作：

![](http://111.230.157.216/img/nosql/mongoSave.png)

保存结果：

![](http://111.230.157.216/img/nosql/mongoResult.png)

Query查询：

![](http://111.230.157.216/img/nosql/mongoQuery.png)


##### 更多详细介绍

正在写作...

## ElasticSearch for SpringBoot 开发介绍

##### application.properties配置：

	# elasticsearch
	#节点名字，默认elasticsearch
	spring.data.elasticsearch.cluster-name=elasticsearch
	# 节点地址，多个节点用逗号隔开
	spring.data.elasticsearch.cluster-nodes=127.0.0.1:9300
	#spring.data.elasticsearch.local=false
	spring.data.elasticsearch.repositories.enable=true

##### 模型Model：
见xy.SpringBoot2NoSQL.model.ElasticSearch.Product

	@Document(indexName = "book",type = "book" , shards = 1, replicas = 0,  refreshInterval = "-1")
	public class Product {
	    @Id
	    private String id;
	    private String name;
	...
Elasticsearch是面向文档(document oriented)的，这意味着它可以存储整个对象或文档(document)。然而它不仅仅是存储，还会索引(index)每个文档的内容使之可以被搜索。在Elasticsearch中，你可以对文档（而非成行成列的数据）进行索引、搜索、排序、过滤。

在Elasticsearch中存储数据的行为就叫做索引(indexing)，不过在索引之前，我们需要明确数据应该存储在哪里。
在Elasticsearch中，文档归属于一种类型(type)，这里是Classname（即book）,而这些类型存在于索引(index)中，我们可以画一些简单的对比图来类比传统关系型数据库：

	Relational DB -> Databases -> Tables -> Rows -> Columns
	Elasticsearch -> Indices   -> Types  -> Documents -> Fields

Elasticsearch集群可以包含多个索引(indices)（数据库），每一个索引可以包含多个类型(types)（表），每一个类型包含多个文档(documents)（行），然后每个文档包含多个字段(Fields)（列，比如name、age...）。文档中的所有字段都会被索引（拥有一个倒排索引），只有这样他们才是可被搜索的。注意：传统数据库为特定列增加一个索引，一般使用B-Tree索引，Elasticsearch和Lucene使用一种叫做倒排索引(inverted index)的数据结构。


##### 数据层repository：
见xy.SpringBoot2NoSQL.repository.ElasticSearch.SampleProductRepository

	public interface SampleProductRepository extends ElasticsearchRepository<Product,String> {
	    List<Product> findByName(String name);
	    List<Product> findByDescription(String description);
	    List<Product> findByName(String name, Pageable pageable);
	    List<Product> findByNameAndId(String name, String id);
	}

通过继承ElasticsearchRepository来完成基本的CRUD及分页操作的，和普通的JPA没有什么区别。ElasticsearchRepository继承了ElasticsearchCrudRepository extends PagingAndSortingRepository。

另外还可以使用ElasticSearchTemplate，ElasticSearchTemplate更多是对ESRepository的补充，里面提供了一些更底层的方法，主要是一些查询相关的，同样是构建各种SearchQuery条件。比如我们经常需要往ElasticSearch中插入大量的测试数据来完成测试搜索，一条一条插肯定是不行的，ES提供了批量插入数据的功能——bulk。
JPA的save方法也可以save（List）批量插值，但适用于小数据量，要完成超大数据的插入就要用ES自带的bulk了，可以迅速插入百万级的数据。
在ElasticSearchTemplate里也提供了对应的方法。

##### 控制器controller:
用于CRUD操作：

xy.SpringBoot2NoSQL.controller.ElasticSearch.ESDataController

用于查询操作：

xy.SpringBoot2NoSQL.controller.ElasticSearch.ESSearchController

其中matchQuery、fuzzyQuery、termsQuery等查询的区别可见：


	@GetMapping("searchProduct/{key}/{value}/{searchtype}")
	public Page<Product> searchProduct(@PathVariable String key,@PathVariable String value, @PathVariable String searchtype) {
		Page<Product> products = null;
		
		if (searchtype.equals("matchQuery")) {
			products = sampleProductRepository.search(ESSearchUtil.matchQuery(key, value), PageRequest.of(0, 5));
		}
		
		if (searchtype.equals("matchPhraseQuery")) {
			products = sampleProductRepository.search(ESSearchUtil.matchPhraseQuery(key, value), PageRequest.of(0, 5));
		}				
		
		if (searchtype.equals("fuzzyQuery")) {
			products = sampleProductRepository.search(ESSearchUtil.fuzzyQuery(key, value), PageRequest.of(0, 5));
		}
		
		if (searchtype.equals("termsQuery")) {
			products = sampleProductRepository.search(ESSearchUtil.termsQuery(key, value), PageRequest.of(0, 5));
		}
				
		return products;
	}

组合查询：

	public QueryBuilder getBoolQuery() {

		return QueryBuilders.boolQuery()
				.must(QueryBuilders.matchPhraseQuery("name", "测试产品1"))
				//.mustNot(QueryBuilders.termQuery("enabled", true));
				.must(QueryBuilders.matchPhraseQuery("enabled", true));
	}

##### 更多详细介绍

正在写作...

## Cassandra for SpringBoot 开发介绍
##### 内容
- SpringBoot配置、控制器、Repository Crud；
- 使用前需要先建表，建表的脚本见“脚本”》“Cassandra”》“建库脚本.txt”
- 注意：如果是3.9版本的Win64的DataStax-DDC版本安装后，是无法启动服务的，需要修改配置文件，配置cdc_raw_directory类似如下：
cdc_raw_directory: "你的cdcraw目录" 
,如果是2.X版不存在这个问题。
- 注意：在启动SpringBoot前必须先启动Cassandra服务。

##### 配置：

cassandra.properties

	#cassandra.properties
	cassandra.contactpoints=127.0.0.1
	cassandra.port=9042
	cassandra.keyspace=mydb

CassandraConfig类

	@Configuration
	@PropertySource(value = { "classpath:cassandra.properties" })
	@EnableCassandraRepositories(basePackages = "xy.SpringBoot2NoSQL.repository.Cassandra")
	public class CassandraConfig extends AbstractCassandraConfiguration {
	    @Autowired
	    private Environment environment;
	
	    @Override
	    protected String getKeyspaceName() {
	        return environment.getProperty("cassandra.keyspace");
	    }
	
	    @Override
	    @Bean
	    public CassandraClusterFactoryBean cluster() {
			...	    
		}
	
	    @Override
	    @Bean
	    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
			...	    
		}

	}

##### 模型Model：

    @Table
    public class Customer {
	
	@PrimaryKey
	private String id;
	private String firstname; 
	...
	get、set
	...   

@Table定义了一个持久化的 Cassandra表，@PrimaryKey指定了主键字段，PrimaryKey是Cassandra中用于获取一行数据的key依据。
可以把Cassandra的数据结构理解为Map<PrimaryKey,SortedMap<ColumnKey,Value>>

主要语句是：

	CREATE TABLE customer(
		 id text PRIMARY KEY,...
是不是有点像关系数据库？
如果想设置自增的key，可以用SERIAL替代text

##### 数据层repository：

	public interface CustomerRepository extends CassandraRepository<Customer, String> {
	
	@Query(value="SELECT * FROM customer WHERE firstname=?0")
	public List<Customer> findByFirstname(String firstname);
 
	@Query("SELECT * FROM customer WHERE age > ?0")
	public List<Customer> findCustomerHasAgeGreaterThan(int age);
	
	@AllowFiltering
	public List<Customer> findByLastname(String lastname);
 
	@AllowFiltering
	public List<Customer> findByAgeGreaterThan(int age);
	}	
和CrudRepository的使用和扩展一样，增删改查一应俱全了，不再赘述。
CassandraRepository<Customer, String>继承于org.springframework.data.repository的CrudRepository<T, ID>
其中ID是primary key的类型，支持单一的key，如果是复合主键（compound primary key），则需要使用{@link MapId}手动定义键值使用。
@AllowFiltering用于扩展查询，他的作用是不使用@Query注解，而直接使用字段名来查询，还支持大于、小于等操作，但@Query还是来的更直观一些。

##### 控制器controller:

	@RestController
	@RequestMapping("/cassandra")
	public class CassandraController {
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	CustomerRepository customerRepository;
	
    @RequestMapping("/add")
    public String add(){
    	customerRepository.deleteAll();    	
		Customer cust_1 = new Customer("Test1", "Test1", "Test1", 20);
        Customer cust_2 = new Customer("Test2", "Test2", "Test2", 25);       
        customerRepository.save(cust_1);
        customerRepository.save(cust_2);
        
    	return "ok";
    }...
    
增删改查操作不再赘述。

##### 运行效果

范围查询：

![](http://111.230.157.216/img/nosql/CassandraController.png)

根据Id查询：

![](http://111.230.157.216/img/nosql/CassandraSearch.png)

##### 更多详细介绍

正在写作...

## CouchBase for SpringBoot 开发介绍


##### application.properties配置：

	#Couchbase
	spring.couchbase.bootstrap-hosts=127.0.0.1
	spring.couchbase.bucket.name=mydb
	spring.couchbase.bucket.password=123456
	spring.data.couchbase.auto-index=true
##### 模型Model：
见xy.SpringBoot2NoSQL.model.Couchbase.Customer
##### 数据层repository：
见xy.SpringBoot2NoSQL.repository.Couchbase.CouchbaseCustomerRepository
##### 控制器controller:
见xy.SpringBoot2NoSQL.controller.Couchbase.CouchbaseController

##### 更多详细介绍

正在写作...

## Solr for SpringBoot 开发介绍
##### application.properties配置：

	spring.data.solr.host=http://localhost:8983/solr
##### 模型Model：
见xy.SpringBoot2NoSQL.model.Solr.Customer
##### 数据层repository：
见xy.SpringBoot2NoSQL.repository.Solr.SolrCustomerRepository
##### 控制器controller:
见xy.SpringBoot2NoSQL.controller.Solr.SolrController
##### 更多详细介绍

正在写作...

## Neo4j for SpringBoot 开发介绍
正在写作...

## Gemfire for SpringBoot 开发介绍
正在写作...

## Hadoop家族 for SpringBoot 开发介绍
准备另开一个项目，不在此工程（SpringBoot2NoSQL）中，新项目将命名为“SpringBoot2Hadoop”