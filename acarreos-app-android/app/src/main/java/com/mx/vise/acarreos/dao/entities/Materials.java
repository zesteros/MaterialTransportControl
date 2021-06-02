package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Materials implements Serializable {
    private static final long serialVersionUID = -2262409134419382769L;

    @Id
    private Long materialIdLocal;
    @Index(unique = true)
    private Long materialIdServer;
    private String idMaterialNavision;
    private String building;
    private String acronym;
    private Integer addUser;
    private Date addDate;
    private Date updDate;
    private String estatusServer;
    private String uploadStatus;
    private Date downloadDate;
    private String description;
    private String unitOfMeasure;

    @Keep
    public Materials(){}


    @Generated(hash = 961660684)
    public Materials(Long materialIdLocal, Long materialIdServer,
            String idMaterialNavision, String building, String acronym,
            Integer addUser, Date addDate, Date updDate, String estatusServer,
            String uploadStatus, Date downloadDate, String description,
            String unitOfMeasure) {
        this.materialIdLocal = materialIdLocal;
        this.materialIdServer = materialIdServer;
        this.idMaterialNavision = idMaterialNavision;
        this.building = building;
        this.acronym = acronym;
        this.addUser = addUser;
        this.addDate = addDate;
        this.updDate = updDate;
        this.estatusServer = estatusServer;
        this.uploadStatus = uploadStatus;
        this.downloadDate = downloadDate;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
    }


    public Long getMaterialIdLocal() {
        return materialIdLocal;
    }
    public void setMaterialIdLocal(Long materialIdLocal) {
        this.materialIdLocal = materialIdLocal;
    }

    public Long getMaterialIdServer() {
        return materialIdServer;
    }

    public void setMaterialIdServer(Long materialIdServer) {
        this.materialIdServer = materialIdServer;
    }

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

    public String getEstatusServer() {
        return estatusServer;
    }

    public void setEstatusServer(String estatusServer) {
        this.estatusServer = estatusServer;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
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
