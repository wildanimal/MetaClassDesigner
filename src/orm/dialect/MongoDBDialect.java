package orm.dialect;


public class MongoDBDialect extends Dialect {
	public String limit(String sql, int firstResult, int maxResults) {
		return sql + " limit " + firstResult + " , " + maxResults;
	}
}
