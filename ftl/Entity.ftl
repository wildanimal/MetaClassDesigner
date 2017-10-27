package ${entity.pkg};


import java.io.Serializable;

import javax.persistence.*;
import java.util.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * ${entity.label}
 */
<#if !Exp.isTrue(entity.nottable)>
@Table(name = "${entity.tableName}")
@Entity
<#else>
@MappedSuperclass
</#if>
public class ${entity.name} ${ORMUtil.getExtendString(entity)} {
	<#if !ORMUtil.isJoinedSubClass(entity) && !Exp.isTrue(entity.nottable)>/** 序号 */
	@Id
	@Column(name="${entity.idcolumn!entity.idname}")
<#if entity.idgentype == 'auto'>
	@GeneratedValue(strategy = GenerationType.AUTO)
<#elseif entity.idgentype == 'assigned'>
    @GenericGenerator(name="assigned", strategy="assigned")  
	@GeneratedValue(generator="assigned")
<#elseif entity.idgentype == 'sequence'>
		@SequenceGenerator(name="seq"
		, sequenceName="${entity.idgen}")
		@GeneratedValue(strategy=GenerationType.SEQUENCE
		,generator="seq")
<#elseif entity.idgentype == 'generator'>
@GeneratedValue(generator="gen")
	@GenericGenerator(name = "gen", strategy = "${entity.idgen}")
		,generator="seq")
<#elseif entity.idgentype == 'uuid'>
	@GeneratedValue(generator="uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
</#if>
	public ${entity.idtype} ${entity.idname};
	</#if>
	
<#list entity.fields as field>

	/** ${field.label!""} */
	<#if Exp.isTrue(field.notcolumn)>@Transient<#else>
	@Column(name="${field.columnName!field.name}"<#if !Exp.isNull(field.length)>, length=${field.length}</#if>)</#if>
	<#if Exp.isTrue(field.required)>@NotNull</#if>
	public ${ORMUtil.getTypeName(field.type)} ${field.name};
</#list>

<#list entity.many2ones as route>
	/** ${route.label!""} */
	<#if Exp.isTrue(route.notcolumn)>@Transient<#else>
	@Column(name = "${route.columnName!field.name}")</#if>
	<#if Exp.isTrue(route.required)>@NotNull</#if>
	public Long ${route.name};
</#list>

<#list entity.one2manys as route>
	<#if !Exp.isTrue(route.toClass.nottable)>
	/** ${route.label!""} */
	@Transient
	public ArrayList<${route.toTypeName}> ${route.name};
	</#if>
</#list>
}