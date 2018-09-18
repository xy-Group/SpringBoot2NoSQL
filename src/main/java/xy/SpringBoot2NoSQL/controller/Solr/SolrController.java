package xy.SpringBoot2NoSQL.controller.Solr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import xy.SpringBoot2NoSQL.model.Solr.Customer;
import xy.SpringBoot2NoSQL.repository.Solr.SolrCustomerRepository;

@RestController
@RequestMapping("/solr")
public class SolrController {
	
	@Autowired
	SolrCustomerRepository customerRepository;
	
	@RequestMapping("/add")
    public String add(){
  	
		customerRepository.deleteAll();
		customerRepository.saveAll(Arrays.asList(new Customer("1", "Jack", 20), 
											new Customer("2", "Adam", 24),
											new Customer("3", "Kim", 27), 
											new Customer("4", "David", 30), 
											new Customer("5", "Peter", 21)));
        
    	return "ok";
    }

	@RequestMapping("/all")
    public Iterable<Customer> getAll(){

    	return customerRepository.findAll();   	
    }
	
	 @RequestMapping("/getByID/{id}")
	 public Optional<Customer> getByID(@PathVariable("id") String id){

	    return customerRepository.findById(id);   	
	}
	 
	 @RequestMapping("/findByNameEndsWith/{name}")
	 public List<Customer> findByNameEndsWith(@PathVariable("name") String name){

	    return customerRepository.findByNameEndsWith(name);
	}
}
