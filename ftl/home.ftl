<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>首页</title>
<!--#include virtual="/common/common_head.html"-->
<script>
$(document).ready(function() {
	//初始化菜单
	common_menu.init('home', 1);
	
	$('#MaskPanel').SPanel({modal : true});
});
</script>
<style>
/*用这个样式重新定义输入表单的大小
#MainPanel {
	width : 300px;
}*/
</style>
</head>
<body>
<!--#include virtual="/common/common_body_header.html"-->
<!--#include virtual="/common/menu.html"-->
<table><tr><td></td></tr></table>
<!--#include virtual="/common/common_body_footer.html"-->
</html>