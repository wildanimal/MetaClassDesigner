$(document).ready(function() {

$('#${entity.name}').validate({
	rules : {
<#assign oclass = entity>
<#list 0..5 as loopIdx>
<#list oclass.fields as field>
	<#if !Exp.isTrue(field.notcolumn)>
		'${field.name}' : {
	<#if Exp.isTrue(field.required)>
		required: true,
	</#if>
	<#if !Exp.isNull(field.length)>
		byteRangeLength: [0, ${field.length}],
	</#if>
	<#if !Exp.isNull(field.pattern)>
		regexp : ${field.pattern},
	</#if>
	<#switch field.type>
		<#case "FDate">
		dateISO : true,
		<#break>
		<#case "FDateTime">
		datetime : true,
		<#break>
		<#case "Number">
		number : true,
		<#break>
		<#case "FFloat">
		floatnum : true,
		<#break>
	</#switch>
		nothing : true
		},
	</#if>
</#list>
<#if Exp.isObjectNull(oclass.superClass)>
	<#break>
</#if>
<#assign oclass=oclass.superClass>
</#list>
		'nothing': {}
	}
});
});