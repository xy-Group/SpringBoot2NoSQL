//package xy.SpringBoot2NoSQL.controller.Couchbase;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import xy.SpringBoot2NoSQL.model.Couchbase.Customer;
//import xy.SpringBoot2NoSQL.repository.Couchbase.CouchbaseCustomerRepository;
//
//@RestController
//@RequestMapping("/couchbase")
//public class CouchbaseController {
//	
//	@Autowired
//	CouchbaseCustomerRepository couchbaseCustomerRepository;
//	
//	@RequestMapping("/add")
//    public String add(){
//  	
//		couchbaseCustomerRepository.deleteAll();
//		couchbaseCustomerRepository.saveAll(Arrays.asList(new Customer("01", "Jack", "Smith"),
//				new Customer("02", "Adam", "Johnson"),
//				new Customer("03", "Kim", "Smith"),
//				new Customer("04", "David", "Williams"),
//				new Customer("05", "Peter", "Davis")));
//        
//    	return "ok";
//    }
//
//	@RequestMapping("/all")
//    public Iterable<Customer> getAll(){
//
//    	return couchbaseCustomerRepository.findAll();   	
//    }
//	
//	 @RequestMapping("/getByID/{id}")
//	 public Optional<Customer> getByID(@PathVariable("id") String id){
//
//	    return couchbaseCustomerRepository.findById(id);   	
//	}
//	 
//	 @RequestMapping("/findByLastName/{name}")
//	 public List<Customer> findByLastName(@PathVariable("name") String name){
//
//	    return couchbaseCustomerRepository.findByLastName(name);
//	}
//
//}
