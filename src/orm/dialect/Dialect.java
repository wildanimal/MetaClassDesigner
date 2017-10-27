package orm.dialect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import orm.Work;
import orm.work.DefaultSchemaWork;
import util.Consts;
import util.Exp;
import util.MetaMap;
import util.Reflects;

/**
 * 根数据库方言.
 * <pre>定义数据库方言基本行为.</pre>
 * @author chenmin
 *
 */
@SuppressWarnings("unchecked")
public class Dialect {
	public static Map<String, String> dbtypes = new HashMap<String, String>();
	public static List<String> lengthTypes = new ArrayList<String>();
	
	public String alterKeyword = " modify ";
	public String idKeyword = " not null ";
	public String boolType = "BIT";
	
	static {
		dbtypes.put("String", "VARCHAR");
		dbtypes.put("Long", "BIGINT");
		dbtypes.put("Integer", "INTEGER");
		dbtypes.put("Double", "DOUBLE");
		dbtypes.put("Float", "FLOAT"); // float
		dbtypes.put("Date", "DATETIME");
		dbtypes.put("DateTime", "DATETIME");
		dbtypes.put("Boolean", "BIT");
		dbtypes.put("Clob", "TEXT");
		
		lengthTypes.add("String");
		lengthTypes.add("Double");
		lengthTypes.add("Float");
	}
	
	public Dialect() {
	}
	
	public static String dbtype(String key) {
		return dbtypes.get(key);
	}
	
	public Work createSchemaWork(MetaMap pkg) {
		return new DefaultSchemaWork(this, pkg);
	}
	
	public int insert(MetaMap model) throws Exception {
		return 0;
	}
	
	public Serializable generateId(MetaMap model, MetaMap data) {
		String idgentype = model.str("idgentype");
		Serializable id = null;
		if (idgentype.equals("uuid")) {
			id = UUID.randomUUID().toString().replaceAll("\\-", "");
		} else if (idgentype.equals("assigned")) {
			id = !Exp.isNull(data.get("id")) ? 
				data.id() : null;
		} else if (idgentype.equals("generator")) {
			String key = model.str("idgenkey");
			try {
				Class clazz = Reflects.classForName(key);
				id = (Serializable)Reflects.invoke(
					clazz, null, "execute", null, null);
			} catch (Exception e) {
				Consts.log.error("库表 " + model.get("name") 
					+ "(" + model.get("label") 
					+ model.get("tableName") + ")生成id错误", e);
			}
		}
			
		return id;
	}
	
	public boolean needInsertId(MetaMap model) {
		if (model.get("superClass") != null
			|| (model.get("idgentype") != null 
				&& !(model.get("idgentype").equals("auto")
				|| model.get("idgentype").equals("sequence")) ) )
		return true;
		
		return false;
	}
	
	public String getPrimaryKeyClause(MetaMap model) {
		return " primary key ("+model.get("idname")+") ";
	}
	
	public String getIdCreateClause(MetaMap model) {
		String result = idKeyword;
		String idgentype = model.str("idgentype");
		if (idgentype.equals("auto") || idgentype.equals("sequence")) {
			result += " auto_increment ";
		}
		return result;
	}
	
	/**
	 * 分页
	 * @param sql
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public String limit(String sql, int firstResult, int maxResults) {
		return sql;
	}
	
	public static Dialect create(String name) {
		name = name.toLowerCase();
		if (name.contains("mysql")) {
			return new MySqlDialect();
		} else if (name.contains("sqlserver")) {
			return new MSSqlDialect();
		} else if (name.contains("oracle")) {
			return new OracleDialect();
		} else if (name.contains("postgresql")) {
			return new PostgreSqlDialect();
		} else if (name.contains("h2")) {
			return new H2Dialect();
		}
		
		return null;
	}
}
