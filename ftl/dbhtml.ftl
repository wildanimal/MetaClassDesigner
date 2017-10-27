<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${entity.label!entity.name}</title>
<style>
.db_def_head td {
	background-color : lightblue;
	font-weight : bold;
}
</style>
</head>
<body>
<div>${entity.label}
包名:${entity.pkgName}
类名:${entity.name}
库表名:${entity.tableName!entity.name}</div>
<table border=1>
	<thead>
		<tr class="db_def_head">
			<td>标题</td>
			<td>字段名</td>
			<td>类型</td>
			<td>是否必填</td>
			<td>长度</td>
			<td>小数点位数</td>
			<td>主键/外键</td>
			<td>备注</td>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>序号</td>
			<td>id</td>
			<td>BIGINT</td>
			<td>是</td>
			<td>10</td>
			<td>&nbsp;</td>
			<td>主键</td>
			<td>&nbsp;</td>
		</tr>
<#list entity.fields as field><tr>
	<td>${field.label!"&nbsp;"}</td>
	<td>${field.columnName!field.name}</td>
	<td>${field.type!"&nbsp;"}</td>
	<td><#if Exp.isTrue(field.required)> 是 <#else> 否 </#if></td>
	<td>${field.length!""}&nbsp;</td>
	<td>${field.scale!""}&nbsp;</td>
	<td>&nbsp;</td>
	<td>${field.memo!""}&nbsp;</td>
</tr></#list>

<#list entity.many2ones as field><tr>
	<td>${field.label!"&nbsp;"}</td>
	<td>${field.columnName!field.name}</td>
	<td>BIGINT</td>
	<td><#if Exp.isTrue(field.required)> 是 <#else> 否 </#if></td>
	<td>${field.length!""}&nbsp;</td>
	<td>${field.scale!""}&nbsp;</td>
	<td>${field.toTypeName}&nbsp;</td>
	<td>${field.memo!""}&nbsp;</td>
</tr>
</#list>
	</tbody>
</table>
</body>
</html>
