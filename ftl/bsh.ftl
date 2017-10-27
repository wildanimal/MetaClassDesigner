import java.util.*;
import util.*;

boolean onShow() {
	boolean result = true;
<#if Exp.isTrue(workflow)>
	result = service.wf.WfBaseService.instance.onShow();
<#elseif Exp.isTrue(entity.uuid)>
	result = service.core.BaseService.instance.uuid();
</#if>
	return result;
}

boolean ok() {
	return ORMgr.saveOrUpdate(model, bean);
}

<#if Exp.isTrue(workflow)>
boolean next() {
	return service.wf.WfBaseService.instance.next();
}

boolean transmit() {
	return ORMgr.saveOrUpdate(model, bean);
}

boolean review() {
	return ORMgr.saveOrUpdate(model, bean);
}
</#if>

boolean deletes(${entity.idtype}[] ${entity.idname}) {
	return ORMgr.deletes(model, ${entity.idname}) > 0;
}