package orm;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库工作.
 * @author chenmin
 *
 */
public interface Work {
	public void execute(Connection conn) throws SQLException;
	
	public String getSql();
}
