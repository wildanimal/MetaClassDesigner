<#list pkg.classes as oclass>
	<#if !oclass.outside>
create table "${oclass.tableName}"
( ${oclass.idname} <#switch oclass.idtype>
		<#case "Long">BIGINT<#break>
		<#case "String">VARCHAR<#break>
		<#default>
</#switch>	not null	<#if oclass.idgentype == "auto">auto_increment</#if>
<#list oclass.fields as field>
	${field.columnName!field.name} <#if !Exp.isNull(field.length)>, length=${field.length}</#if>

	/** ${field.label!""} */
	<#if Exp.isTrue(field.required)> not null </#if>
	public ${ORMUtil.getTypeName(field.type)} ${field.name};
</#list>

<#list oclass.many2ones as route>
	/** ${route.label!""} */
	@ManyToOne(cascade = {})
	@JoinColumn(name = "${route.columnName!field.name}")
	public ${route.toTypeName} ${route.name};
</#list>
)


<#list oclass.one2manys as route>
	<#if !Exp.isTrue(route.toClass.nottable)>
	/** ${route.label!""} */
	@Transient
	public ArrayList<${route.toTypeName}> ${route.name};
	</#if>
</#list>
	</#if>
</#list>
