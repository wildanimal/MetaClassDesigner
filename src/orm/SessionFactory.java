package orm;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import orm.dialect.Dialect;
import util.Consts;

/**
 * 会话工厂.
 * @author chenmin
 *
 */
public class SessionFactory {
	public Configuration cfg = null;
	
	public BasicDataSource ds = null;
	
	public Dialect dialect = null;
	
	public SessionFactory() {
	}
	
	public SessionFactory(Configuration cfg) {
		this.cfg = cfg;
        dialect = Dialect.create(cfg.dialect);
		
		ds = new BasicDataSource();
		ds.setDriverClassName(cfg.driver);
        ds.setUrl(cfg.url);
        ds.setUsername(cfg.username);
        ds.setPassword(cfg.password);
	}
	
	public ClassMetadata getClassMetadata(Class model) {
		return null;
	}
	
	public Session openSession() throws Exception {
		//Consts.log.info("open a session");
		return new Session(this);
	}
	
	public void destroy() throws Exception {
		if (ds != null)
			ds.close();
	}
}
