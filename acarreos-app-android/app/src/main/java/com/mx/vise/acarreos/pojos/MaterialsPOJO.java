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
 * Creado por Angelo el lunes 11 de febrero del 2019 a las 11:23
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 *
 * Pojo para los materiales
 */
public class MaterialsPOJO implements Serializable {	private static final long serialVersionUID = -563900699827087057L;

    private Long idMaterialServer;
    private String idMaterialNavision;
    private String building;
    private String acronym;
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
    private String description;
    private String unitOfMeasure;

    public String getIdMaterialNavision() {
        return idMaterialNavision;
    }

    public void setIdMaterialNavision(String idMaterialNavision) {
        this.idMaterialNavision = idMaterialNavision;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
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

    public Long getIdMaterialServer() {
        return idMaterialServer;
    }

    public void setIdMaterialServer(Long idMaterialServer) {
        this.idMaterialServer = idMaterialServer;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

}
