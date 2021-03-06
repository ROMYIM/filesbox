package com.filebox.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseLoginLog<M extends BaseLoginLog<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public M setAccountId(java.lang.Integer accountId) {
		set("accountId", accountId);
		return (M)this;
	}

	public java.lang.Integer getAccountId() {
		return get("accountId");
	}

	public M setLoginAt(java.util.Date loginAt) {
		set("loginAt", loginAt);
		return (M)this;
	}

	public java.util.Date getLoginAt() {
		return get("loginAt");
	}

	public M setIp(java.lang.String ip) {
		set("ip", ip);
		return (M)this;
	}

	public java.lang.String getIp() {
		return get("ip");
	}

}
