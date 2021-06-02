package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DistancesByPoint implements Serializable {

    private static final long serialVersionUID = -2262439134341082769L;

    @Id
    private Long idDistanceLocal;
    private Long idDistanceServer;
    private Long idPointServer;
    private Float distance;
    private Integer addUser;
    private Date addDate;
    private Date updDate;
    private String estatusServer;
    private Date uploadDate;
    private String uploadStatus;
    private Date downloadDate;

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

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Keep
    public DistancesByPoint(){}

    @Generated(hash = 768791190)
    public DistancesByPoint(Long idDistanceLocal, Long idDistanceServer,
            Long idPointServer, Float distance, Integer addUser, Date addDate,
            Date updDate, String estatusServer, Date uploadDate,
            String uploadStatus, Date downloadDate) {
        this.idDistanceLocal = idDistanceLocal;
        this.idDistanceServer = idDistanceServer;
        this.idPointServer = idPointServer;
        this.distance = distance;
        this.addUser = addUser;
        this.addDate = addDate;
        this.updDate = updDate;
        this.estatusServer = estatusServer;
        this.uploadDate = uploadDate;
        this.uploadStatus = uploadStatus;
        this.downloadDate = downloadDate;
    }


    public Long getIdDistanceLocal() {
        return idDistanceLocal;
    }

    public void setIdDistanceLocal(Long idDistanceLocal) {
        this.idDistanceLocal = idDistanceLocal;
    }

    public Long getIdDistanceServer() {
        return idDistanceServer;
    }

    public void setIdDistanceServer(Long idDistanceServer) {
        this.idDistanceServer = idDistanceServer;
    }

    public Long getIdPointServer() {
        return idPointServer;
    }

    public void setIdPointServer(Long idPointServer) {
        this.idPointServer = idPointServer;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}
