package com.mx.vise.acarreos.pojos;

import java.io.Serializable;

public class AddTagPOJO implements Serializable {

	private static final long serialVersionUID = 3696101998361772386L;
	
	private String tid;
	private Integer cubId;
	private Integer updUser;
	
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public Integer getCubId() {
		return cubId;
	}
	public void setCubId(Integer cubId) {
		this.cubId = cubId;
	}
	public Integer getUpdUser() {
		return updUser;
	}
	public void setUpdUser(Integer updUser) {
		this.updUser = updUser;
	}
}
