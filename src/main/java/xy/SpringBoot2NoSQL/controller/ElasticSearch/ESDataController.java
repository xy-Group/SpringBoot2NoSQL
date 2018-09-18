package xy.SpringBoot2NoSQL.controller.ElasticSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xy.SpringBoot2NoSQL.model.ElasticSearch.Product;
import xy.SpringBoot2NoSQL.repository.ElasticSearch.SampleProductRepository;

@RestController
@RequestMapping("/es")
public class ESDataController {
	
	@Autowired
    private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	SampleProductRepository sampleProductRepository;

	@Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

	//服务情况查询
    @GetMapping("details")
    public ResponseEntity<Map<String, String>> getElasticInformation() {

        Client client = elasticsearchOperations.getClient();
        Map<String, String> asMap = client.settings().getAsMap();
        return ResponseEntity.ok(asMap);
    }
    
    //健康检查
    @GetMapping("checkHealth")
    public String checkHealth() {

        return getHealth();
    }
    
	@GetMapping("insertProduct")
	public Product insert() {
		Product product = new Product("1", "测试产品1", "测试产品111111.", true);
		return sampleProductRepository.save(product);
	}

	@GetMapping("insertProductMore")
	public String insertProductMore() {

		List<Product> productList = new ArrayList<Product>();

		for (int i = 0; i < 100; i++) {
			Product product = new Product(String.valueOf(i), "测试产品" + i, "测试产品描述.", true);
			productList.add(product);
		}

		// 只需要执行一次批量
		sampleProductRepository.saveAll(productList);

		return "success";
	}

	@GetMapping("insertProduct2/{id}/{name}")
	public Product insert2(@PathVariable String name, @PathVariable String id) {
		Product product = new Product(id, name, name + "产品描述.", true);
		return sampleProductRepository.save(product);
	}

	@GetMapping("findByNameProducts/{name}")
	public List<Product> findByNameProducts(@PathVariable String name) {
		List<Product> list = sampleProductRepository.findByName(name);
		return list;
	}

	@GetMapping("findByNameAndIdProducts/{name}/{id}")
	public List<Product> findByNameAndIdProducts(@PathVariable String name, @PathVariable String id) {
		List<Product> list = sampleProductRepository.findByNameAndId(name, id);
		return list;
	}

	@GetMapping("findByNameProductsPage/{name}/{page}/{size}")
	public List<Product> findByNameProductsPage(@PathVariable String name, @PathVariable int page,
			@PathVariable int size) {
		List<Product> list = sampleProductRepository.findByName(name, PageRequest.of(page, size));
		return list;
	}

	@GetMapping("count")
	public long CountAllElementsInIndex() {

		long count = sampleProductRepository.count();
		return count;
	}

	@GetMapping("searchQueries/{id}/{name}/{page}/{size}")
	public Page<Product> SearchQueries(@PathVariable String name, @PathVariable String id, @PathVariable int page,
			@PathVariable int size) {

		BoolQueryBuilder bqb = QueryBuilders.boolQuery();
		bqb.must(QueryBuilders.termQuery("name", name));
		//bqb.must(QueryBuilders.rangeQuery("id").gt(id));// 大于

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(bqb).withPageable(PageRequest.of(page, size))
				.build();

		Page<Product> products = sampleProductRepository.search(searchQuery);
		return products;
	}
	
	//根据id删除
	public boolean deleteById(String id) {
		try {
			elasticsearchTemplate.delete(getEntityClass(), id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	//根据条件删除
	public boolean deleteByQuery(Map<String,Object> filedContentMap) {
		try {
			DeleteQuery dq = new DeleteQuery();
			
			BoolQueryBuilder query=QueryBuilders. boolQuery();
			if(filedContentMap!=null)
				for (String key : filedContentMap.keySet()) {//字段查询
					query.must(QueryBuilders.matchQuery(key,filedContentMap.get(key)));
				}
			dq.setQuery(query);
			elasticsearchTemplate.delete(dq, getEntityClass());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}



	//健康检查
	public String getHealth() {
		try {
			Client client = elasticsearchOperations.getClient();
			ActionFuture<ClusterHealthResponse> health = client.admin()
					.cluster().health(new ClusterHealthRequest());
			ClusterHealthStatus status = health.actionGet().getStatus();
			if (status.value() == ClusterHealthStatus.RED.value()) {
				return "red";
			}
			if (status.value() == ClusterHealthStatus.GREEN.value()) {
				return "green";
			}
			if (status.value() == ClusterHealthStatus.YELLOW.value()) {
				return "yellow";
			}
			return "unknow";
		} catch (Exception e) {

			return "unknow";
		}
	}
	
	private Class<Product> getEntityClass() {

		return Product.class;
	}
}
