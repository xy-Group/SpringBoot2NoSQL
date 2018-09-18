package xy.SpringBoot2NoSQL.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class User implements Serializable{

	private static final long serialVersionUID = -510562806568452731L;

	Logger logger = LogManager.getLogger(getClass());

	private String login;
	
	private String fullName;
	
	private Date lastLogin;

	public User() {}
	
	public User(String login, String fullName) {
		this.login = login;
		this.fullName = fullName;
		this.lastLogin = new Date();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}		
	
}

