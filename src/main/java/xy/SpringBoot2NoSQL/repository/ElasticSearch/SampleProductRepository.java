package xy.SpringBoot2NoSQL.repository.ElasticSearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import xy.SpringBoot2NoSQL.model.ElasticSearch.Product;

import java.util.List;

public interface SampleProductRepository extends ElasticsearchRepository<Product,String> {
    List<Product> findByName(String name);
    List<Product> findByDescription(String description);
    List<Product> findByName(String name, Pageable pageable);
    List<Product> findByNameAndId(String name, String id);
}
