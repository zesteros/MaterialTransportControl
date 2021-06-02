package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Tags implements Serializable {
    private static final long serialVersionUID = -2262401134341082769L;
    @Keep
    public Tags(){}

    @Generated(hash = 922856919)
    public Tags(Long idTag, Integer idActivoTag, String tidTag, Integer addUser,
            Date addDate, Date updDate, String estatusServer, Date uploadDate,
            String uploadStatus, Date downloadDate) {
        this.idTag = idTag;
        this.idActivoTag = idActivoTag;
        this.tidTag = tidTag;
        this.addUser = addUser;
        this.addDate = addDate;
        this.updDate = updDate;
        this.estatusServer = estatusServer;
        this.uploadDate = uploadDate;
        this.uploadStatus = uploadStatus;
        this.downloadDate = downloadDate;
    }

    @Id
    private Long idTag;
    private Integer idActivoTag;
    private String tidTag;
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

    public Long getIdTag() {
        return idTag;
    }

    public void setIdTag(Long idTag) {
        this.idTag = idTag;
    }

    public Integer getIdActivoTag() {
        return idActivoTag;
    }

    public void setIdActivoTag(Integer idActivoTag) {
        this.idActivoTag = idActivoTag;
    }

    public String getTidTag() {
        return tidTag;
    }

    public void setTidTag(String tidTag) {
        this.tidTag = tidTag;
    }
}
