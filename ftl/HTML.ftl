<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${entity.label!""}</title>
<!--#include virtual="/common/common_head.html"-->
<script>
var form_params = {
	sortname : '${entity.idname}', // 默认排序列名
	//数据表格的列头名称
	grid_column_names : [
		"id", "${entity.idname}"
<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		, "${field.label!""}"
	</#if>
</#list>
<#list entity.many2ones as route>
		, "${route.name}"
</#list>
	],
	//数据表格的列定义
	grid_column_models : [
		{name : "id", hidden : true, sortable : false}
		, {name : "${entity.idname}", hidden : true}
<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		, {name : "${field.name}"}
	</#if>
</#list>
<#list entity.many2ones as route>
		, {name : "${route.name}", hidden : true}
</#list>
	],
	// 表单的校验规则
	form_validator_rules : {}
};

$(document).ready(function() {
	//初始化菜单
	common_menu.init('${entity.name}', 2);
	
	//$('#menu_${entity.name}').removeClass('ui-state-default').addClass('ui-state-active');

	common_form.init(form_params);
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
<table><tr><td>
<div id=MainContent>
	<!--搜索条件设定-->
	<!--
	<div class="box">
	<span id=SearchBar>
		<label for="q_username">用户名</label> <input name="q_username" id="q_username">
	</span>
		<input type=button id="searchBtn" name="searchBtn" value=查找>
	</div>-->
	<!--新建按钮-->
	<input type=button name="newBtn" id="newBtn" value=新建><br/>
	<!--数据表格容器-->
	<div id=MainGridBox>
		<!--表格翻页控制-->
		<div id=pager></div>
		<!--数据表格-->
		<table id=MainGrid></table>
	</div>
</div>
</td></tr></table>
<!--表单窗口容器-->
<div id=MainPanel class="SPanel">
<!--表单-->
<Form id="MainForm" method="POST" action="">
	<div class="form_title ui-widget-header 
			ui-corner-all 
			ui-helper-clearfix">${entity.label!""}</div>
	
	<!--表单主体-->
	<table border=0>
<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
	<tr>
		<td><label for=name class="<#if Exp.isTrue(field.required)>required</#if>">
			<#if Exp.isTrue(field.required)>*</#if>${field.label!""}：</label></td>
			<td>
				<#switch field.uitype>
					<#default>
				<input name="${field.name}" id="${field.name}">
				</#switch>
			</td>
	</tr>
	</#if>
</#list>
	</table>
	<!--表单按钮容器-->
	<div class="form_button_bar">
		&nbsp;
		<!--用户登录令牌值--><input name="login_token" id="login_token" type=hidden>
		<!--主键控件--><input name="id" id="id" type=hidden>
		<input name="${entity.idname}" id="${entity.idname}" type=hidden>
		<!--外键控件-->
<#list entity.many2ones as route>
	<input name="${route.name}" id="${route.name}" type=hidden>
</#list>
			<!--保存按钮-->
			<input type=submit name=okBtn id=okBtn value=确定>
			<!--取消按钮-->
			<input type=button name=cancelBtn id=cancelBtn value=取消>
	</div>
</Form>
</div>
<!--#include virtual="/common/common_body_footer.html"-->
</html>