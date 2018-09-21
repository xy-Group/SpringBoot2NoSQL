package xy.spring2nosql.repository.Solr;

import java.util.List;
import org.springframework.data.solr.repository.SolrCrudRepository;
import xy.spring2nosql.model.Solr.Customer;


public interface SolrCustomerRepository extends SolrCrudRepository<Customer, String> {
	List<Customer> findByNameEndsWith(String name);
}
