package com.mx.vise.acarreos.pojos.carries;


import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mx.vise.util.CustomCalendarDeserializer;
import com.mx.vise.util.CustomDateSerializer;
import com.mx.vise.util.JsonDate;
import com.mx.vise.util.UtilDate;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 11 de febrero del 2019 a las 12:00
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class MaterialsByPointPOJO implements Serializable {


	private static final long serialVersionUID = 1523722931169335504L;
	
	private Integer idMaterialByPointServer;
    private Integer idMaterialServer;
    private Integer idPointServer;
    private Integer addUser;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date addDate;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date updDate;
    private String statusServer;

    public Integer getIdMaterialByPointServer() {
        return idMaterialByPointServer;
    }

    public void setIdMaterialByPointServer(Integer idMaterialByPointServer) {
        this.idMaterialByPointServer = idMaterialByPointServer;
    }

    public Integer getIdMaterialServer() {
        return idMaterialServer;
    }

    public void setIdMaterialServer(Integer idMaterialServer) {
        this.idMaterialServer = idMaterialServer;
    }

    public Integer getIdPointServer() {
        return idPointServer;
    }

    public void setIdPointServer(Integer idPointServer) {
        this.idPointServer = idPointServer;
    }

    public Integer getAddUser() {
        return addUser;
    }

    public void setAddUser(Integer addUser) {
        this.addUser = addUser;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public String getStatusServer() {
        return statusServer;
    }

    public void setStatusServer(String statusServer) {
        this.statusServer = statusServer;
    }
}
