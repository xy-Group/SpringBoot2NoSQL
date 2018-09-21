package xy.spring2nosql.repository.Mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import xy.spring2nosql.model.Mongo.Department;


public interface DepartmentRepository extends MongoRepository<Department, String> {

	Department findByName(String name);

}

