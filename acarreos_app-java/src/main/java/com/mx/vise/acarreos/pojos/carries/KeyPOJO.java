package com.mx.vise.acarreos.pojos.carries;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;

public class KeyPOJO implements Serializable {
	
	private static final long serialVersionUID = -2362412345619382711L;

	private Integer idKeyServer;
	private String keyA;
	private String keyB;
	private Integer sector;
	private Integer version;
	private Date updDate;
	private Integer updUser;
	private Date addDate;
	private Integer addUser;
	private String estatus;

	public Integer getIdKeyServer() {
		return idKeyServer;
	}

	public void setIdKeyServer(Integer idKeyServer) {
		this.idKeyServer = idKeyServer;
	}

	public String getKeyA() {
		return keyA;
	}

	public void setKeyA(String keyA) {
		this.keyA = keyA;
	}

	public String getKeyB() {
		return keyB;
	}

	public void setKeyB(String keyB) {
		this.keyB = keyB;
	}

	public Integer getSector() {
		return sector;
	}

	public void setSector(Integer sector) {
		this.sector = sector;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Integer getAddUser() {
		return addUser;
	}

	public void setAddUser(Integer addUser) {
		this.addUser = addUser;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public Integer getUpdUser() {
		return updUser;
	}

	public void setUpdUser(Integer updUser) {
		this.updUser = updUser;
	}
}
