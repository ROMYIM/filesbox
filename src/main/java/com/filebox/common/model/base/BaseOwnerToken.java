package com.filebox.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseOwnerToken<M extends BaseOwnerToken<M>> extends Model<M> implements IBean {

	public M setTokenId(java.lang.Integer tokenId) {
		set("token_id", tokenId);
		return (M)this;
	}

	public java.lang.Integer getTokenId() {
		return get("token_id");
	}

	public M setOwnerId(java.lang.Integer ownerId) {
		set("owner_id", ownerId);
		return (M)this;
	}

	public java.lang.Integer getOwnerId() {
		return get("owner_id");
	}

	public M setToken(java.lang.String token) {
		set("token", token);
		return (M)this;
	}

	public java.lang.String getToken() {
		return get("token");
	}

	public M setLoginTime(java.lang.Long loginTime) {
		set("login_time", loginTime);
		return (M)this;
	}

	public java.lang.Long getLoginTime() {
		return get("login_time");
	}

	public M setClientType(java.lang.String clientType) {
		set("client_type", clientType);
		return (M)this;
	}

	public java.lang.String getClientType() {
		return get("client_type");
	}

}