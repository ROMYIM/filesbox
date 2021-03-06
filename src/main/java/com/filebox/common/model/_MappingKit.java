package com.filebox.common.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("account", "id", Account.class);
		// Composite Primary Key order: account_id,device_id
		arp.addMapping("account_device", "account_id,device_id", AccountDevice.class);
		arp.addMapping("admin_log", "id", AdminLog.class);
		arp.addMapping("adv_type", "id", AdvType.class);
		arp.addMapping("advertising", "id", Advertising.class);
		arp.addMapping("advertising_phone", "id", AdvertisingPhone.class);
		arp.addMapping("api_log", "id", ApiLog.class);
		arp.addMapping("api_session", "id", ApiSession.class);
		arp.addMapping("bind_application", "id", BindApplication.class);
		arp.addMapping("bonus_points", "id", BonusPoints.class);
		arp.addMapping("box_order", "id", BoxOrder.class);
		arp.addMapping("courier_device", "id", CourierDevice.class);
		arp.addMapping("file", "id", File.class);
		arp.addMapping("file_box", "id", FileBox.class);
		arp.addMapping("file_box_owner", "id", FileBoxOwner.class);
		arp.addMapping("file_courier", "id", FileCourier.class);
		arp.addMapping("file_device", "id", FileDevice.class);
		arp.addMapping("information", "id", Information.class);
		arp.addMapping("login_log", "id", LoginLog.class);
		arp.addMapping("message", "id", Message.class);
		arp.addMapping("model", "id", Model.class);
		arp.addMapping("model_detail", "id", ModelDetail.class);
		arp.addMapping("operator", "id", Operator.class);
		arp.addMapping("owner_detail", "id", OwnerDetail.class);
		arp.addMapping("owner_device", "id", OwnerDevice.class);
		arp.addMapping("owner_token", "token_id", OwnerToken.class);
		arp.addMapping("pickup_record", "file_id", PickupRecord.class);
		arp.addMapping("postage", "id", Postage.class);
		arp.addMapping("protocol", "id", Protocol.class);
		arp.addMapping("rechange_bill", "id", RechangeBill.class);
		arp.addMapping("repair_application", "id", RepairApplication.class);
		arp.addMapping("repair_man", "id", RepairMan.class);
		arp.addMapping("session", "id", Session.class);
		arp.addMapping("special_user", "id", SpecialUser.class);
		arp.addMapping("sys_menu", "id", SysMenu.class);
		arp.addMapping("sys_role", "id", SysRole.class);
		arp.addMapping("sys_role_menu", "id", SysRoleMenu.class);
	}
}

