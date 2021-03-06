package com.filebox.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseOwnerDetail<M extends BaseOwnerDetail<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public M setUnit(java.lang.String unit) {
		set("unit", unit);
		return (M)this;
	}

	public java.lang.String getUnit() {
		return get("unit");
	}

	public M setFloor(java.lang.String floor) {
		set("floor", floor);
		return (M)this;
	}

	public java.lang.String getFloor() {
		return get("floor");
	}

	public M setRoomAddress(java.lang.String roomAddress) {
		set("room_address", roomAddress);
		return (M)this;
	}

	public java.lang.String getRoomAddress() {
		return get("room_address");
	}

	public M setOwnerId(java.lang.Integer ownerId) {
		set("owner_id", ownerId);
		return (M)this;
	}

	public java.lang.Integer getOwnerId() {
		return get("owner_id");
	}

	public M setDeviceId(java.lang.String deviceId) {
		set("device_id", deviceId);
		return (M)this;
	}

	public java.lang.String getDeviceId() {
		return get("device_id");
	}

	public M setPasswordCard(java.lang.String passwordCard) {
		set("password_card", passwordCard);
		return (M)this;
	}

	public java.lang.String getPasswordCard() {
		return get("password_card");
	}

	public M setPasswordFingerprint(java.lang.String passwordFingerprint) {
		set("password_fingerprint", passwordFingerprint);
		return (M)this;
	}

	public java.lang.String getPasswordFingerprint() {
		return get("password_fingerprint");
	}

	public M setOwnerDeviceStatus(java.lang.Integer ownerDeviceStatus) {
		set("owner_device_status", ownerDeviceStatus);
		return (M)this;
	}

	public java.lang.Integer getOwnerDeviceStatus() {
		return get("owner_device_status");
	}

	public M setSysupdate(java.util.Date sysupdate) {
		set("sysupdate", sysupdate);
		return (M)this;
	}

	public java.util.Date getSysupdate() {
		return get("sysupdate");
	}

}
