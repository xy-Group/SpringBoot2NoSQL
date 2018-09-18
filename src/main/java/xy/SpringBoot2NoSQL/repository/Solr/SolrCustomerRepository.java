package xy.SpringBoot2NoSQL.repository.Solr;

import java.util.List;
import org.springframework.data.solr.repository.SolrCrudRepository;
import xy.SpringBoot2NoSQL.model.Solr.Customer;


public interface SolrCustomerRepository extends SolrCrudRepository<Customer, String> {
	List<Customer> findByNameEndsWith(String name);
}
