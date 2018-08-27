<?php
$error_msg = '{"result" : "FAIL", "msg" : "$1"}';

include_once '../../common/common_include.php';
require_once '../../common/check_access.php';
check_access('/${entity.dbschema}/${entity.name}');

$sql = 'delete from ${entity.tableName} 
where ${entity.idname} = :${entity.idname}';

try { 
	$db->beginTransaction(); 
	$stmt = $db->prepare($sql);

	$stmt->bindParam(":${entity.idname}", $_REQUEST['id']);

	$result = common_commit($stmt);
} catch(PDOExecption $e) { 
	$db->rollback(); 
    echo '{"code" : 600, "msg" : "$e->getMessage()"}';
    exit();
}
 
$json = json_encode($result);

echo $json;
?>