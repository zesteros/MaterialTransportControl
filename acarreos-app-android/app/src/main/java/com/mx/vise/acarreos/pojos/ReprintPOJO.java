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
 * Creado por Angelo el lunes 25 de febrero del 2019 a las 11:05
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */

public class ReprintPOJO implements Serializable{

    private static final long serialVersionUID = 2735904518993202554L;
    private String sheetNumber;
    private String coordinates;
    private int addUser;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date addDate;
    private Integer idReprint;

    public String getSheetNumber() {
        return sheetNumber;
    }
    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }
    public String getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
    public int getAddUser() {
        return addUser;
    }
    public void setAddUser(int addUser) {
        this.addUser = addUser;
    }
    public Date getAddDate() {
        return addDate;
    }
    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Integer getIdReprintLocal() {
        return idReprint;
    }

    public void setIdReprintLocal(Integer idReprint) {
        this.idReprint = idReprint;
    }
}
