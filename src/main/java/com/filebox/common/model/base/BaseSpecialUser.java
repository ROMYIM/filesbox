package com.filebox.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSpecialUser<M extends BaseSpecialUser<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public M setDeviceId(java.lang.String deviceId) {
		set("device_id", deviceId);
		return (M)this;
	}

	public java.lang.String getDeviceId() {
		return get("device_id");
	}

	public M setPhone(java.lang.String phone) {
		set("phone", phone);
		return (M)this;
	}

	public java.lang.String getPhone() {
		return get("phone");
	}

	public M setIcCard(java.lang.String icCard) {
		set("ic_card", icCard);
		return (M)this;
	}

	public java.lang.String getIcCard() {
		return get("ic_card");
	}

	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public M setTerminal(java.lang.Integer terminal) {
		set("terminal", terminal);
		return (M)this;
	}

	public java.lang.Integer getTerminal() {
		return get("terminal");
	}

	public M setSysupdate(java.util.Date sysupdate) {
		set("sysupdate", sysupdate);
		return (M)this;
	}

	public java.util.Date getSysupdate() {
		return get("sysupdate");
	}

}
