package com.mx.vise.acarreos.pojos;

import java.io.Serializable;

public class RequestDeviceRegisterPOJO implements Serializable {
	private static final long serialVersionUID = -7716361520214751291L;

	private String imei;
	private Integer idEmpleado;
	private String deviceName;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Integer getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(Integer idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return deviceName;
	}
}
