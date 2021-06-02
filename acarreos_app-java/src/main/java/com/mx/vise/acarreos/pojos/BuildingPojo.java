package com.mx.vise.acarreos.pojos;

import java.io.Serializable;

public class BuildingPojo implements Serializable {

	private static final long serialVersionUID = -8880684994175529379L;

	private String buildingId;

	private String buildingDescription;

	private String superintendent;

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public String getSuperintendent() {
		return superintendent;
	}

	public void setSuperintendent(String superintendent) {
		this.superintendent = superintendent;
	}

	public String getBuildingDescription() {
		return buildingDescription;
	}

	public void setBuildingDescription(String buildingDescription) {
		this.buildingDescription = buildingDescription;
	}

}
