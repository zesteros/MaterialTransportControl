package com.mx.vise.acarreos.dao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Tickets implements Serializable {

    private static final long serialVersionUID = -2262409134341082769L;


    @Id
    private Long idTicketLocal;
    private Long idTicketServer;
    private Integer ticketType;
    private String building;
    private String sheetNumber;
    private String rearLicensePlate;
    private Float increase;
    private Float capacity;
    private Long idMaterialServer;
    private Date exitDate;
    private Date arrivalDate;
    private Integer userIdBank;
    private String usernameBank;
    private Integer userIdThrow;
    private String userNameThrow;
    private Long idPuntoServerOrigin;
    private String exitCoordinates;
    private String arrivalCoordinates;
    private Float discount;
    private Float distance;
    private Long getIdPuntoServerDestiny;
    private Date expirationDate;
    private Integer unitOfMeasure;
    private Integer addUser;
    private Date addDate;
    private String uploadStatus;
    private Date uploadDate;
    private Integer cancelInApp;
    private String cancelUploadStatus;

    @Keep
    public Tickets(){}

    @Generated(hash = 1924218698)
    public Tickets(Long idTicketLocal, Long idTicketServer, Integer ticketType,
            String building, String sheetNumber, String rearLicensePlate,
            Float increase, Float capacity, Long idMaterialServer, Date exitDate,
            Date arrivalDate, Integer userIdBank, String usernameBank,
            Integer userIdThrow, String userNameThrow, Long idPuntoServerOrigin,
            String exitCoordinates, String arrivalCoordinates, Float discount,
            Float distance, Long getIdPuntoServerDestiny, Date expirationDate,
            Integer unitOfMeasure, Integer addUser, Date addDate,
            String uploadStatus, Date uploadDate, Integer cancelInApp,
            String cancelUploadStatus) {
        this.idTicketLocal = idTicketLocal;
        this.idTicketServer = idTicketServer;
        this.ticketType = ticketType;
        this.building = building;
        this.sheetNumber = sheetNumber;
        this.rearLicensePlate = rearLicensePlate;
        this.increase = increase;
        this.capacity = capacity;
        this.idMaterialServer = idMaterialServer;
        this.exitDate = exitDate;
        this.arrivalDate = arrivalDate;
        this.userIdBank = userIdBank;
        this.usernameBank = usernameBank;
        this.userIdThrow = userIdThrow;
        this.userNameThrow = userNameThrow;
        this.idPuntoServerOrigin = idPuntoServerOrigin;
        this.exitCoordinates = exitCoordinates;
        this.arrivalCoordinates = arrivalCoordinates;
        this.discount = discount;
        this.distance = distance;
        this.getIdPuntoServerDestiny = getIdPuntoServerDestiny;
        this.expirationDate = expirationDate;
        this.unitOfMeasure = unitOfMeasure;
        this.addUser = addUser;
        this.addDate = addDate;
        this.uploadStatus = uploadStatus;
        this.uploadDate = uploadDate;
        this.cancelInApp = cancelInApp;
        this.cancelUploadStatus = cancelUploadStatus;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getIdTicketLocal() {
        return idTicketLocal;
    }

    public void setIdTicketLocal(Long idTicketLocal) {
        this.idTicketLocal = idTicketLocal;
    }

    public Long getIdTicketServer() {
        return idTicketServer;
    }

    public void setIdTicketServer(Long idTicketServer) {
        this.idTicketServer = idTicketServer;
    }

    public Integer getTicketType() {
        return ticketType;
    }

    public void setTicketType(Integer ticketType) {
        this.ticketType = ticketType;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public String getRearLicensePlate() {
        return rearLicensePlate;
    }

    public void setRearLicensePlate(String rearLicensePlate) {
        this.rearLicensePlate = rearLicensePlate;
    }

    public Float getIncrease() {
        return increase;
    }

    public void setIncrease(Float increase) {
        this.increase = increase;
    }

    public Float getCapacity() {
        return capacity;
    }

    public void setCapacity(Float capacity) {
        this.capacity = capacity;
    }

    public Long getIdMaterialServer() {
        return idMaterialServer;
    }

    public void setIdMaterialServer(Long idMaterialServer) {
        this.idMaterialServer = idMaterialServer;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Integer getUserIdBank() {
        return userIdBank;
    }

    public void setUserIdBank(Integer userIdBank) {
        this.userIdBank = userIdBank;
    }

    public String getUsernameBank() {
        return usernameBank;
    }

    public void setUsernameBank(String usernameBank) {
        this.usernameBank = usernameBank;
    }

    public Integer getUserIdThrow() {
        return userIdThrow;
    }

    public void setUserIdThrow(Integer userIdThrow) {
        this.userIdThrow = userIdThrow;
    }

    public String getUserNameThrow() {
        return userNameThrow;
    }

    public void setUserNameThrow(String userNameThrow) {
        this.userNameThrow = userNameThrow;
    }

    public Long getIdPuntoServerOrigin() {
        return idPuntoServerOrigin;
    }

    public void setIdPuntoServerOrigin(Long idPuntoServerOrigin) {
        this.idPuntoServerOrigin = idPuntoServerOrigin;
    }

    public String getExitCoordinates() {
        return exitCoordinates;
    }

    public void setExitCoordinates(String exitCoordinates) {
        this.exitCoordinates = exitCoordinates;
    }

    public String getArrivalCoordinates() {
        return arrivalCoordinates;
    }

    public void setArrivalCoordinates(String arrivalCoordinates) {
        this.arrivalCoordinates = arrivalCoordinates;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Long getGetIdPuntoServerDestiny() {
        return getIdPuntoServerDestiny;
    }

    public void setGetIdPuntoServerDestiny(Long getIdPuntoServerDestiny) {
        this.getIdPuntoServerDestiny = getIdPuntoServerDestiny;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Integer unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
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

    public Integer getCancelInApp() {
        return cancelInApp;
    }

    public void setCancelInApp(Integer cancelInApp) {
        this.cancelInApp = cancelInApp;
    }

    public String getCancelUploadStatus() {
        return cancelUploadStatus;
    }

    public void setCancelUploadStatus(String cancelUploadStatus) {
        this.cancelUploadStatus = cancelUploadStatus;
    }
}
