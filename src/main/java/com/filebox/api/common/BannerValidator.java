package com.filebox.api.common;

import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RetKit;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
* @Description:TODO(广告)
* @author 作者 : jinghui.su
* @date 创建时间：2017年5月31日
*/
public class BannerValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		String deviceId = c.getHeader(FileConstant.deviceId);
		if (deviceId==null) {
			addError("msg", "设备ID不能为空");
			return;
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(RetKit.fail(c.getAttr("msg")));
	}

}
