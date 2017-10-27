create table ${entity.tableName!entity.name} (
	${entity.idname} ${endity.idtype}  not null auto_increment
<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		, ${field.columnName} 
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