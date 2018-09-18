package xy.SpringBoot2NoSQL.repository.Mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import xy.SpringBoot2NoSQL.model.Mongo.Department;


public interface DepartmentRepository extends MongoRepository<Department, String> {

	Department findByName(String name);

}

