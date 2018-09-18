package xy.SpringBoot2NoSQL.repository.Mongo;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import xy.SpringBoot2NoSQL.model.Mongo.Person;
import java.util.List;

public interface PersonRepository extends MongoRepository<Person, String> {

	Person findByName(String name);

	@Query("{'age': { '$lt' : ?0}}")
	List<Person> withQueryFindByAge(Integer age);
	
	Page<Person> findAll(Pageable pageable);

}
