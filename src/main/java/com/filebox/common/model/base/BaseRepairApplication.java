package com.filebox.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseRepairApplication<M extends BaseRepairApplication<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public M setType(java.lang.Integer type) {
		set("type", type);
		return (M)this;
	}

	public java.lang.Integer getType() {
		return get("type");
	}

	public M setOwnerId(java.lang.Integer ownerId) {
		set("owner_id", ownerId);
		return (M)this;
	}

	public java.lang.Integer getOwnerId() {
		return get("owner_id");
	}

	public M setPhone(java.lang.String phone) {
		set("phone", phone);
		return (M)this;
	}

	public java.lang.String getPhone() {
		return get("phone");
	}

	public M setDeviceId(java.lang.String deviceId) {
		set("device_id", deviceId);
		return (M)this;
	}

	public java.lang.String getDeviceId() {
		return get("device_id");
	}

	public M setCabinetNum(java.lang.Integer cabinetNum) {
		set("cabinet_num", cabinetNum);
		return (M)this;
	}

	public java.lang.Integer getCabinetNum() {
		return get("cabinet_num");
	}

	public M setNumber(java.lang.Integer number) {
		set("number", number);
		return (M)this;
	}

	public java.lang.Integer getNumber() {
		return get("number");
	}

	public M setRepairDetail(java.lang.String repairDetail) {
		set("repair_detail", repairDetail);
		return (M)this;
	}

	public java.lang.String getRepairDetail() {
		return get("repair_detail");
	}

	public M setPhotoUrl(java.lang.String photoUrl) {
		set("photo_url", photoUrl);
		return (M)this;
	}

	public java.lang.String getPhotoUrl() {
		return get("photo_url");
	}

	public M setApplicationTime(java.util.Date applicationTime) {
		set("application_time", applicationTime);
		return (M)this;
	}

	public java.util.Date getApplicationTime() {
		return get("application_time");
	}

	public M setOrderTime(java.util.Date orderTime) {
		set("order_time", orderTime);
		return (M)this;
	}

	public java.util.Date getOrderTime() {
		return get("order_time");
	}

	public M setFixedTime(java.util.Date fixedTime) {
		set("fixed_time", fixedTime);
		return (M)this;
	}

	public java.util.Date getFixedTime() {
		return get("fixed_time");
	}

	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public M setRepairManId(java.lang.Integer repairManId) {
		set("repair_man_id", repairManId);
		return (M)this;
	}

	public java.lang.Integer getRepairManId() {
		return get("repair_man_id");
	}

	public M setRepairManPhone(java.lang.String repairManPhone) {
		set("repair_man_phone", repairManPhone);
		return (M)this;
	}

	public java.lang.String getRepairManPhone() {
		return get("repair_man_phone");
	}

}
