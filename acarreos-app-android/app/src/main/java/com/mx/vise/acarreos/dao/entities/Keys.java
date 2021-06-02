package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Keys implements Serializable {


    private static final long serialVersionUID = -2362412345619382711L;

    @Id
    private Long idKeyLocal;
    @Index(unique = true)
    private Long idKeyServer;
    private String keyA;
    private String keyB;
    private Integer sector;
    private Integer version;
    private Date updDate;
    private Integer updUser;
    private Date addDate;
    private Integer addUser;
    private String estatus;

    @Keep
    public Keys(){}

    @Generated(hash = 1074154345)
    public Keys(Long idKeyLocal, Long idKeyServer, String keyA, String keyB,
            Integer sector, Integer version, Date updDate, Integer updUser,
            Date addDate, Integer addUser, String estatus) {
        this.idKeyLocal = idKeyLocal;
        this.idKeyServer = idKeyServer;
        this.keyA = keyA;
        this.keyB = keyB;
        this.sector = sector;
        this.version = version;
        this.updDate = updDate;
        this.updUser = updUser;
        this.addDate = addDate;
        this.addUser = addUser;
        this.estatus = estatus;
    }

    public Long getIdKeyServer() {
        return idKeyServer;
    }

    public void setIdKeyServer(Long idKeyServer) {
        this.idKeyServer = idKeyServer;
    }

    public String getKeyA() {
        return keyA;
    }

    public void setKeyA(String keyA) {
        this.keyA = keyA;
    }

    public String getKeyB() {
        return keyB;
    }

    public void setKeyB(String keyB) {
        this.keyB = keyB;
    }

    public Integer getSector() {
        return sector;
    }

    public void setSector(Integer sector) {
        this.sector = sector;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Integer getAddUser() {
        return addUser;
    }

    public void setAddUser(Integer addUser) {
        this.addUser = addUser;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public Integer getUpdUser() {
        return updUser;
    }

    public void setUpdUser(Integer updUser) {
        this.updUser = updUser;
    }

    public Long getIdKeyLocal() {
        return this.idKeyLocal;
    }

    public void setIdKeyLocal(Long idKeyLocal) {
        this.idKeyLocal = idKeyLocal;
    }
}
