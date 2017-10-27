package orm.dialect;


public class MySqlDialect extends Dialect {
	public String limit(String sql, int firstResult, int maxResults) {
		return sql + " limit " + firstResult + " , " + maxResults;
	}
}
