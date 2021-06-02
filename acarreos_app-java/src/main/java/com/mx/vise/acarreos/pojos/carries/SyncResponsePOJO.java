package com.mx.vise.acarreos.pojos.carries;

import java.io.Serializable;
import java.util.ArrayList;
import com.mx.vise.nav.sistemas.activos.pojos.TagPOJO;

public class SyncResponsePOJO implements Serializable{
	

	private static final long serialVersionUID = 3820970969258901852L;

	private ArrayList<PointPOJO> newPoints;
	private ArrayList<PointPOJO> pointsChanged;
	private ArrayList<MaterialsPOJO> newMaterials;
	private ArrayList<MaterialsPOJO> materialsChanged;
	private ArrayList<MaterialsByPointPOJO> newMaterialsByPoint;
	private ArrayList<MaterialsByPointPOJO> materialsByPointChanged;
	private ArrayList<TagPOJO> tags;
	private ArrayList<DistancePOJO> newDistances;
	private ArrayList<DistancePOJO> distancesChanged;
	private ArrayList<KeyPOJO> newKeys;
	private ArrayList<KeyPOJO> keysChanged;


	public ArrayList<PointPOJO> getPointsChanged() {
		return pointsChanged;
	}
	public void setPointsChanged(ArrayList<PointPOJO> pointsChanged) {
		this.pointsChanged = pointsChanged;
	}
	public ArrayList<PointPOJO> getNewPoints() {
		return newPoints;
	}
	public void setNewPoints(ArrayList<PointPOJO> newPoints) {
		this.newPoints = newPoints;
	}


	public ArrayList<MaterialsPOJO> getMaterialsChanged() {
		return materialsChanged;
	}

	public void setMaterialsChanged(ArrayList<MaterialsPOJO> materialsChanged) {
		this.materialsChanged = materialsChanged;
	}

	public ArrayList<MaterialsByPointPOJO> getMaterialsByPointChanged() {
		return materialsByPointChanged;
	}

	public void setMaterialsByPointChanged(ArrayList<MaterialsByPointPOJO> materialsByPointChanged) {
		this.materialsByPointChanged = materialsByPointChanged;
	}

	public ArrayList<MaterialsByPointPOJO> getNewMaterialsByPoint() {
		return newMaterialsByPoint;
	}

	public void setNewMaterialsByPoint(ArrayList<MaterialsByPointPOJO> newMaterialsByPoint) {
		this.newMaterialsByPoint = newMaterialsByPoint;
	}

	public ArrayList<MaterialsPOJO> getNewMaterials() {
		return newMaterials;
	}

	public void setNewMaterials(ArrayList<MaterialsPOJO> newMaterials) {
		this.newMaterials = newMaterials;
	}
	public void setTags(ArrayList<TagPOJO> tags) {
		this.tags = tags;
	}

	public ArrayList<TagPOJO> getTags(){
		return tags;
	}

	public ArrayList<DistancePOJO> getNewDistances() {
		return newDistances;
	}
	public void setNewDistances(ArrayList<DistancePOJO> distances) {
		this.newDistances = distances;
	}
	public void setDistancesChanged(ArrayList<DistancePOJO> changedDistances) {
		this.distancesChanged = changedDistances;

	}
	public ArrayList<DistancePOJO> getDistancesChanged(){
		return distancesChanged;
	}
	public ArrayList<KeyPOJO> getKeysChanged() {
		return keysChanged;
	}
	public void setKeysChanged(ArrayList<KeyPOJO> keysChanged) {
		this.keysChanged = keysChanged;
	}
	public ArrayList<KeyPOJO> getNewKeys() {
		return newKeys;
	}
	public void setNewKeys(ArrayList<KeyPOJO> newKeys) {
		this.newKeys = newKeys;
	}

}
