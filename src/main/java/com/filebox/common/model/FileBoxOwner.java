package com.filebox.common.model;

import com.filebox.common.model.base.BaseFileBoxOwner;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class FileBoxOwner extends BaseFileBoxOwner<FileBoxOwner> {
	public static final int STATUS_OK = 1;
	public static final int STATUS_LOCK = 0;
	
	public boolean isStatusOk(){
		return getStatus() == STATUS_OK;
	}
	
	public boolean isStatusLock(){
		return getStatus() == STATUS_LOCK;
	}
}
