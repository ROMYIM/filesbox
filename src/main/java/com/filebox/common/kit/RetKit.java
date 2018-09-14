package com.filebox.common.kit;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.json.Json;
import com.jfinal.kit.Ret;

/**
 * 返回值封装，常用于业务层需要多个返回值
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class RetKit extends HashMap {

	private static final String CODE = "code";
	private static final String MSG = "msg";
	private static final String DATA = "data";
	private static final String SUCCESS = "success";
	
	
	public static RetKit ok() {
		return new RetKit().setOk();
	}
	public static RetKit okData(Object value) {
		return new RetKit().setOkData(value);
	}
	
	public static RetKit ok(Object value) {
		return new RetKit().setOk(value);
	}
	
	public static RetKit ok(Object key, Object value) {
		return ok().set(key, value);
	}
	
	public static RetKit fail() {
		return new RetKit().setFail();
	}
	
	public static RetKit fail(int code,Object msg) {
		return new RetKit().setFail(code,msg);
	}
	
	public static RetKit ok(int code,Object msg) {
		return new RetKit().setOk(code, msg);
	}
	
	public static RetKit fail(Object msg) {
		return new RetKit().setFail(msg);
	}
	
	public static RetKit fail(Object key, Object value) {
		return fail().set(key, value);
	}
	
	public static RetKit create() {
		return new RetKit();
	}
	
	public static RetKit create(Object key, Object value) {
		return new RetKit().set(key, value);
	}
	
	public boolean success() {
		Boolean success = (Boolean)get(SUCCESS);
		return success != null && success;
	}
	
	public RetKit setOk() {
		super.put(SUCCESS, true);
		super.put(CODE, 200);
		super.put(MSG, "操作成功");
		return this;
	}
	
	public RetKit setOk(Object msg) {
		super.put(SUCCESS, true);
		super.put(CODE, 200);
		super.put(MSG, msg);
		return this;
	}
	
	
	public RetKit setOk(int code,Object msg) {
		super.put(SUCCESS, true);
		super.put(CODE, code);
		super.put(MSG, "操作成功");
		return this;
	}
	
	public RetKit setOkData(Object value) {
		super.put(DATA, value);
		super.put(SUCCESS, true);
		super.put(CODE, 200);
		super.put(MSG, "操作成功");
		return this;
	}
	
	public Ret okRet(Ret ret){
		ret.set(SUCCESS, true);
		ret.set(CODE, 200);
		ret.set(MSG, "操作成功");
		return ret;
	}
	
	public RetKit setFail(Object msg) {
		super.put(SUCCESS, false);
		super.put(CODE, 501);
		super.put(MSG, msg);
		return this;
	}
	
	public RetKit setFail(int code,Object msg) {
		super.put(SUCCESS, false);
		super.put(CODE, code);
		super.put(MSG, msg);
		return this;
	}
	
	public RetKit setFail() {
		super.put(SUCCESS, false);
		super.put(CODE, 501);
		super.put(MSG, "操作失败");
		return this;
	}
	
	public RetKit set(Object key, Object value) {
		super.put(key, value);
		return this;
	}
	
	public RetKit setCode(int value) {
		super.put(CODE, value);
		return this;
	}
	
	public RetKit setMsg(String value) {
		super.put(MSG, value);
		return this;
	}
	
	public RetKit set(Map map) {
		super.putAll(map);
		return this;
	}
	
	public RetKit set(RetKit ret) {
		super.putAll(ret);
		return this;
	}
	
	public RetKit delete(Object key) {
		super.remove(key);
		return this;
	}
	
	public <T> T getAs(Object key) {
		return (T)get(key);
	}
	
	public String getStr(Object key) {
		return (String)get(key);
	}

	public Integer getInt(Object key) {
		return (Integer)get(key);
	}

	public Long getLong(Object key) {
		return (Long)get(key);
	}

	public Boolean getBoolean(Object key) {
		return (Boolean)get(key);
	}
	
	/**
	 * key 存在，并且 value 不为 null
	 */
	public boolean notNull(Object key) {
		return get(key) != null;
	}
	
	/**
	 * key 不存在，或者 key 存在但 value 为null
	 */
	public boolean isNull(Object key) {
		return get(key) == null;
	}
	
	/**
	 * key 存在，并且 value 为 true，则返回 true
	 */
	public boolean isTrue(Object key) {
		Object value = get(key);
		return (value instanceof Boolean && ((Boolean)value == true));
	}
	
	/**
	 * key 存在，并且 value 为 false，则返回 true
	 */
	public boolean isFalse(Object key) {
		Object value = get(key);
		return (value instanceof Boolean && ((Boolean)value == false));
	}
	
	public String toJson() {
		return Json.getJson().toJson(this);
	}
	
	public boolean equals(Object ret) {
		return ret instanceof RetKit && super.equals(ret);
	}
}


