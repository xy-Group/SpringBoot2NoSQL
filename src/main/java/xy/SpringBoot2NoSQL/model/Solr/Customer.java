package xy.SpringBoot2NoSQL.model.Solr;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "customer")
public class Customer {
	@Id
	@Field
	private String id;

	@Field
	private String name;
	
	@Field
	private Integer age;
	
	public Customer() {
	}
	
	public Customer(String id, String name, Integer age){
		this.id = id;
		this.name = name;
		this.age = age;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setAge(Integer age){
		this.age = age;
	}
	
	public Integer getAge(){
		return this.age;
	}
	
	@Override
	public String toString() {
		return "Customer [id=" + this.id + ", name=" + this.name + ", age=" + this.age + "]";
	}
}

