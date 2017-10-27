package orm.dialect;


public class MSSqlDialect extends Dialect {
	
	public MSSqlDialect() {
		dbtypes.put("String", "varchar");
		dbtypes.put("Long", "bigint");
		dbtypes.put("Integer", "integer");
		dbtypes.put("Double", "double");
		dbtypes.put("Float", "float"); // float
		dbtypes.put("Date", "datetime");
		dbtypes.put("DateTime", "datetime");
		dbtypes.put("Boolean", "bit");
		dbtypes.put("Clob", "text");
		
		alterKeyword = " alter column ";
		
		idKeyword = " identity not null ";
	}
}
