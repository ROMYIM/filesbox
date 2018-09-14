#@layout()
#define main()
#@adminMenu()
<div class="right">
	<div class="table_fiter">
		<button id="search" class="btn btn-primary" onclick="showData()">保存</button>
	</div>
	<div class="table">
		<table id="advTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th>排序</th>
					<th>图片</th>
					<th>运营商</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<!-- 加载编辑器的容器 -->
				<script id="container" name="content" type="text/plain">
        这里写你的初始化内容
    </script>
				<!-- 配置文件 -->
				<script type="text/javascript" src="./ueditor/ueditor.config.js"></script>
				<!-- 编辑器源码文件 -->
				<script type="text/javascript" src="./ueditor/ueditor.all.js"></script>
				<!-- 手动加载语言 -->
				<script type="text/javascript" src="./ueditor/lang/zh-cn/zh-cn.js"></script>

				<!-- 实例化编辑器 -->
				<script type="text/javascript">
					var ue = UE.getEditor('container');
				</script>
			</tbody>
		</table>
	</div>
</div>
#end 
#define js()
	<script src="assets/js/jquery.form.min.js"></script>
	<script type="text/javascript">
	function showData() {
		var test = UE.getEditor('container').body.innerHTML;
		alert(test);
	}
	</script>
#end
