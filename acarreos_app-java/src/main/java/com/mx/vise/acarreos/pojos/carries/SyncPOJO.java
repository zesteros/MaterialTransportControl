package com.mx.vise.acarreos.pojos.carries;


import java.io.Serializable;

public class SyncPOJO implements Serializable {

	private static final long serialVersionUID = -8962264780331930186L;
	private String location;
	private String imei;
	private Integer userId;
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}