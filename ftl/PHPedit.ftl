<?php
$error_msg = '{"result" : "FAIL", "msg" : "$1"}';

include_once '../../common/common_include.php';
require_once '../../common/check_access.php';
check_access('/${entity.dbschema}/${entity.name}');

<#assign i=0>
$sql = 'update ${entity.tableName} set
<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		<#if i==0><#assign i=1><#else>,</#if> ${field.name} = :${field.name}    
	</#if>
</#list>
<#list entity.many2ones as route>
		,  ${route.name} = :${route.name} 
</#list>
where ${entity.idname} = :${entity.idname}';

$result = null;
try { 
	$db->beginTransaction(); 
	$stmt = $db->prepare($sql);

<#list entity.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
	$${field.name} = $_REQUEST['${field.name}'] != null ? $_REQUEST['${field.name}'] : <#if field.type == "String">""<#else>0</#if>;
	$stmt->bindParam(":${field.name}", $${field.name});
	</#if>
</#list>
<#list entity.many2ones as route>
	$${route.name} = $_REQUEST['${route.name}'] != null ? $_REQUEST['${route.name}'] : 0;
	$stmt->bindParam(":${route.name}", $${route.name});
</#list>

	$stmt->bindParam(":${entity.idname}", $_REQUEST['${entity.idname}']);

	$result = common_commit($stmt);
} catch(PDOExecption $e) { 
	$db->rollback(); 
	header('Status: 600');
    echo '{"code" : 600, "msg" : $e->getMessage()}';
    exit();
}

$result['result'] = true;
$result['code'] = 200;

$json = json_encode($result);

echo $json;
?>