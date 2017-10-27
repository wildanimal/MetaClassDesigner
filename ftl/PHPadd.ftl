<?php
include_once '../../common/common_include.php';

<#assign i=0>
$sql = 'insert into ${entity.tableName}(
<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		<#if i==0><#assign i=1><#else>,</#if> ${field.name}
	</#if>
</#list>
<#list entity.many2ones as route>
		, ${route.name}
</#list>) values (<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		<#if i==1><#assign i=2><#else>,</#if> :${field.name}
	</#if>
</#list>
<#list entity.many2ones as route>
		, :${route.name}
</#list>)';

$result = null;
try { 
	$db->beginTransaction(); 
	$stmt = $db->prepare($sql);

<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
	$stmt->bindParam(":${field.name}", isset($_REQUEST['${field.name}']) ? $_REQUEST['${field.name}'] : null);
	</#if>
</#list>
<#list entity.many2ones as route>
	$stmt->bindParam(":${route.name}", isset($_REQUEST['${route.name}']) && $_REQUEST['${route.name}'] != '' ? $_REQUEST['${route.name}'] : null);
</#list>

	$result = common_commit($stmt);
} catch(PDOExecption $e) { 
	$db->rollback(); 
	header('Status: 600');
    echo '{"code" : 600, "msg" : $e->getMessage()}';
    exit();
}

$json = json_encode($result);

echo $json;
?>