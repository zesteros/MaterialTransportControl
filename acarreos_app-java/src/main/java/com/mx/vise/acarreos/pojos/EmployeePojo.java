package com.mx.vise.acarreos.pojos;

import java.io.Serializable;
import java.util.ArrayList;

import com.mx.vise.eflow.permissions.pojo.PermissionPOJO;
import com.mx.vise.eflow.permissions.pojo.ProjectsByArea;

public class EmployeePojo implements Serializable {

	private static final long serialVersionUID = -8371635006566270831L;
	private Integer employeeId;
	private String employeeName;
	private String employeeEmail;
	private ArrayList<BuildingPojo> buildings;
	private ArrayList<ProjectsByArea> permissions;

	// private ArrayList<Supplier> suppliers;
	// private ArrayList<UnitBrand> unitBrands;

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public ArrayList<ProjectsByArea> getPermissions() {
		return permissions;
	}

	public void setPermissions(ArrayList<ProjectsByArea> permissions) {
		this.permissions = permissions;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public ArrayList<BuildingPojo> getBuildings() {
		return buildings;
	}

	public void setBuildings(ArrayList<BuildingPojo> buildings) {
		this.buildings = buildings;
	}

}
