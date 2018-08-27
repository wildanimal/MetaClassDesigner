<#list pkg.classes as oclass>
	<#if !oclass.outside>
create table `${oclass.tableName}`
( `${oclass.idname}` <#switch oclass.idtype>
		<#case "Long">BIGINT(20)<#break>
		<#case "String">VARCHAR(200)<#break>
		<#default>
</#switch>	not null	<#if oclass.idgentype == "auto">auto_increment</#if>,
<#list oclass.fields as field>
	`${field.columnName!field.name}` <#switch field.type>
		<#case "Integer">INT(20)<#break>
		<#case "Long">BIGINT(20)<#break>
		<#case "String">VARCHAR(200)<#break>
		</#switch> <#if Exp.isTrue(field.required)> not null </#if>,
</#list>
<#list oclass.many2ones as route>
	`${route.columnName!route.name}` <#switch route.type>
		<#case "Integer">INT(20)<#break>
		<#case "Long">BIGINT(20)<#break>
		<#case "String">VARCHAR(200)<#break>
		</#switch> <#if Exp.isTrue(route.required)> not null </#if>,
</#list>
	primary key (`${oclass.idname}`)
);

	</#if>
</#list>
