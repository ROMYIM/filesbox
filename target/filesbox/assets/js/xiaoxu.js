  /**
   * autoload select zujian
   */
var AutoSelectUtil={
		fillIt:function(select,datas){
			AutoSelectUtil.clear(select);
			if(datas&&datas.length>0){
				for(var i in datas){
					select.append('<option value="'+datas[i].optionValue+'">'+datas[i].optionText+'</option>');
				}
				select.trigger("change");
			}
		},
		clear:function(select){
			select.empty();
			var text=select.data("text");
			var value=select.data("value");
			if(text){
				select.append('<option value="'+value+'">'+text+'</option>');
			}
		},
		bindLinkAgeEvent:function(select){
			var linkage=select.data("linkage");
			if(linkage){
				var sonSelect=$("#"+linkage);
				var url=sonSelect.data("url");//area/son/pid
				var pid=select.val();
				if(pid&&pid!="0"&&pid!="undefined"){
					AutoSelectUtil.load(sonSelect,url+"/"+pid);
				}
				select.change(function(){
					pid=select.val();
					AutoSelectUtil.load(sonSelect,url+"/"+pid);
				});
			}
		},
		load:function(select,url){
			if(!url){
				url=select.data("url");
			}
			$.ajax({
				type:"get",
				dateType:"json",
				url:url,
				success:function(data){
					if(data.success){
						AutoSelectUtil.fillIt(select,data.result);
					}
				}
			})
		},
		autoload:function(){
			$(".autoloadSelect").each(function(){
				AutoSelectUtil.load($(this)); 
			});
			$("[data-linkage]").each(function(){
				AutoSelectUtil.bindLinkAgeEvent($(this)); 
			});
		}
}


$(function(){
	AutoSelectUtil.autoload();
});

//弹出层
var  MsgBox={
		close:function(){
		$(".layer").fadeOut(function(){
			$(this).remove();
		});	
		},
		create:function(msg,time,callback){
			if(!msg){
				msg="消息";
			}
			var box='<div class="layer"><div class="layer-msg">'+msg+'</div><div class="layer-img"><img src="assets/css/imgs/waitting.gif"/></div></div>';
			$("body").append(box);
			if(time){
				setTimeout(function(){
					MsgBox.close();
					if(callback){
						callback();
					}
				}, time);
			}
		},loading:function(msg){
			MsgBox.create(msg?msg:"处理中",3000);
		},success:function(msg){
			MsgBox.create(msg?msg:"操作成功",3000);
		},error:function(msg){
			MsgBox.create(msg?msg:"失败",3000);
		}
}


/**
 * 弹出alert封装
 */
var ModelUtil = {
	msg:function(title,msg,callback){
		var modelId = "mdoel_"+new Date().getTime();
		var title = title;
		if(!title){
			title = "提示信息";
		}
		if(!msg){
			msg = "操作成功!";
		}
		var mod ='<div class="modal msgModel fade" id="'+modelId+'"><div class="modal-dialog"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">'+title+'</h5></div><div class="modal-body">'+msg+'</div><div class="modal-footer"><button type="button" id="model-close" class="btn btn-default">关闭</button><button type="button" id="model-confirm" class="btn btn-success ">确认</button></div></div></div></div>';
		$("body").append(mod);
		$("#"+modelId).modal('show');
		if(callback){
			$("#model-close").click(function(){ModelUtil.close(modelId)});
			$("#model-confirm").click(function(){ModelUtil.confirm(modelId,callback)});
		}else{
			$("#model-close,#model-confirm").click(function(){ModelUtil.close(modelId)});
		}
	},close:function(modelId){
		$("#"+modelId).modal("hide");
		$(".modal-backdrop").removeClass();
		$("#"+modelId).remove();
	},confirm:function(modelId,callback){
		ModelUtil.close(modelId);
		if(callback){
			callback();
		}
	}
	
}




