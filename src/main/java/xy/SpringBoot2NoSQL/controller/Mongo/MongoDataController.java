package xy.SpringBoot2NoSQL.controller.Mongo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xy.SpringBoot2NoSQL.model.Mongo.Department;
import xy.SpringBoot2NoSQL.model.Mongo.Location;
import xy.SpringBoot2NoSQL.model.Mongo.Person;
import xy.SpringBoot2NoSQL.repository.Mongo.DepartmentRepository;
import xy.SpringBoot2NoSQL.repository.Mongo.PersonRepository;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mongo")
public class MongoDataController {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    @RequestMapping("/persons/{name}")
    public Person getPerson(@PathVariable("name") String name){
        return personRepository.findByName(name);
    }
    
    @GetMapping("findAll")
    public List<Person> getUserList() {
        List<Person> userInfoList = personRepository.findAll();
        return userInfoList;
    }
    
    @GetMapping("query1")
    public Person q1(String name){
        return personRepository.findByName(name);
    }

    @GetMapping("query2")
    public List<Person> q2(Integer age){
        return personRepository.withQueryFindByAge(age);
    }
    
    @GetMapping("delete")
    public String delete(String id) {
    	personRepository.deleteById(id);
        return "success";
    }
    
    @DeleteMapping("/colleagues/{name}")
    public ResponseEntity<String> deleteColleague(@PathVariable  String name){
        Person person = personRepository.findByName(name);
        if(person!=null) {
        	personRepository.delete(person);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("insert")
    public Person insert(Long id, String username, String password) {
        Person user = new Person("test2",22);       
        return personRepository.insert(user);
    }
    
    //要先执行添加部门的操作
    @RequestMapping("saveDepartment")
    public Department saveDepartment(){
    	Department  d = new Department("abc001","研发部");
        return departmentRepository.save(d);
    }
    
    //执行这个之前要先执行”saveDepartment“，因为需要一个部门的引用
    @RequestMapping("save")
    public Person save(){
        Person  p = new Person("王昕",32);
        Collection<Location> locations =  new LinkedHashSet<Location>();
        Location loc1 = new Location("上海","2009");
        Location loc2 = new Location("广州","2011");
        locations.add(loc1);
        locations.add(loc2);

        p.setLocations(locations);
        
        Department  d = departmentRepository.findByName("研发部");
        p.setDepartment(d);

        return personRepository.save(p);
    }
    
    
    @PostMapping("/formsave")
    public ResponseEntity<String> addColleague(@RequestBody Person person){
    	personRepository.save(person);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}