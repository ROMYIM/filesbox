package com.filebox.common.model;

import com.filebox.common.model.base.BaseFileBox;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class FileBox extends BaseFileBox<FileBox> {
	/**
	 * 信报箱状态 0-删除 1-使用中 2-待使用 3-维修中 4-停用
	 */
	public static final int BOX_STATUS_USED = 1;
	public static final int BOX_STATUS_WAIT = 2;
	public static final int BOX_STATUS_REPAIR = 3;
	public static final int BOX_STATUS_STOP = 4;
	public static final int BOX_STATUS_DELETE = 0;
	
	/*快递柜故障码说明：0  正常使用；1  柜门无法打开；2  柜门无法关闭；3  快递柜污损；4  快递柜被破坏；5  其他原因*/
	
	public static final int CODE_0 = 0;
	public static final int CODE_1 = 1;
	public static final int CODE_2 = 2;
	public static final int CODE_3 = 3;
	public static final int CODE_4 = 4;
	public static final int CODE_5 = 5;

	
	/**
	 * 柜门状态 1-打开 0关闭
	 */
	public static final int DOOR_STATUS_OPEN = 1;
	public static final int DOOR_STATUS_CLOSE = 0;
	
	
	/**
	 * 信报箱规格 1-小 2-中 3-大
	 */
	public static final int SPEC_SM = 1;
	public static final int SPEC_MD = 2;
	public static final int SPEC_LG = 3;
	
	/**
	 * 开门
	 */
	public static final int SOCKET_TYPE_OPEN =1;
	
	/**
	 * 更新特殊用户到终端数据库
	 */
	public static final int SOCKET_TYPE_UPDATE_SEPCIAL_USER =2;
	
	public boolean isCodeNormal(){
		return getCode()==CODE_0;
	}
	
	/**
	 * 箱子 
	 */
	public int owner_id;

	public int getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(int owner_id) {
		this.owner_id = owner_id;
	}
	
}
