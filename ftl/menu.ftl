[
	{"id" : "home", "label" : "首页", "depth": 1, "script" : "common_menu.go_to_home()"}
<#list pkg.classes as oclass>
	<#if !oclass.outside>
	, {"id" : "${oclass.name}", "label" : "${oclass.label}", "depth": 2}
	</#if>
</#list>
	, {"id" : "logout", "label" : "退出", "depth": 0, "script" : "common_menu.logout()"}
]