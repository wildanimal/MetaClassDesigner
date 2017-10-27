package orm.work;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.Session;
import orm.Work;
import orm.dialect.Dialect;
import orm.dialect.MongoDBDialect;
import util.Exp;
import util.MetaMap;
//@SuppressWarnings("unchecked")
/**
 * 数据库单条记录新增工作.
 * @author chenmin
 *
 */
public class InsertWork implements Work {
	public static Log log = LogFactory.getLog(InsertWork.class);
	public MetaMap data = null;
	
	public MetaMap model = null;
	
	public Serializable id = null;
	
	public Session session = null;
	
	public int count = 0;

	public String sql = "";
	
	@Override
	public String getSql() {
		return sql;
	}
	
	public InsertWork(Session session, MetaMap model, MetaMap data)
	throws SQLException {
		this.session = session;
		this.model = model;
		this.data = data;
		
		if (!model.name().equals("Token")) {
			data.put("createTime", new Date());
			MetaMap user = new MetaMap();
//			try { user = Https.getUser();
//			} catch (Exception e) {}
			
			if (user != null) {
				data.put("creator.id", user.get("id"));
				data.put("creator", user.get("id"));
				data.put("creatorName", user.get("name"));
			}
		}
	}

	public void execute(Connection conn) throws SQLException {
		MetaMap top = model;
		MetaMap current = model;
//		MetaMap parent = model;

		String name = "";
		Object value = null;
		String values = "";
		
		if (session.sf.dialect instanceof MongoDBDialect) {
			
			return;
		}
		
		
		Object refid = null;
		// 寻找最顶层父类
		while (top.get("superClass") != null) {
			top = top.map("superClass");
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = null;
		int i = 0;
		List<MetaMap> fields = null;
		List<MetaMap> many2ones = null;
		
		sql = top.get("insert").toString();
		log.info(sql);
		
		Dialect dialect = session.sf.dialect;
		id = dialect.generateId(model, data);
		
		ps = conn.prepareStatement(sql);
		
		if (id != null) {
			data.put("id", id);
			ps.setObject(1, id);
			i = 1;
			values += "id=" + id;
		} else {
			i = 0;
		}
		
		fields = top.listmap("fields");
		for (int j = 0; j < fields.size(); j++) {
			if (Exp.isTrue(fields.get(j).get("notcolumn")))
				continue;
			
			ps.setObject(++i, value = data.get(name = fields.get(j).str("name")));
			values += "," + name + "=" + value;
		}

		many2ones = current.listmap("many2ones");
		for (int j = 0; j < many2ones.size(); j++) {
			if (Exp.isTrue(many2ones.get(j).get("notcolumn")))
				continue;
			
			if (Exp.isNull(refid = data.get(name = many2ones.get(j).get("name") + ".id" )) )
				refid = null;
			ps.setObject((++i), refid);
			values += "," + name + "=" + refid;
		}
		
		log.info(values);
			try {
				count += ps.executeUpdate();

				if (id == null) {
					// FIXME : 不同数据库对获取id的方式不同
					rs = ps.getGeneratedKeys();
					if (rs.next()) {
						id = (Serializable)rs.getObject(1);
					}
					data.put("id", id);
				}
			} catch (SQLException e) {
				log.error("新增库表 " 
					+  model.get("label") 
					+ "(" + model.get("tableName") 
					+ ") 记录时错误:" + e.getLocalizedMessage());
				return;
			} finally {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			}

		// 遍历子类
		while (current != null && current != top) {
			sql = current.get("insert").toString();
			log.info(sql);
			
			ps = conn.prepareStatement(sql);
			ps.setObject(1, id);
			
			fields = current.listmap("fields");
			i = 1; values = "";
			for (int j = 0; j < fields.size(); j++) {
				if (Exp.isTrue(fields.get(j).get("notcolumn")))
					continue;
				
				ps.setObject(++i, value = data.get(name = fields.get(j).str("name")));
				values += "," + name + "=" + value;
			}
			
			many2ones = current.listmap("many2ones");
			if (many2ones != null) {
				for (int j = 0; j < many2ones.size(); j++) {
					if (Exp.isTrue(many2ones.get(j).get("notcolumn")))
						continue;
					
					if (Exp.isNull(refid = data.get(name = many2ones.get(j).get("name") + ".id")) )
						refid = null;
					ps.setObject(++i, refid);
					values += "," + name + "=" + refid;
				}
			}

			
			log.info(values);
			try {
				count += ps.executeUpdate();
				if (id == null) {
					rs = ps.getGeneratedKeys();
					if (rs.next()) {
						id = rs.getLong(1);
					}
					data.put("id", id);
					rs.close();
				}
			} catch (SQLException e) {
				log.error("新增库表 " 
						+  model.get("label") 
						+ "(" + model.get("tableName") 
						+ ") 记录时错误:"
						+ e.getLocalizedMessage());
				return;
				} finally {
					if (rs != null) rs.close();
					if (ps != null) ps.close();
				}

			current = (MetaMap)current.get("superClass");
		}
//		conn.commit();
	}

}
