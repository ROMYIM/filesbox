$(document).ready( function () {
	//设置当前导航样式
	/*setMenuList();*/
	setCurrentNavMenu();
	//DataTables  
	$.extend(true, $.fn.dataTable.defaults, {
		"dom": '', //tp<il>
		ordering: false,
		"paging": false,
		"autoWidth": false,
		"initComplete": function () {
			//$(".img").fancybox();
			checkBoxRadio("dataTable");
		},
		search:{ "smart": false },//关闭智能搜索
		language: {
	        "sProcessing": "处理中...",
	        "sLengthMenu": "显示 _MENU_ 项结果",
	        "sZeroRecords": "没有匹配结果",
	        "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
	        "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
	        "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
	        "sInfoPostFix": "",
	        "sSearch": "搜索:",
	        "sUrl": "",
	        "sEmptyTable": "表中数据为空",
	        "sLoadingRecords": "载入中...",
	        "sInfoThousands": ",",
	        "oPaginate": {
	            "sFirst": "首页",
	            "sPrevious": "上页",
	            "sNext": "下页",
	            "sLast": "末页"
	        },
	        "oAria": {
	            "sSortAscending": ": 以升序排列此列",
	            "sSortDescending": ": 以降序排列此列"
	        }
	    }
	});	
	 //错误信息提示
    $.fn.dataTable.ext.errMode = function (s, h, m) {
        if (h == 1) {
            alert("连接服务器失败！");
        } else if (h == 7) {
            alert("返回数据错误！");
        }
    };
})
/**
 * 设置菜单样式
 
*/

function setCurrentNavMenu() {
	var url = location.pathname, navMenus = $(".affix ul.nav-pills li");
	if (url.indexOf('/expbox',5)>0) {
		navMenus.eq(0).addClass("active");
	} 
	else if (url.indexOf('/advPhone')>0) {
		navMenus.eq(1).addClass("active");
	}else if (url.indexOf('/courier')>0) {
		navMenus.eq(2).addClass("active");
	} else if (url.indexOf('/express')>0) {
		navMenus.eq(3).addClass("active");
	} else if (url.indexOf('/adv')>0) {
		navMenus.eq(0).addClass("active");
	}
	else if (url.indexOf('/advPhone')>0) {
		navMenus.eq(1).addClass("active");
	}else if (url.indexOf('/account')>0) {
		navMenus.eq(2).addClass("active");
	}else if (url.indexOf('/operator')>0) {
		navMenus.eq(3).addClass("active");
	}else if (url.indexOf('/model')>0) {
		navMenus.eq(4).addClass("active");
	}else if (url.indexOf('/finance')>0) {
		navMenus.eq(5).addClass("active");
	}else if (url.indexOf('/company')>0) {
		navMenus.eq(6).addClass("active");
	}else if (url.indexOf('/protocol')>0) {
		navMenus.eq(7).addClass("active");
	}
}

/*function setMenuList(){
	$.ajax({
		type:"get",
		dateType:"json",
		data:"",
		url:"/account/menu",
		success:function(menus){
			//console.log(menus);
			var menuList = menus.menuList;
			var menuListStr ="";
			for(var i=0;i < menus.menuList.length;i++){
				if (menuList[i].level > 0) {
					menuListStr = menuListStr+"<li><a href='"+menuList[i].url+"'>"+menuList[i].name+"</a></li>"
				}
			}
			$("#menuList").html(menuListStr);
		}
	})
}*/

/**
 * 单选
 * @param tableId
 */
function checkBoxRadio(tableId){
	//勾选效果
	$("#"+tableId).on("change",":checkbox",function() {
		/*if($(this).is(":checked")){
        	$(this).closest("tr").addClass('selected');
    	}else{
    		$(this).closest("tr").removeClass('selected');
    	}*/
		$('#'+tableId+' tr').removeClass('selected');
		$(":checkbox").prop("checked",false);
		$(this).prop("checked",true);
		if (!$(this).is("[name='check-all']")) {
			$(this).closest("tr").addClass('selected');
		}

	}).on("click",".table_checkbox",function(event) {
	    //点击单元格即点击复选框
	    !$(event.target).is(":checkbox") && $(":checkbox",this).trigger("click");
	})

}

/**
 * 多选
 * @param tableId
 */
function checkBox(tableId){
	//勾选效果
	$("#"+tableId).on("change",":checkbox",function() {
	    if ($(this).is("[name='check-all']")) {
	        //全选
	        if($(this).is(":checked")){
	        	$(":checkbox").prop("checked",true);
	       		$('#'+tableId+' tbody tr').addClass('selected');
	        }else{
	        	$(":checkbox").prop("checked",false);
	       		$('#'+tableId+' tbody tr').removeClass('selected');
	        }
	    }else{
	    	if($(this).is(":checked")){
	        	$(this).closest("tr").addClass('selected');
	    	}else{
	    		$(this).closest("tr").removeClass('selected');
	    	}
	    }
	}).on("click",".table_checkbox",function(event) {
	    //点击单元格即点击复选框
	    !$(event.target).is(":checkbox") && $(":checkbox",this).trigger("click");
	})

}


/**
 * 确认对话框层，点击确定才真正操作
 * @param msg 对话框的提示文字
 * @param operationUrl 点击确认后请求到的目标 url
 */
function confirmAjaxAction(msg, operationUrl) {
	layer.confirm(msg, {
		icon: 0
		, title:''                                    // 设置为空串时，title消失，并自动切换关闭按钮样式，比较好的体验
		, shade: 0.4
		, offset: "139px"
	}, function(index) {                            // 只有点确定后才会回调该方法
		// location.href = operationUrl;     // 操作是一个 GET 链接请求，并非 ajax
		// 替换上面的 location.href 操作，改造成 ajax 请求。后端用 renderJson 更方便，不需要知道 redirect 到哪里
		ajaxAction(operationUrl);
		layer.close(index);                           // 需要调用 layer.close(index) 才能关闭对话框
	});
}

/**
 * ajax 做通用的操作，不传递表单数据，仅传id值的那种
 */
function ajaxAction(url) {
	$.ajax(url, {
		type: "GET"
		, cache: false
		, dataType: "json"
		// , data: {	}
		, beforeSend: function() {}
		, error: function(ret) {alert(ret);}
		, success: function(ret) {
			if (ret.success||ret.isOk) {
				showAjaxActionMsg(0, ret.msg);
			} else {
				showAjaxActionMsg(6, ret.msg);
			}
		}
	});
}

/**
 * ajax 做通用的操作，不刷新页面
 */
function ajaxActionStatic(url) {
	$.ajax(url, {
		type: "GET"
		, cache: false
		, dataType: "json"
		// , data: {	}
		, beforeSend: function() {}
		, error: function(ret) {alert(ret);}
		, success: function(ret) {
			showAjaxActionMsg(6, ret.msg);
		}
	});
}

//提交Form表单
function ajaxForm(formId,succUrl){
	$("#"+formId).ajaxForm({
		dataType: "json"
		, beforeSubmit: function(formData, jqForm, options) {
			//ue.sync();  // 同步一下 ueditor 中的数据到表单域
		}
		, success: function(ret) {
			if (ret.success) {
				location.href = succUrl;
			} else {
				showErrorMsg(ret.msg);
			}
		}
		, error: function(ret) {
			alert(ret);
		}
		, complete: function(ret) { }       // 无论是 success 还是 error，最终都会被回调
	});
}
/**
 * Ajax提交
 * @param formId
 * @param succUrl
 */
function ajaxPost(data,url){
	$.ajax(url, {
		type: "POST"
		, dataType: "json"
		, data: data
		, beforeSend: function() {}
		, error: function(ret) {
			showErrorMsg(ret);
		}
		, success: function(ret) {
			if (ret.success) {
				showAjaxActionMsg(0, ret.msg);
			} else {
				showAjaxActionMsg(6, ret.msg);
			}
		}
	});
}
/**
 * 弹出窗口 成功刷新
 * @param shift
 * @param msg
 */
function showAjaxActionMsg(shift, msg) {
	layer.msg(msg, {
			shift: shift
			, shade: 0.4
			, time: 0
			, offset: "140px"
			, closeBtn: 1
			, shadeClose: true
			,maxWidth: "1000"
		}, function () {
			if (shift != 6) {
				location.reload();
			}
		}
	);
}
/**
 * 弹出窗提示信息 
 * @param msg
 */
function showErrorMsg(msg) {
	layer.msg(msg, {
			shift: 6
			, shade: 0.4
			, time: 0
			, offset: "140px"
			, closeBtn: 1
			, shadeClose: true
			,maxWidth: "1000"
		}, function () {}
	);
}
/**
 * 弹窗提示 自动隐藏
 * @param msg
 * @param time
 */
function showMsg(msg,time) {
	layer.msg(msg, {
			shift: 6
			, shade: 0.4
			, time: time
			, offset: "140px"
			, closeBtn: 1
			, shadeClose: true
			,maxWidth: "1000"
		}, function () {}
	);
}