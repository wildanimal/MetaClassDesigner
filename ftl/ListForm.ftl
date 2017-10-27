{
	"name" : "${entity.name}List"
	, "label" : "${entity.label!""}列表"
	, "entity" : "${entity.pkg}.${entity.name}"
	, "service" : "service.${entity.pkg}.${entity.name}Service"
	, "inputPath" : "/forms/SimpleForm.ftl"
	, "module" : "${entity.pkg}"
	, "type" : "ListForm"
	, "showLabel" : true
	, "children" : [{
			"name" : "${entity.name}Grid"
			, "label" : "${entity.label}列表"
			, "type" : "SGrid"
			, "fromClause" : " from ${entity.tableName} t
<#assign oclass = entity><#list 0..5 as loopIdx>
<#if Exp.isObjectNull(oclass.superClass)> <#break> <#else><#assign oclass=oclass.superClass>, ${oclass.tableName}
</#if>
</#list> where 1=1 <#assign oclass = entity><#list 0..5 as loopIdx>
<#if Exp.isObjectNull(oclass.superClass)> <#break> <#else><#assign oclass=oclass.superClass>  and t.id = ${oclass.tableName}.id
</#if>
</#list>"
			, "area" : "input"
			, "popup" : "${entity.pkg}.${entity.name}?${entity.idname}={$${entity.idname}}"
			, "columns" : [
				{
					"name" : "${entity.idname}"
					, "type" : "CheckBox"
					, "expression" : "t.${entity.idname}"
					, "label" : ""
					, "width" : "10%"
				}
<#assign oclass = entity>
<#list 0..5 as loopIdx>
<#list oclass.many2ones as route>
	<#if !Exp.isTrue(route.notcolumn)>
 		, {
			"name" : "${route.name!""}"
			, "type" : "Hidden"
			, "expression" : "t.${route.name}"
			, "label" : "${route.label!""}"
			, "area" : "input"
		}
	</#if>
</#list>
<#list oclass.fields as field>
	<#if !Exp.eq(field.type, "Clob") 
	&& !Exp.isTrue(field.notcolumn) 
	&& !Exp.eq(field.uitype, "TextArea")>
				, {
					"name" : "${field.name}"
					, "label" : "${field.label}"
					, "expression" : "${field.columnName}"
					, "width" : "10%"
					<#if field.type == "Boolean">
					, "type" : "${field.uitype!""}"
					<#else>
					, "type" : "${field.uitype!""}"
					</#if>
				}
	</#if>
</#list>
<#if Exp.isObjectNull(oclass.superClass)>
	<#break>
<#else>
	<#assign oclass=oclass.superClass>
</#if>
</#list>
			]
			, "children" : [] 
		}, {
			"name" : "add"
			, "label" : "添加"
			, "type" : "Button"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
			, "popup" : "${entity.pkg}.${entity.name}"
			, "popupTarget" : "输入表单"
			, "popupOwner" : "${entity.name}Grid"
		}, {
			"name" : "remove"
			, "label" : "删除"
			, "type" : "Delete"
			, "target" : "${entity.name}Grid"
			, "area" : "command"
			, "boxClass" : "ButtonBox"
			, "cssClass" : "Button"
		}
	]
}