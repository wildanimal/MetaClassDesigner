package orm.work;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.Session;
import orm.Work;
import util.Consts;
import util.MetaMap;
/**
 * 数据库批量删除工作.
 * @author chenmin
 *
 */
public class DeletesWork implements Work {
	public static Log log = LogFactory.getLog(DeletesWork.class);
	public Serializable[] ids = null;
	public MetaMap model = null;
	
	public int count = 0;
	
	public Session session = null;

	public String sql = "";
	
	@Override
	public String getSql() {
		return sql;
	}
	
	public DeletesWork(Session session, MetaMap model, Serializable[] ids) {
		this.session = session;
		this.model = model;
		this.ids = ids;
	}

	public void execute(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		MetaMap parent = model;
		String idchain = (ids.getClass() == Long[].class) ? 
			StringUtils.join(ids, ",") : "'" + StringUtils.join(ids, "','" + "'");
		// FIXME: 注意sql注入安全
		String sql = null;
		while (parent != null) {
			sql = parent.get("delete").toString() + " in ("+idchain+")";
			log.info(sql);
			ps = conn.prepareStatement(sql);
			
			try {
				count += ps.executeUpdate();
			} catch (SQLException e) {
				Consts.log.error("删除库表 " 
					+  model.get("label") 
					+ "(" + model.get("tableName") 
					+ ") 记录时错误:\n" + e.getLocalizedMessage());
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
