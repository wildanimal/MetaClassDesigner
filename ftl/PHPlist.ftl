<?php
include_once '../../common/common_include.php';

$page = new SQL_Page;

$sql = 'select SQL_CALC_FOUND_ROWS
	${entity.idname} id,
	${entity.idname}
<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		, ${field.name}
	</#if>
</#list>
<#list entity.many2ones as route>
		, ${route.name}
</#list>
from ${entity.tableName}';

$page->buildSql($sql);

$result = $page->query();

$json = json_encode($result);

echo $json;
?>