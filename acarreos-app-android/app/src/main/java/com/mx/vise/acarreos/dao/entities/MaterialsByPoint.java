package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

@Entity
public class MaterialsByPoint implements Serializable {

    private static final long serialVersionUID = -1112409134341082769L;

    @Id
    private Long idMaterialByPointLocal;
    @Index(unique = true)
    private Long idMaterialByPointServer;
    private Long idMaterialServer;
    private Long idPointServer;
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
    public MaterialsByPoint() {
    }

    @Generated(hash = 843780831)
    public MaterialsByPoint(Long idMaterialByPointLocal,
            Long idMaterialByPointServer, Long idMaterialServer, Long idPointServer,
            Integer addUser, Date addDate, Date updDate, String estatusServer,
            Date uploadDate, String uploadStatus, Date downloadDate) {
        this.idMaterialByPointLocal = idMaterialByPointLocal;
        this.idMaterialByPointServer = idMaterialByPointServer;
        this.idMaterialServer = idMaterialServer;
        this.idPointServer = idPointServer;
        this.addUser = addUser;
        this.addDate = addDate;
        this.updDate = updDate;
        this.estatusServer = estatusServer;
        this.uploadDate = uploadDate;
        this.uploadStatus = uploadStatus;
        this.downloadDate = downloadDate;
    }

    public void setIdMaterialByPointLocal(Long idMaterialByPointLocal) {
        this.idMaterialByPointLocal = idMaterialByPointLocal;
    }

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

    public Long getIdMaterialByPointLocal() {
        return this.idMaterialByPointLocal;
    }
}
