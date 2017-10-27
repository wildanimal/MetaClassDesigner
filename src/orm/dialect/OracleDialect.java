package orm.dialect;

import java.io.Serializable;

import util.MetaMap;

public class OracleDialect extends Dialect {
	
	public OracleDialect() {
		
		idKeyword = " primary key ";
	}
	
	public String getPrimaryKeyClause(MetaMap model) {
		return " constraint pk_" + model.get("tableName")
		+ " primary key(id) using index ";
	}
	
	public String getIdCreateClause(MetaMap model) {
		return idKeyword;
	}
	
	public Serializable generateId(MetaMap model, MetaMap data) {
		Serializable id = super.generateId(model, data);
		String idgentype = model.str("idgentype");
		if (idgentype.equals("auto")) {
			// TODO 使用和表名相同的序列号
		} else if (idgentype.equals("sequence")) {
			// TODO 使用固定序列号
		}
			
		return id;
	}
	
	public String getIdInsertClause(MetaMap model) {
		return null;
	}
	
	public String limit(String sql, int firstResult, int maxResults) {
		return "select * from  ( select a.*, rownum rn from (" + sql + ") a where rownum <= " 
		+ (firstResult + maxResults) + ") where rn > " + firstResult;
	}
}
