package orm;

import java.sql.SQLException;

/**
 * 事务.
 * @author chenmin
 *
 */
public class Transaction {
	public Session s = null;
	public Transaction(Session s) throws SQLException {
		this.s = s;
		s.conn.setAutoCommit(false);
	}
	
	public void commit() throws SQLException {
		s.conn.commit();
	}
	
	public void rollback() throws SQLException {
		s.conn.rollback();
	}
}
