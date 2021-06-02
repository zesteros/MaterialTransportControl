package com.mx.vise.acarreos.pojos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.mx.vise.androidwscon.utils.CustomCalendarDeserializer;
import com.mx.vise.androidwscon.utils.CustomDateSerializer;
import com.mx.vise.androidwscon.utils.JsonDate;
import com.mx.vise.androidwscon.utils.UtilDate;
import java.io.Serializable;
import java.util.Date;

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

    private Long idMaterialByPointServer;
    private Long idMaterialServer;
    private Long idPointServer;
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

    public Long getIdMaterialByPointServer() {
        return idMaterialByPointServer;
    }

    public void setIdMaterialByPointServer(Long idMaterialByPointServer) {
        this.idMaterialByPointServer = idMaterialByPointServer;
    }

    public Long getIdMaterialServer() {
        return idMaterialServer;
    }

    public void setIdMaterialServer(Long idMaterialServer) {
        this.idMaterialServer = idMaterialServer;
    }

    public Long getIdPointServer() {
        return idPointServer;
    }

    public void setIdPointServer(Long idPointServer) {
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
