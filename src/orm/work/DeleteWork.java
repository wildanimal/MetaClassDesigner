package orm.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.Session;
import orm.Work;
import util.Consts;
import util.MetaMap;
/**
 * 数据库单条记录删除工作.
 * @author chenmin
 *
 */
public class DeleteWork implements Work {
	public static Log log = LogFactory.getLog(DeleteWork.class);
	public MetaMap data = null;
	public MetaMap model = null;
	
	public int count = 0;
	
	public Session session = null;

	public String sql = "";
	
	@Override
	public String getSql() {
		return sql;
	}
	
	public DeleteWork(Session session, MetaMap model, MetaMap data) {
		this.session = session;
		this.model = model;
		this.data = data;
	}

	public void execute(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		MetaMap parent = model;

		while (parent != null) {
			String sql = parent.get("delete").toString() + " = ? ";
			log.info(sql);
			ps = conn.prepareStatement(sql);
			ps.setObject(1, data.get("id"));
			
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
