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
			, "fromClause" : " from ${entity.tableName} t<#assign oclass = entity><#list 0..5 as loopIdx>
<#if Exp.isObjectNull(oclass.superClass)> <#break> <#else><#assign oclass=oclass.superClass>, ${oclass.tableName}
</#if></#list> where 1=1 <#assign oclass = entity><#list 0..5 as loopIdx><#if Exp.isObjectNull(oclass.superClass)> <#break> <#else><#assign oclass=oclass.superClass>  and t.id = ${oclass.tableName}.id
</#if></#list> and wf_process.state is null"
			, "area" : "input"
			, "popup" : "${entity.pkg}.${entity.name}?id={$id}<#if Exp.isTrue(workflow)>&pdefid=${entity.name}&adefid=Apply</#if>"
			, "columns" : [
				{
					"name" : "id"
					, "type" : "CheckBox"
					, "expression" : "t.id"
					, "label" : ""
					, "width" : "10%"
				}
<#assign oclass = entity>
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
			]
			, "children" : [] 
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