{
	"name" : "${entity.name}"
	, "label" : "${entity.label!""}"
	, "entity" : "${entity.pkg}.${entity.name}"
	, "service" : "service.${entity.pkg}.${entity.name}Service"
	, "inputPath" : "/forms/SimpleForm.ftl"
	, "module" : "${entity.pkg}"
	, "type" : "InputForm"
	, "showLabel" : true
	, "objects" : [

<#assign oclass = entity>
<#assign idx=0>
<#list 0..5 as loopIdx>
<#list oclass.many2ones as route>
	<#if !Exp.isTrue(route.notcolumn)>
 		<#if idx &gt; 0>,</#if>{
			"name" : "${route.name}"
			, "label" : "${route.label}"
			, "className" : "${route.toClass.pkg}.${route.toClass.name}"
			, "value" : "${route.name}.id"
		}
		<#assign idx=1>
	</#if>
</#list>
<#if Exp.isObjectNull(oclass.superClass)>
	<#break>
</#if>
<#assign oclass=oclass.superClass>
</#list>
	<#if idx &gt; 0>,</#if>
	{
			"name" : "audit"
			, "label" : "审批单"
			, "className" : "bsn.wf.WfAudit"
			, "value" : "audit.id"
		}
	]
	, "children" : [
		{"labelClass" : "SmallLabel",
			"area" : "query",
			"mx_internal_uid" : "A3D24D8D-3EF4-60AA-36EE-85FA31B5FCA7",
			"label" : "标识符",
			"showLabel" : true,
			"name" : "uuid",
			"column" : "uuid",
			"operate" : "=",
			"objects" : 
			[],
			"cssClass" : "",
			"type" : "Hidden",
			"columnType" : "String",
			"boxClass" : "Hide",
			"size" : "small",
			"img" : "icon/Hidden.png"
		}, {
			"name" : "redirect"
			, "type" : "Hidden"
			, "label" : "重定向"
			, "area" : "command"
			, "showLabel" : true
			, "size" : "small"
			, "value" : "${entity.pkg}.${entity.name}List"
		}, {
			"name" : "id"
			, "type" : "Hidden"
			, "label" : "序号"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}, {
			"name" : "procid"
			, "type" : "Hidden"
			, "label" : "流程序号"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}, {
			"name" : "actid"
			, "type" : "Hidden"
			, "label" : "活动序号"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}, {
			"name" : "reviewid"
			, "type" : "Hidden"
			, "label" : "阅知序号"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}, {
			"name" : "adefid"
			, "type" : "Hidden"
			, "label" : "活动定义序号"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}, {
			"name" : "updateVarNames"
			, "type" : "Hidden"
			, "label" : "活动提交需要的变量"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}, {
			"name" : "result"
			, "type" : "Hidden"
			, "label" : "审批结果"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
			, "value" : true
		}
<#list wfprocess.fields as field>
	<#if !Exp.isTrue(field.notcolumn) 
		&& !Exp.eq(field.name, "uuid")
		&& !Exp.eq(field.name, "name")
		&& !Exp.eq(field.name, "code")>
		, {
			"name" : "${field.name}"
			, "label" : "${field.label}"
			, "showLabel" : true
			, "area" : "input"
			, "size" : "small"
			, "required" : ${(Exp.isTrue(field.required))?string("true",  "false")}
			, "type" : "Hidden"
		}
	</#if>
</#list>, {
			"name" : "name"
			, "type" : "TextBox"
			, "label" : "名称"
			, "area" : "input"
			, "showLabel" : true
			, "boxClass" : "SmallBox"
			, "labelClass" : "SmallLabel"
			, "cssClass" : "SmallWidget"
			, "size" : "small"
		}, {
			"name" : "code"
			, "type" : "Text"
			, "label" : "编码"
			, "area" : "input"
			, "showLabel" : true
			, "boxClass" : "SmallBox"
			, "labelClass" : "SmallLabel"
			, "cssClass" : "SmallWidget"
			, "size" : "small"
		}
<#assign oclass = entity>
<#list oclass.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		, {
			"name" : "${field.name}"
			, "label" : "${field.label}"
			, "boxClass" : "SmallBox"
			, "labelClass" : "SmallLabel"
			, "cssClass" : "SmallWidget"
			, "showLabel" : true
			, "area" : "input"
			, "size" : "small"
			, "required" : ${(Exp.isTrue(field.required))?string("true",  "false")}
			<#switch field.type>
		<#case "Integer">
		<#case "Long">
			, "type" : "Number"
			<#break>
		<#case "String">
			, "type" : "TextBox"
			<#break>
		<#case "Date">
			, "type" : "FDate"
			<#break>
		<#case "DateTime">
			, "type" : "FDateTime"
			<#break>
		<#case "Float">
			, "type" : "FFloat"
			<#break>
		<#case "Boolean">
			, "type" : "CheckBox"
			, "datasource" : "[{\"data\" : \"true\", \"label\":\"\"}]"
			, "sourcetype" : "json"
			<#break>
		<#case "Clob">
			, "type" : "TextArea"
			<#break>
	</#switch>
		}
	</#if>
</#list>

<#list oclass.many2ones as route>
	<#if !Exp.isTrue(route.notcolumn)>
 		, {
			"name" : "${route.name}.id"
			, "label" : "${route.label}"
			, "type" : "Hidden"
			, "area" : "input"
			, "size" : "small"
			, "required" : "${(Exp.isTrue(route.required))?string("true", "false")}"
		}, {
			"name" : "${route.name}.name"
			, "label" : "${route.label}"
			, "boxClass" : "SmallBox"
			, "cssClass" : "SmallWidget"
			, "labelClass" : "SmallLabel"
			, "showLabel" : true
			, "type" : "TextBox"
			, "area" : "input"
			, "size" : "small"
			, "required" : "${(Exp.isTrue(route.required))?string("true", "false")}"
			, "html" : "readonly='true'"
			, "popup" : "${route.toTypeName}List?binding={\"id\":\"${route.name}.id\", \"label\":\"${route.name}.label\"}"
			, "popupTarget" : "请选择"
			, "popupParam" : "width=400,height=300,scrolling=yes,resizable=yes"
		}
	</#if>
</#list>

<#list oclass.one2manys as route>
	<#if !Exp.isTrue(route.toClass.nottable) && Exp.isTrue(route.bind)>
		, {
			"name" : "${route.name}Grid"
			, "label" : "${route.label}"
			, "type" : "SGrid"
			, "area" : "input"
			, "fromClause" : "from ${route.toClass.tableName} where uuid = '{$uuid}'"
			, "popup" : "${route.toTypeName}?id={$id}"
			, "sourcetype" : "sql"
			, "children" : [{
					"name" : "add_${route.name}"
					, "label" : "添加"
					, "type" : "Button"
					, "area" : "head"
					, "boxClass" : "ButtonBox"
					, "cssClass" : "Button"
					, "popup" : "${route.toClass.pkg}.${route.toClass.name}?uuid={$uuid}"
					, "popupTarget" : "${route.name}"
					, "popupParam" : "width=600,height=400,scrolling=yes,resizable=yes"
					, "popupOwner" : "${route.name}Grid"
				} , {
					"name" : "del_${route.name}"
					, "label" : "删除"
					, "type" : "Delete"
					, "target" : "${route.name}Grid"
					, "form" : "${route.toTypeName}"
					, "area" : "head"
					, "boxClass" : "ButtonBox"
					, "cssClass" : "Button"
				}]
			, "columns" : [
				{
					"name" : "id"
					, "type" : "CheckBox"
					, "expression" : "id"
					, "label" : ""
					, "width" : "10%"
				}
			<#list route.toClass.fields as rfield>
				, {
					"name" : "${rfield.name}"
					, "label" : "${rfield.label}"
					, "expression" : "${rfield.columnName}"
					, "width" : "10%"
					<#if rfield.type == "Boolean">
					, "type" : "${rfield.uitype!""}"
					<#else>
					, "type" : "${rfield.uitype!""}"
					</#if>
				}
			</#list>
			] 
		}
	</#if>
</#list>, {
			"name" : "memo"
			, "type" : "TextArea"
			, "label" : "备注"
			, "area" : "input"
			, "showLabel" : true
			, "boxClass" : "MiddleBox"
			, "labelClass" : "MiddleLabel"
			, "cssClass" : "MiddleWidget"
			, "size" : "middle"
		}
		, {
			"name" : "AuditGrid"
			, "label" : "经办记录"
			, "type" : "SGrid"
			, "fromClause" : " from wf_audit a
, wf_process p
, wf_activity act
where a.actid = act.id
and a.procid = p.id
and act.procid = p.id "
			, "area" : "input"
			, "popup" : ""
			, "columns" : [
				{
					"name" : "id"
					, "type" : "hidden"
					, "expression" : "a.id"
					, "label" : ""
					, "width" : "10%"
				}
				, {"expression" : "act.name",
					"name" : "name",
					"label" : "名称",
					"width" : "30%"},
				
				{"expression" : "a.result",
					"name" : "result",
					"type" : "bool",
					"label" : "结果",
					"width" : "100"},
				
				{"expression" : "a.creatorName",
					"name" : "creatorName",
					"label" : "处理人",
					"width" : "160"},
				
				{"expression" : "a.createTime",
					"name" : "createTime",
					"label" : "时间",
					"type" : "FDatetime",
					"width" : "200"}
			]
			, "children" : [] 
		} , {"popup" : "",
			"area" : "input",
			"label" : "附件列表",
			"img" : "icon/Grid.png",
			"children" : 
			[],
			"type" : "SGrid",
			"name" : "AttachGrid",
			"objects" : 
			[],
			"sourcetype" : "sql",
			"selectClause" : "",
			"target" : "",
			"fromClause" : "from file_file p where 1=1 ",
			"mx_internal_uid" : "D469CD63-B0F0-444F-DB70-8183D1BDAC5C",
			"columns" : 
			[
				{"expression" : "id",
					"name" : "id",
					"label" : "",
					"mx_internal_uid" : "DC570D65-009D-F316-3650-8183D1CD1107",
					"type" : "CheckBox",
					"width" : "60"},
				
				{"expression" : "ver",
					"name" : "ver",
					"type" : "file_ver",
					"mx_internal_uid" : "C2750D9F-FC41-D803-ACC2-8183D2D75CC2",
					"label" : "版本",
					"width" : "60"},
				
				{"expression" : "name",
					"name" : "name",
					"type" : "text",
					"mx_internal_uid" : "12496F7B-3E9A-124D-6C35-8183D2C7E09F",
					"label" : "名称",
					"width" : "50%"},
				
				{"expression" : "''",
					"name" : "op",
					"label" : "操作",
					"mx_internal_uid" : "DBB41A64-115D-0337-C5F7-86005BD5E584",
					"type" : "file_op",
					"width" : "100"}]}
		, {"children" : [],
			"area" : "input",
			"grid" : "AttachGrid",
			"LabelClass" : "MiddleLabel",
			"label" : "文档",
			"showLabel" : true,
			"mx_internal_uid" : "A63F4585-79EF-9052-70AB-85CF214B9265",
			"name" : "files",
			"objects" : [],
			"cssClass" : "Box",
			"type" : "Doc",
			"labelClass" : "MiddleLabel",
			"boxClass" : "MiddleBox",
			"size" : "middle",
			"img" : "icon/File.png"}
		, {
			"name" : "nextActUsers"
			, "label" : "下步处理人"
			, "showLabel" : true
			, "type" : "Box"
			, "area" : "input"
			, "boxClass" : "MiddleBox"
			, "cssClass" : "MiddleWidget"
			, "labelClass" : "MiddleLabel"
		}, {
			"name" : "nextReviewers"
			, "label" : "下步阅知人"
			, "showLabel" : true
			, "type" : "Box"
			, "area" : "input"
			, "boxClass" : "MiddleBox"
			, "cssClass" : "MiddleWidget"
			, "labelClass" : "MiddleLabel"
		}, {
			"name" : "ok"
			, "label" : "确定"
			, "type" : "Ok"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}, {
			"name" : "next"
			, "label" : "选择处理人"
			, "type" : "WfUser"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}, {
			"name" : "next"
			, "label" : "提交下一步"
			, "type" : "WfNext"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}, {
			"name" : "transmit"
			, "label" : "转发"
			, "type" : "WfTransmit"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}, {
			"name" : "rollback"
			, "label" : "回退"
			, "type" : "Button"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}, {
			"name" : "review"
			, "label" : "阅知"
			, "type" : "Review"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}, {
			"name" : "WfGraph"
			, "label" : "查看流程图"
			, "type" : "WfGraph"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}, {
			"name" : "cancel"
			, "label" : "取消"
			, "type" : "Cancel"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}
	]
}