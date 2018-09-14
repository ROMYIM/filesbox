package com.filebox.common.model;

import com.filebox.common.model.base.BaseFile;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class File extends BaseFile<File> {
	/**
	 * 1 -取出 
	 */
	public static final int STATUS_TAKE_OUT = 1;
	/**
	 * 0 - 未取出
	 */
	public static final int STATUS_TAKE_IN = 0;
	
	
	/**
	 * 0 -密码 
	 */
	public static final int TAKE_WYAY_0 = 0;
	/**
	 * 1 - idCard
	 */
	public static final int TAKE_WYAY_1 = 1;
	/**
	 * 2 -指纹 
	 */
	public static final int TAKE_WYAY_2 = 2;
	/**
	 * 3 - 验证码
	 */
	public static final int TAKE_WYAY_3 = 3;
	
	

}
