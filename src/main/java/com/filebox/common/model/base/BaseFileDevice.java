package com.filebox.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseFileDevice<M extends BaseFileDevice<M>> extends Model<M> implements IBean {

	public M setId(java.lang.String id) {
		set("id", id);
		return (M)this;
	}

	public java.lang.String getId() {
		return get("id");
	}

	public M setOperatorId(java.lang.Integer operatorId) {
		set("operator_id", operatorId);
		return (M)this;
	}

	public java.lang.Integer getOperatorId() {
		return get("operator_id");
	}

	public M setName(java.lang.String name) {
		set("name", name);
		return (M)this;
	}

	public java.lang.String getName() {
		return get("name");
	}

	public M setAddress(java.lang.String address) {
		set("address", address);
		return (M)this;
	}

	public java.lang.String getAddress() {
		return get("address");
	}

	public M setCabinetAmount(java.lang.Long cabinetAmount) {
		set("cabinet_amount", cabinetAmount);
		return (M)this;
	}

	public java.lang.Long getCabinetAmount() {
		return get("cabinet_amount");
	}

	public M setUsageRate(java.lang.String usageRate) {
		set("usage_rate", usageRate);
		return (M)this;
	}

	public java.lang.String getUsageRate() {
		return get("usage_rate");
	}

	public M setSysupdate(java.util.Date sysupdate) {
		set("sysupdate", sysupdate);
		return (M)this;
	}

	public java.util.Date getSysupdate() {
		return get("sysupdate");
	}

	public M setStatus(java.lang.Integer status) {
		set("status", status);
		return (M)this;
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public M setProvince(java.lang.String province) {
		set("province", province);
		return (M)this;
	}

	public java.lang.String getProvince() {
		return get("province");
	}

	public M setCity(java.lang.String city) {
		set("city", city);
		return (M)this;
	}

	public java.lang.String getCity() {
		return get("city");
	}

	public M setDistrict(java.lang.String district) {
		set("district", district);
		return (M)this;
	}

	public java.lang.String getDistrict() {
		return get("district");
	}

	public M setLng(java.math.BigDecimal lng) {
		set("lng", lng);
		return (M)this;
	}

	public java.math.BigDecimal getLng() {
		return get("lng");
	}

	public M setLat(java.math.BigDecimal lat) {
		set("lat", lat);
		return (M)this;
	}

	public java.math.BigDecimal getLat() {
		return get("lat");
	}

}