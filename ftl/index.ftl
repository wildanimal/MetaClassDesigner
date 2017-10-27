<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>首页</title>
<script src="../js/jquery/jquery.js"></script>
<script src="../js/jquery/jquery.json.js"></script>
<script src="../js/jquery/jquery.cookie.js"></script>

<script>
$(document).ready(function() {
	var login_token = $.cookie('login_token');
	if (login_token) {
		$.ajax({
			url : '../common/check_login_token.php?login_token=' + login_token,
			async : false,
			success : function() {
				self.location.href = 'home.shtml';
			}
		});
	}
		
	$('#okBtn').click(function() {
		$.ajax({
			url : '../common/login.php'
			, type : 'POST'
			, data : {userid : $('#userid').val(), password : $('#password').val()}
			, dataType : 'JSON'
			, success : function(data) {
				$.cookie('login_token', data.login_token, {path : '/'});
				$.cookie('userid', data.userid, {path : '/'});
				$.cookie('username', data.username, {path : '/'});
				self.location.href = 'home.shtml';
			}, error : function() {
				alert('用户名或密码错误');
			}
		});
	});
});
</script>
<style>
/*用这个样式重新定义输入表单的大小
#MainPanel {
	width : 300px;
}*/
#box {
	width: 100%;
 	height: 100%;}

#box_cell {
	width:100%;
	height:100%;

}

.form_title {
	height:30px;
	text-align : center;
}
</style>
</head>
<body>
<table id=box><tr>
<td id=box_cell align=center>
<table>	

	<tr>
		<td colspan=2><div class="form_title ui-widget-header ui-corner-all ui-widget-header">后台管理系统</div></td></tr>
	<tr>
		<td width=80px align=right>用户名</td><td><input name=userid id=userid class=input></td>
	</tr>
	<tr>
		<td align=right>密码</td><td><input name=password id=password type=password class=input></td>
	</tr>
	<tr>
		<td colspan=2 align=center>
		<input type=button id=okBtn value=登录><!--
		<input type=button value=注册>
		<input type=button value=忘记密码? onclick="self.location.href='refind.php'">-->
		</td>
	</tr>
</table>
</div>
</div>
<!--#include virtual="/common/common_body_footer.html"-->
</html>