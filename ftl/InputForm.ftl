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
	]
	, "children" : [
		{
			"name" : "${entity.idname}"
			, "type" : "Hidden"
			, "label" : "序号"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}
		<#if Exp.isTrue(entity.uuid)>
		,  {
			"name" : "uuid"
			, "type" : "Hidden"
			, "label" : "标识符"
			, "area" : "input"
			, "showLabel" : true
			, "size" : "small"
		}
		</#if>
<#assign oclass = entity>
<#list 0..5 as loopIdx>
<#list oclass.many2ones as route>
	<#if !Exp.isTrue(route.notcolumn)>
 		, {
			"name" : "${route.name!""}"
			, "type" : "Hidden"
			, "label" : "${route.label!""}"
			, "area" : "input"
		}
	</#if>
</#list>
<#if Exp.isObjectNull(oclass.superClass)>
	<#break>
</#if>
<#assign oclass=oclass.superClass>
</#list>
<#assign oclass = entity>
<#list 0..5 as loopIdx>
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
		<#if Exp.isNull(field.uitype)>
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
		<#else>
			, "type" : "${field.uitype}"
			<#switch field.uitype>
		<#case "ListBox">
		<#case "ComboBox">
			, "datasource" : "${field.datasource?replace("\"", "\\\"")}"
			, "sourcetype" : "${field.sourcetype}"
			<#break>
		<#case "Ref">
			<#assign route = ORMUtil.getManyToOne(entity, field.refname)>
			, "type" : "TextBox"
			, "required" : "${(Exp.isTrue(route.required))?string("true", "false")}"
			, "html" : "readonly='true'"
			, "popup" : "${route.toTypeName}List?binding={\"id\":\"${route.name!""}\", \"label\":\"${field.name!""}\"
			, "popupTarget" : "请选择"
			, "popupParam" : "width=400,height=300,scrolling=yes,resizable=yes"
			<#break>
		</#switch>
		</#if>
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
</#list>
<#if Exp.isObjectNull(oclass.superClass)>
	<#break>
</#if>
<#assign oclass=oclass.superClass>
</#list>
		, {
			"name" : "ok"
			, "label" : "确定"
			, "type" : "Ok"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		} , {
			"name" : "cancel"
			, "label" : "取消"
			, "type" : "Cancel"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}
	]
}