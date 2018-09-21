package xy.spring2nosql.controller.Cassandra;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xy.spring2nosql.model.Cassandra.Customer;
import xy.spring2nosql.repository.Cassandra.CustomerRepository;



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
        Customer cust_3 = new Customer("Test3", "Test3", "Test3", 30);
        Customer cust_4 = new Customer("Test4", "Test4", "Test4", 35);
        Customer cust_5 = new Customer("Test5", "Test5", "Test5", 40);
        Customer cust_6 = new Customer("Test6", "Test6", "Test6", 45);
        
        customerRepository.save(cust_1);
        customerRepository.save(cust_2);
        customerRepository.save(cust_3);
        customerRepository.save(cust_4);
        customerRepository.save(cust_5);
        customerRepository.save(cust_6);
        
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
    	
    @RequestMapping("/getByName/{name}")
    public List<Customer> getByName(@PathVariable("name") String name){
    	
    	return customerRepository.findByLastname(name);
    	
    }
    	
    @RequestMapping("/getByAge/{age}")
    public List<Customer> getByAge(@PathVariable("age") String age){
    	
    	return customerRepository.findByAgeGreaterThan(Integer.valueOf(age));
    	
    }
    	

}
