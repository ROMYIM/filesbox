package com.filebox.api.admin;

public class Device {
	private int cabinet_num;
	private String cabinetModel;
	private int doorStatus[];
	
	public int getCabinetNum() {
		return cabinet_num;
	}
	public void setCabinetNum(int cabinetNum) {
		this.cabinet_num = cabinetNum;
	}
	
	public int getCabinet_num() {
		return cabinet_num;
	}
	public void setCabinet_num(int cabinet_num) {
		this.cabinet_num = cabinet_num;
	}
	public String getCabinetModel() {
		return cabinetModel;
	}
	public void setCabinetModel(String cabinetModel) {
		this.cabinetModel = cabinetModel;
	}
	public int[] getDoorStatus() {
		return doorStatus;
	}
	public void setDoorStatus(int[] doorStatus) {
		this.doorStatus = doorStatus;
	}
	
	
}

