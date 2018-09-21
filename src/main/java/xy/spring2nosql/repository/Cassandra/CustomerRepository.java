package xy.spring2nosql.repository.Cassandra;

import java.util.List;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;


import xy.spring2nosql.model.Cassandra.Customer;

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