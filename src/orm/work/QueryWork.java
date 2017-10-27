package orm.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.Session;
import orm.Work;
import util.Consts;
import util.Exp;
import util.MetaMap;
/**
 * 数据库查询工作.
 * @author chenmin
 *
 */
@SuppressWarnings("unchecked")
public class QueryWork implements Work {
	public static Log log = LogFactory.getLog(QueryWork.class);
	public String sql = null;
	public List result = new ArrayList();
	
	public int count = 0;
	public boolean query = true;
	public int index = 0;
	
	public Session session = null;

	@Override
	public String getSql() {
		return sql;
	}
	
	
	public Map<Integer, Object> params = new LinkedHashMap<Integer, Object>();
	
	public QueryWork(Session session, String sql) {
		this.session = session;
		this.sql = sql;
	}

	public void execute(Connection conn) throws SQLException {

		ResultSet rs = null;
		PreparedStatement ps = null;
		
//		String lowerSql = sql.toLowerCase();
		try {
//			Dialect dialect = ORMgr.dialects.get(ORMgr.defaultName);
//			if (dialect instanceof MySqlDialect
//				&& (lowerSql.contains("update") 
//					|| lowerSql.contains("insert") 
//					|| lowerSql.contains("delete")) ) {
//				sql = " set SQL_SAFE_UPDATES = 0; " + sql;
//			}
			ps = conn.prepareStatement(sql);
			for (Integer i : params.keySet()) {
				ps.setObject(i+1, params.get(i));
			}
			Consts.log.info(sql);
			if (query) {
				Object val = null;
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				if (rsmd.getColumnCount() > 1) {
					while (rs.next()) {
						MetaMap map = new MetaMap();
						result.add(map);
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							val  = rs.getObject(i);
							if (rsmd.getColumnTypeName(i).equals(
								session.sf.dialect.boolType) 
								&& !(val instanceof Boolean)) {
								val = !Exp.isNull(val);
							}
							map.put(rsmd.getColumnName(i), val);//rsmd.getColumnName(i)
						}
					}
				} else {
					while (rs.next()) {
						result.add(rs.getObject(1));
					}
				}
			} else {
				count = ps.executeUpdate();
			}
//			conn.commit();
		} 
//		catch (SQLException e) {} 
		finally {
			if (rs != null)
				rs.close();
			
			if (ps != null) {
				ps.close();
			}
		}
	}
	
	public void setParameter(String key, Object value) {
		params.put( index++, value);
		sql = sql.replace(":" + key, "?");
	}
	
	public void setParameter(Integer idx, Object value) {
		params.put(idx, value);
		index = idx + 1;
	}
	
	public void setParameterList(String key, Object value) {
		StringBuffer quotions = new StringBuffer();
		if (value instanceof Object[]) {
			for (Object val : (Object[])value) {
				quotions.append("?,");
				params.put( index++, val);
			}
			quotions.deleteCharAt(quotions.length() - 1);
		} else if (value instanceof List) {
			for (Object val : (List)value) {
				quotions.append("?,");
				params.put( index++, val);
			}
			quotions.deleteCharAt(quotions.length() - 1);
		} else {
			quotions.append("?");
			params.put( index++, value);
		}
		sql = sql.replace(":" + key, quotions.toString());
		
	}

}
