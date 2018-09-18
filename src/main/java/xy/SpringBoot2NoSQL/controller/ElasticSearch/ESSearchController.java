package xy.SpringBoot2NoSQL.controller.ElasticSearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xy.SpringBoot2NoSQL.model.ElasticSearch.Product;
import xy.SpringBoot2NoSQL.repository.ElasticSearch.SampleProductRepository;
import xy.SpringBoot2NoSQL.utils.ESSearchUtil;

@RestController
@RequestMapping("/essearch")
public class ESSearchController {

	@Autowired
	SampleProductRepository sampleProductRepository;
	
	@GetMapping("search")
	public Page<Product> search() {

		return sampleProductRepository.search(getBoolQuery(), PageRequest.of(0, 5));		
	}

	@GetMapping("searchAll/{page}")
	public Page<Product> searchAll(@PathVariable int page) {

		return sampleProductRepository.search(ESSearchUtil.matchAllQuery(), PageRequest.of(page, 5));		
	}

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
	
	public QueryBuilder getBoolQuery() {

		return QueryBuilders.boolQuery()
				.must(QueryBuilders.matchPhraseQuery("name", "测试产品1"))
				//.mustNot(QueryBuilders.termQuery("enabled", true));
				.must(QueryBuilders.matchPhraseQuery("enabled", true));
	}

}
