package edu.uic.f17g201.bean;

public class LoginBean {
	private String username;
	private String password;
	private String host;
	private String dmbsType;
	private String port;
	private String schema;
	
	public LoginBean(String username, String password, String host, String dmbsType, String schema) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.dmbsType = dmbsType;
		this.schema = schema;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDmbsType() {
		return dmbsType;
	}

	public void setDmbsType(String dmbsType) {
		this.dmbsType = dmbsType;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
}