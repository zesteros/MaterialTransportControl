package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Reprints implements Serializable {

    private static final long serialVersionUID = -3262409134341082768L;

    @Keep
    public Reprints(){}

    @Generated(hash = 2012294212)
    public Reprints(Long idReprintLocal, Integer idReprintServer, String sheetNumber,
            String coordinates, Integer addUser, Date addDate, Date updDate, String estatusServer,
            Date uploadDate, String uploadStatus, Date downloadDate) {
        this.idReprintLocal = idReprintLocal;
        this.idReprintServer = idReprintServer;
        this.sheetNumber = sheetNumber;
        this.coordinates = coordinates;
        this.addUser = addUser;
        this.addDate = addDate;
        this.updDate = updDate;
        this.estatusServer = estatusServer;
        this.uploadDate = uploadDate;
        this.uploadStatus = uploadStatus;
        this.downloadDate = downloadDate;
    }


    @Id
    private Long idReprintLocal;
    private Integer idReprintServer;
    private String sheetNumber;
    private String coordinates;
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

    public Long getIdReprintLocal() {
        return idReprintLocal;
    }

    public void setIdReprintLocal(Long idReprintLocal) {
        this.idReprintLocal = idReprintLocal;
    }

    public Integer getIdReprintServer() {
        return idReprintServer;
    }

    public void setIdReprintServer(Integer idReprintServer) {
        this.idReprintServer = idReprintServer;
    }

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

    /*
    *                 "CREATE TABLE " + SCRIPT.TABLE_NAME_REPRINTS + " (" +
                        SCRIPT.COLUMN_ID_REPRINT_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_ID_REPRINT_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_SHEET_NUMBER + " TEXT," +
                        SCRIPT.COLUMN_COORDINATES + " TEXT," +
                        SCRIPT.COLUMN_UPLOAD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT);";
    * */
}
