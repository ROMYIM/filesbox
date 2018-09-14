package com.filebox.api.owner;

import java.util.HashSet;
import java.util.List;

import com.filebox.common.constant.FileConstant;
import com.filebox.common.kit.RetKit;
import com.filebox.common.model.OwnerDetail;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import sun.print.resources.serviceui;

/**
 * @Description:TODO(箱子用户)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月4日
 */
public class ApiOwnerController extends Controller {
	public final static ApiOwnerService srv = ApiOwnerService.me;

	/**
	 * 更新用户状态
	 */
	@Clear
	public void updateOwnerStatus() {
		int ownerId = getParaToInt("owner_id");
		int status = getParaToInt("status");
		RetKit retKit = srv.updateOwnerStatus(ownerId, status);
		renderJson(retKit);
	}
	
	
	/**
	 * 更新用户指定设备状态
	 */
	@Clear
	public void updateOwnerDeviceStatus() {
		String deviceId = getHeader(FileConstant.deviceId);
		int ownerId = getParaToInt("owner_id");
		int status = getParaToInt("status");
		RetKit retKit = srv.updateOwnerDeviceStatus(ownerId, deviceId, status);
		renderJson(retKit);
	}
	

	/**
	 * 删除用户
	 */
	@Clear
	public void deleteOwner() {
		int ownerId = getParaToInt("owner_id");
		RetKit retKit = srv.deleteOwner(ownerId);
		renderJson(retKit);
	}

	/**
	 * 搜索用户
	 */
	@Clear
	public void searchOwner() {
		String deviceId = getHeader(FileConstant.deviceId);
		String searchValue = getPara("searchValue");
		RetKit retKit = srv.searchOwner(deviceId, searchValue, getParaToInt("pageSize", 10),
				getParaToInt("pageNum", 1));
		renderJson(retKit);
	}

	/**
	 * 随机生成指定范围并且排除已经存在的数 利用HashSet的特征，只能存放不同的值 min 指定范围最小值 max 指定范围最大值 n 随机数个数
	 * HashSet<Integer> set随机数结果集
	 */
	@Clear
	private void randomSet(int min, int max, int n, HashSet<Integer> set) {
		int temp = 0; // 记录当前随机到的不重复数字
		if (n > (max - min + 1) || max < min) {
			return;
		}
		for (int i = 0; i < n; i++) {
			// 调用Math.random()方法
			int num = (int) (Math.random() * (max - min)) + min;
			temp = num;
			set.add(num);// 将不同的数存入HashSet中
		}
		int setSize = set.size();
		// 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小
		if (setSize < (n + set.size())) {
			randomSet(min, max, n - setSize, set);// 递归
		} else {
			System.out.println("随机数: " + temp);
		}
	}

	/**
	 * 
	 * 获取一个该设备指纹不重复随机数
	 */
	@Clear
	public void getFingerPrintRandom() {
		String deviceId = getHeader(FileConstant.deviceId);
		RetKit retKit = srv.getFingerPrintRandom(deviceId);
		renderJson(retKit);
	}

}
