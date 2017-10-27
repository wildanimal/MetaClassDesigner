package orm.dialect;


public class H2Dialect extends Dialect {
	
	public H2Dialect() {
		dbtypes.put("Boolean", "TINYINT");
		dbtypes.put("Date", "DATE");
		dbtypes.put("DateTime", "TIMESTAMP");
		dbtypes.put("Clob", "CLOB");
		
		alterKeyword = " alter ";
		
		boolType = "TINYINT";
	}
	
	public String limit(String sql, int firstResult, int maxResults) {
		return sql + " limit " + firstResult + " , " + maxResults;
	}
}
