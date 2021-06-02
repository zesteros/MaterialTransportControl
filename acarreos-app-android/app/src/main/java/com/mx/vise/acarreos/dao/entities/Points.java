package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Points implements Serializable {
    private static final long serialVersionUID = -1262409549919382761L;

    @Id
    private Long pointId;
    private Integer pointType;
    private String bankName;
    private Float radio;
    private String chainage;
    private Integer isBankToo;
    private Float latitude;
    private Float longitude;
    private Date regDate;
    private String uploadStatus;
    private Date uploadDate;
    private Integer addUser;
    private String building;
    @Index(unique = true)

    private Long idPointServer;
    private Date updDateServer;
    private String estatusServer;
    private Integer authorized;
    @Keep
    public Points(){}

    @Generated(hash = 1675937340)
    public Points(Long pointId, Integer pointType, String bankName, Float radio,
            String chainage, Integer isBankToo, Float latitude, Float longitude,
            Date regDate, String uploadStatus, Date uploadDate, Integer addUser,
            String building, Long idPointServer, Date updDateServer,
            String estatusServer, Integer authorized) {
        this.pointId = pointId;
        this.pointType = pointType;
        this.bankName = bankName;
        this.radio = radio;
        this.chainage = chainage;
        this.isBankToo = isBankToo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.regDate = regDate;
        this.uploadStatus = uploadStatus;
        this.uploadDate = uploadDate;
        this.addUser = addUser;
        this.building = building;
        this.idPointServer = idPointServer;
        this.updDateServer = updDateServer;
        this.estatusServer = estatusServer;
        this.authorized = authorized;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public Integer getPointType() {
        return pointType;
    }

    public void setPointType(Integer pointType) {
        this.pointType = pointType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Float getRadio() {
        return radio;
    }

    public void setRadio(Float radio) {
        this.radio = radio;
    }

    public String getChainage() {
        return chainage;
    }

    public void setChainage(String chainage) {
        this.chainage = chainage;
    }

    public Integer getIsBankToo() {
        return isBankToo;
    }

    public void setIsBankToo(Integer isBankToo) {
        this.isBankToo = isBankToo;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Integer getAddUser() {
        return addUser;
    }

    public void setAddUser(Integer addUser) {
        this.addUser = addUser;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Long getIdPointServer() {
        return idPointServer;
    }

    public void setIdPointServer(Long idPointServer) {
        this.idPointServer = idPointServer;
    }

    public Date getUpdDateServer() {
        return updDateServer;
    }

    public void setUpdDateServer(Date updDateServer) {
        this.updDateServer = updDateServer;
    }

    public String getEstatusServer() {
        return estatusServer;
    }

    public void setEstatusServer(String estatusServer) {
        this.estatusServer = estatusServer;
    }


    public Integer getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Integer authorized) {
        this.authorized = authorized;
    }
}
