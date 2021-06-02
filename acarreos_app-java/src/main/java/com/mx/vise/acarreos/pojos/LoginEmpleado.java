package com.mx.vise.acarreos.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class LoginEmpleado implements Serializable {
	private static final long serialVersionUID = -6085079678478171394L;
	private String username;
	private String key;
	private ArrayList<Integer> projects;

	public LoginEmpleado() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "LoginEmpleado [username=" + username + ", key=" + key + "]";
	}

	public ArrayList<Integer> getProjects() {
		return projects;
	}

	public void setProjects(ArrayList<Integer> projects) {
		this.projects = projects;
	}

}
