package orm.work;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.Session;
import orm.Work;
/**
 * 数据库提交工作.
 * @author chenmin
 *
 */
public class CommitWork implements Work {
	public static Log log = LogFactory.getLog(CommitWork.class);

	public static CommitWork instance = new CommitWork();

	public String sql = "";
	
	@Override
	public String getSql() {
		return sql;
	}
	
	public Session session = null;

	public void execute(Connection conn) throws SQLException {
		if (!conn.getAutoCommit())
			conn.commit();
	}

}
