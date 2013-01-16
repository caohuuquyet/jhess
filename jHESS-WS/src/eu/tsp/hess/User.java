package eu.tsp.hess;

public class User {
	private String id;
	private String name;
 
	public User(String id, String name){
		this.id = id;
		this.name = name;
	}
 
	public void setName(String name){
		this.name = name;
	}
 
	public String getID(){
		return this.id;
	}
 
	public String getName(){
		return this.name;
	}
}