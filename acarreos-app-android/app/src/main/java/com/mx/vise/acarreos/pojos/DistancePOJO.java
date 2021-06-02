package com.mx.vise.acarreos.pojos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mx.vise.androidwscon.utils.CustomCalendarDeserializer;
import com.mx.vise.androidwscon.utils.CustomDateSerializer;
import com.mx.vise.androidwscon.utils.JsonDate;
import com.mx.vise.androidwscon.utils.UtilDate;


import java.io.Serializable;
import java.util.Date;

public class DistancePOJO implements Serializable {

	private static final long serialVersionUID = -3588775676328589083L;
	
	private Long idDistanceServer;
	private Long idPoint;
	private Float distance;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
	private Date addDate;
	private Integer addUser;
	private Integer updUser;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
	private Date updDate;
	private String estatus;
	
	public Long getIdDistanceServer() {
		return idDistanceServer;
	}
	public void setIdDistanceServer(Long idDistanceServer) {
		this.idDistanceServer = idDistanceServer;
	}
	public Long getIdPoint() {
		return idPoint;
	}
	public void setIdPoint(Long idPoint) {
		this.idPoint = idPoint;
	}
	public Float getDistance() {
		return distance;
	}
	public void setDistance(Float distance) {
		this.distance = distance;
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
	public Integer getUpdUser() {
		return updUser;
	}
	public void setUpdUser(Integer updUser) {
		this.updUser = updUser;
	}
	public Date getUpdDate() {
		return updDate;
	}
	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
}
