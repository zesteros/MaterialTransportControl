package com.mx.vise.acarreos.pojos.carries;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mx.vise.util.CustomCalendarDeserializer;
import com.mx.vise.util.CustomDateSerializer;
import com.mx.vise.util.JsonDate;
import com.mx.vise.util.UtilDate;

public class TicketPOJO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 228621799806641635L;
	private Integer ticketType;
    private String building;
    private String sheetNumber;
    private String rearLicensePlate;
    private Float increase;
    private Float totalVolume;
    private MaterialsPOJO material;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date exitDate;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date arrivalDate;
    private Integer userIdBank;
    private String usernameBank;
    private Integer userIdThrow;
    private String userNameThrow;
    private PointPOJO origin;
    private LatLng exitCoordinates;
    private LatLng arrivalCoordinates;
    private Float discount;
    private Float distance;
    private PointPOJO destiny;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date expirationDate;
    private Integer unitOfMeasure;
    private Integer addUser;
    private Integer idTicket;



    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
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
        return totalVolume;
    }

    public void setCapacity(Float totalVolume) {
        this.totalVolume = totalVolume;
    }

    public MaterialsPOJO getMaterial() {
        return material;
    }

    public void setMaterial(MaterialsPOJO material) {
        this.material = material;
    }

    public PointPOJO getOrigin() {
        return origin;
    }

    public void setOrigin(PointPOJO origin) {
        this.origin = origin;
    }

    public LatLng getExitCoordinates() {
        return exitCoordinates;
    }

    public void setExitCoordinates(LatLng exitCoordinates) {
        this.exitCoordinates = exitCoordinates;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Integer getTicketType() {
        return ticketType;
    }

    public void setTicketType(Integer ticketType) {
        this.ticketType = ticketType;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
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

    public LatLng getArrivalCoordinates() {
        return arrivalCoordinates;
    }

    public void setArrivalCoordinates(LatLng arrivalCoordinates) {
        this.arrivalCoordinates = arrivalCoordinates;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public PointPOJO getDestiny() {
        return destiny;
    }

    public void setDestiny(PointPOJO destiny) {
        this.destiny = destiny;
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
    public Integer getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Integer idTicket) {
        this.idTicket = idTicket;
    }
}
