<?php
include_once '../../common/common_include.php';

$sql = 'delete from ${entity.tableName} 
where ${entity.idname} = :${entity.idname}';

try { 
	$db->beginTransaction(); 
	$stmt = $db->prepare($sql);

	$stmt->bindParam(":${entity.idname}", $_REQUEST['id']);

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