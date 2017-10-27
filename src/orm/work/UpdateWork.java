package orm.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import orm.Session;
import orm.Work;
import util.Consts;
import util.Exp;
import util.MetaMap;
//@SuppressWarnings("unchecked")
/**
 * 数据库单条记录更新工作.
 * @author chenmin
 *
 */
public class UpdateWork implements Work {
	public MetaMap data = null;
	public MetaMap model = null;
	
	public int count = 0;
	
	public Session session = null;

	public String sql = "";
	
	@Override
	public String getSql() {
		return sql;
	}
	
	
	public UpdateWork(Session session, MetaMap model, MetaMap data) {
		this.session = session;
		this.model = model;
		this.data = data;
	}

	public void execute(Connection conn) throws SQLException {

		String name = "";
		Object value = null;
		String values = "";
		
		PreparedStatement ps = null;
		int i = 0;
		MetaMap parent = model;
		String sql = null;
		Object refid = null;
		while (parent != null) {
			sql = parent.get("update").toString() + data.get("id");
			Consts.log.info(sql);
			
			ps = conn.prepareStatement(sql);
			i = 0;
			List<MetaMap> fields = parent.listmap("fields");
			for (int j = 0; j < fields.size(); j++) {
				if (Exp.isTrue(fields.get(j).get("notcolumn")))
					continue;
				
				ps.setObject(++i, value = data.get(name = fields.get(j).str("name")));
				values += "," + name + "=" + value;
			}

			List<MetaMap> many2ones = parent.listmap("many2ones");
			for (int j = 0; j < many2ones.size(); j++) {
				if (Exp.isTrue(fields.get(j).get("notcolumn")))
					continue;
				
				
				if (Exp.isNull(refid = data.get(name = many2ones.get(j).get("name") + ".id")) 
					&& Exp.isNull(refid = data.get(name = many2ones.get(j).str("columnName"))) )
					refid = null;
				ps.setObject(++i, refid);
				values += "," + name + "=" + refid;
			}
			
			Consts.log.info(values);
			try {
				count += ps.executeUpdate();
			} catch (SQLException e) {
				Consts.log.error("更新库表 " 
					+  model.get("label") 
					+ "(" + model.get("tableName") 
					+ ") 记录时错误:" + e.getLocalizedMessage());
				return;
			} finally {
				if (ps != null)
					ps.close();
			}
			
			parent = (MetaMap)parent.get("superClass");
		}
//		conn.commit();
	}

}
