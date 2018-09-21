package xy.spring2nosql.repository.ElasticSearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import xy.spring2nosql.model.ElasticSearch.Product;

import java.util.List;

public interface SampleProductRepository extends ElasticsearchRepository<Product,String> {
    List<Product> findByName(String name);
    List<Product> findByDescription(String description);
    List<Product> findByName(String name, Pageable pageable);
    List<Product> findByNameAndId(String name, String id);
}
