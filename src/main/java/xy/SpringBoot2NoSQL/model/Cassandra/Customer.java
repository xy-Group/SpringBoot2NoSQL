package xy.SpringBoot2NoSQL.model.Cassandra;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Customer {
	
	@PrimaryKey
	private String id;
	private String firstname;
	private String lastname;
	private int age;
	
//	@Column
//    private Set<String> tags = new HashSet<>();
	
	public Customer(){}
	
	public Customer(String id, String firstname, String lastname, int age){
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.age = age;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setFirstname(String firstname){
		this.firstname = firstname;
	}
	
	public String getFirstname(){
		return this.firstname;
	}
	
	public void setLastname(String lastname){
		this.lastname = lastname;
	}
	
	public String getLastname(){
		return this.lastname;
	}
	
	public void setAge(int age){
		this.age = age;
	}
	
	public int getAge(){
		return this.age;
	}
	 
//	public Set getTags() {
//	        return tags;
//	}
//	 
//	public void setTags(final Set<String> tags) {
//        this.tags = tags;
//    }
	
	@Override
	public String toString() {
		return String.format("Customer[id=%d, firstName='%s', lastName='%s', age=%d]", this.id,
				this.firstname, this.lastname, this.age);
	}
}
