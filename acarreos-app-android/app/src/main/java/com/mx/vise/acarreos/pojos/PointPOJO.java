package com.mx.vise.acarreos.pojos;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mx.vise.androidwscon.utils.CustomCalendarDeserializer;
import com.mx.vise.androidwscon.utils.CustomDateSerializer;
import com.mx.vise.androidwscon.utils.JsonDate;
import com.mx.vise.androidwscon.utils.UtilDate;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.acarreos.entities
 * Creado por Angelo el martes 08 de enero del 2019 a las 05:29 PM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-java
 */
public class PointPOJO implements Serializable {

    private static final long serialVersionUID = 8339408297841423627L;

    private Integer addUser;

    private String cadenamiento;

    private Integer esBancoYTiro;
    private String estatus;

    private Integer idPuntoLocal;

    private Double latitud;

    private Double longitud;

    private String nombreBanco;

    private Double radio;

    private String obra;

    private Integer idPuntoServer;

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date regDate;

    private int tipoPunto;

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date updDate;

    private Integer autorizado;



    public PointPOJO() {
    }

    public PointPOJO(int addUser, String cadenamiento, int esBancoYTiro, String estatus,
                     double latitud, double longitud, String nombreBanco, double radio, Date regDate,
                     int tipoPunto){
        this.addUser = addUser;
        this.cadenamiento = cadenamiento;
        this.esBancoYTiro = esBancoYTiro;
        this.estatus = estatus;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreBanco = nombreBanco;
        this.radio = radio;
        this.regDate = regDate;
        this.tipoPunto = tipoPunto;
    }

    public PointPOJO(int idPunto) {
        this.idPuntoLocal = idPunto;
    }


    public int getAddUser() {
        return addUser;
    }

    public void setAddUser(int addUser) {
        this.addUser = addUser;
    }


    public String getCadenamiento() {
        return cadenamiento;
    }

    public void setCadenamiento(String cadenamiento) {
        this.cadenamiento = cadenamiento;
    }

    public int getEsBancoYTiro() {
        return esBancoYTiro;
    }

    public void setEsBancoYTiro(int esBancoYTiro) {
        this.esBancoYTiro = esBancoYTiro;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public int getIdPuntoLocal() {
        return idPuntoLocal;
    }

    public void setIdPuntoLocal(int idPunto) {
        this.idPuntoLocal = idPunto;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public int getTipoPunto() {
        return tipoPunto;
    }

    public void setTipoPunto(int tipoPunto) {
        this.tipoPunto = tipoPunto;
    }

    /**
     * @return the obra
     */
    public String getObra() {
        return obra;
    }

    /**
     * @param obra the obra to set
     */
    public void setObra(String obra) {
        this.obra = obra;
    }

    public Integer getIdPuntoServer() {
        return idPuntoServer;
    }

    public void setIdPuntoServer(Integer idPuntoServer) {
        this.idPuntoServer = idPuntoServer;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public Integer getAutorizado() {
        return autorizado;
    }

    public void setAutorizado(Integer autorizado) {
        this.autorizado = autorizado;
    }
}