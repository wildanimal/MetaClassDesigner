package orm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.transformer.MetaMapTransformer;
import orm.work.DeleteWork;
import orm.work.DeletesWork;
import orm.work.InsertWork;
import orm.work.UpdateWork;
import util.ClassScaner;
import util.Consts;
import util.Exp;
import util.MetaMap;
import util.NatualJsonDecode;

/**
 * 数据库操作管理类.
 */
@SuppressWarnings("unchecked")
public class ORMgr {
	public static ORMgr instance = new ORMgr();
	
	/* log4j logger */
	public static final transient Log log = LogFactory.getLog(ORMgr.class);

	public static Map<String, String> databases = new HashMap<String, String>();

	public static Map<String, Configuration> cfgs = new HashMap<String, Configuration>();
	/** session factorys */
	public static Map<String, SessionFactory> sessionFactorys = new HashMap<String, SessionFactory>();

	public static String defaultName = "default";
	
	public static Map<String, MetaMap> packages = new LinkedHashMap<String, MetaMap>();

	/**
	 * session var
	 */
	public static final ThreadLocal<Session> sessions = new ThreadLocal<Session>();
	public static final ThreadLocal<String> names = new ThreadLocal<String>();
	
	public static boolean configEmpty() throws Exception {
		File file = new File(Consts.root + "hibernate.properties");
		
		return !file.exists() || file.length() == 0;
	}
	
	public static boolean first = true;

	public static void configure(String name, String path, String[] dirs) throws Exception {
		
		if (configEmpty())
			return;
		
		File hibernateFile = new File(path);
		databases.put(name, name);
		Configuration cfg = new Configuration();
		cfgs.put(name, cfg);
		
		cfg.configure(hibernateFile);
		sessionFactorys.put(name, cfg.buildSessionFactory());
		
//		SchemaUpdate su = new SchemaUpdate(cfg);
//		su.execute(true, true);
		
		// 初始化数据
		// 主菜单表单的数据
		// 读取所有opackage并加载进内存
		//List<String> pkgs = s().createSQLQuery( "select t.name from orm_pkg t" ).list();
		if (first) {
			first = false;
			File dir = new File(Consts.root + "orm/");
	
			Properties props = new Properties();
			props.load(new FileReader(Consts.root + "hibernate.properties"));
			
			loadModel(new File(Consts.root + "orm/bsn.system.orm"), false);
			loadModel(new File(Consts.root + "orm/bsn.acl.orm"), false);
			loadModel(new File(Consts.root + "orm/bsn.orm.orm"), false);
			loadModel(new File(Consts.root + "orm/bsn.wf.orm"), false);
			
			ClassScaner cs = new ClassScaner(true, true, Entity.class);
			Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
			for (String scandir : dirs) {
				classes.addAll(cs.getPackageAllClasses(scandir, true));
			}
		
			for (Class<?> clazz : classes) {
				cfg.addAnnotatedClass(clazz);
			}
			
			for (MetaMap pkg : packages.values()) {
				try {
					updatePackageSchema(pkg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			String pkgFilter = "bsn.system.orm|bsn.acl.orm|bsn.orm.orm|bsn.wf.orm";
			
			for (File file : dir.listFiles()) {
				if (file.isDirectory() || !file.getName().endsWith(".orm")
					|| pkgFilter.indexOf(file.getName()) != -1) {
					continue;
				}
				loadModel(file, true);
			}
		}
		
		Transaction ts = null;
		try {		
			ts = s().beginTransaction();
			if (sql(" select id from t_user t where t.account = 'admin' ").size() == 0) {
				MetaMap user = new MetaMap();
				user.put("account", "admin");
				user.put("name", "系统管理员");
				user.put("passwd", "1");
				String sql = "insert into t_user (account, name, passwd) " +
					" values('admin', '系统管理员', '1')";
				ORMgr.createSQLQuery(sql).executeUpdate();
				//ORMgr.save("bsn.system", "User", user);
				
				ts.commit();
			}
		} catch (Exception e) {
			log.error("新增管理员用户错误", e);
			if (ts != null)
				ts.rollback();
		} finally {
			ORMgr.closeSession();
		}
//		MetaMap classModel = ORMgr.getModel("bsn.orm", "OClass");
//		try {
//			ts = ORMgr.s().beginTransaction();
//			for (MetaMap pkg : pkgmap.values()) {
//				if (Exp.isTrue(pkg.get("nottable")) ) {
//					continue;
//				}
//				ORMgr.s().createSQLQuery(
//				"delete from orm_class where pkg = ?")
//				.setParameter(0, pkg.get("name")).executeUpdate();
//				List<MetaMap> oclasses = (List<MetaMap>)pkg.get("classes");
//				for (MetaMap oclass : oclasses) {
//					if (Exp.isTrue(oclass.get("nottable")) ) {
//						continue;
//					}
//					ORMgr.insert(classModel, oclass);
//				}
//			}
//		ts.commit();
//		} catch (Exception e) {
//			if (ts != null)
//				ts.rollback();
//			
//			throw e;
//		}
	}

	private static void loadModel(File file, boolean upload) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		
		StringBuffer content = new StringBuffer();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(fis, "utf-8");
			while (reader.ready()) {
				content.append((char) reader.read());
			}
		} finally {
			if (reader != null)
				reader.close();
		}
		MetaMap pkg = (MetaMap)NatualJsonDecode.fromJson(
			content.toString(), MetaMap.class);
		if (!Exp.isTrue(pkg.get("nottable")))
			ORMUtil.arrange(pkg);
		ORMgr.packages.put(pkg.str("name"), pkg);
		
		if (upload) {
			try {
				updatePackageSchema(pkg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * #getSession() 的简化版.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Session session() throws Exception {
		return getSession();
	}

	public static Session s() throws Exception {
		return getSession();
	}

	public static Session getSession() throws Exception {
		if (names.get() == null) {
			names.set(defaultName);
		}

		Session session = null;
		// Open a new Session, if this Thread has none yet
		if (sessions.get() == null) {
			session = openSession(names.get().toString());
		} else {
			session = (Session) sessions.get();
		}

		if (session == null)
			return null;

		if (!session.isOpen()) {
			session = openSession(names.get().toString());
		}
		return session;
	}

	public static Session getSession(String name) throws Exception {
		log.info("get session :" + name);
		if (name == null || name.equals("")) {
			return getSession();
		}

		names.set(name);

		Session session = null;
		if (sessions.get() == null) {
			session = openSession(names.get().toString());
		} else {
			session = (Session) sessions.get();
		}

		if (null == session)
			return session;

		if (!session.isOpen()) {
			session = openSession(names.get().toString());
		}

		return session;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static Session openSession() throws Exception {

		if (names.get() == null) {
			names.set(defaultName);
		}

		Session session = null;
		if (sessions.get() == null || !((Session) sessions.get()).isOpen()) {
			SessionFactory sf = ((SessionFactory) sessionFactorys.get(names
				.get().toString()));
			if (null == sf)
				return null;

			session = sf.openSession();
		}

		sessions.set(session);
		return session;
	}

	public static Session openSession(String name) throws Exception {
//		log.info("hibernate thread local: open hibernate session.");
		names.set(name);

		Session session = null;
		if (sessions.get() == null || !((Session) sessions.get()).isOpen()) {
			if (sessionFactorys.get(name) == null)
				return null;
			SessionFactory sf = ((SessionFactory) sessionFactorys.get(name));
			if (null == sf)
				return null;

			session = sf.openSession();
		} else {
			session = ((Session) sessions.get());
		}

		sessions.set(session);
		return session;
	}

	public static void closeSession() {
		//log.info("thread local: close session.");
		if (sessions.get() != null) {
			sessions.get().close();
		}

		sessions.set(null);
		names.set(null);
	}

	/**
	 * @return Returns the sessionFactory.
	 */
	public static SessionFactory getSessionFactory() {
		if (names.get() == null) {
			names.set(defaultName);
		}

		return (SessionFactory) sessionFactorys.get(names.get().toString());
	}

	/**
	 * @return Returns the cfg.
	 */
	public static Configuration getCfg() {
		if (names.get() == null)
			names.set(defaultName);

		return (Configuration) cfgs.get(names.get().toString());
	}

	public static void setCfg(String name, Configuration cfg) {
		cfgs.put(name, cfg);
	}

	public static Configuration getCfg(String name) {
		return cfgs.get(name) == null ? null : (Configuration) cfgs.get(name);
	}

	public static SessionFactory getSessionFactory(String name) {
		return sessionFactorys.get(name) == null ? null
			: (SessionFactory) sessionFactorys.get(name);
	}

	public static void setSessionFactory(String name, SessionFactory sf) {
		sessionFactorys.put(name, sf);
	}
	
	public static void saveOrUpdate(Object obj) throws Exception {
		s().saveOrUpdate(obj);
	}
	
	public static void saveOrUpdate(String pkgName, String className
		, MetaMap obj)
	throws Exception {
		MetaMap model = getModel(pkgName, className);
		saveOrUpdate(model, obj);
	}
	
	public static void save(Object obj) throws Exception {
		s().save(obj);
	}
	
	public static void update(Object obj) throws Exception {
		s().update(obj);
	}
	
	public static int hqlUpdate(String sql) throws Exception {
		return s().createQuery(sql).executeUpdate();
	}
	
	public static int sqlUpdate(
			String sql, MetaMap model, MetaMap data) 
	throws Exception {
		return s().createSQLQuery(sql).executeUpdate();
	}
	
	public static int save(
		String pkgName, String className, MetaMap data)
	throws Exception {
		return insert(pkgName, className, data);
	}
	
	public static int insert(
		String pkgName, String className, MetaMap data)
	throws Exception {
		MetaMap model = getModel(pkgName, className);
		
		return insert(model, data);
	}
	
	public static int insert(MetaMap model, MetaMap data) 
	throws Exception {
		InsertWork work = new InsertWork(s(), model, data);
		s().doWork(work);
		
		return work.count;
	}
	
	public static int update(
		String pkgName, String className, MetaMap data) 
	throws Exception {
		MetaMap model = getModel(pkgName, className);
		return update(model, data);
	}
	
	public static int update(MetaMap model, MetaMap data)
	throws Exception {
		UpdateWork work = new UpdateWork(s(), model, data);
		s().doWork(work);
		return work.count;
	}
	
	public static int delete(MetaMap model, MetaMap data)
	throws Exception {
		DeleteWork work = new DeleteWork(s(), model, data);
		s().doWork(work);
		return work.count;
	}
	
	public static int deletes(MetaMap model, Serializable[] ids)
	throws Exception {
		DeletesWork work = new DeletesWork(s(), model, ids);
		s().doWork(work);
		return work.count;
	}
	
	public static List<?> hql(String sql)
	throws Exception {
		return s().createQuery(sql).list();
	}
	
	public static List<?> sql(String sql)
	throws Exception {
		return s().createSQLQuery(sql).list();
	}
	
	public static int count(String sql)
	throws Exception {
		return Integer.parseInt(s().createSQLQuery(sql).uniqueResult().toString());
	}
	
	public static MetaMap get(
		String entityName, Serializable id)
	throws Exception {
		MetaMap model = getModel(entityName);
		return get(model, id);
	}
	
	public static MetaMap get(
		String pkgName, String className, Serializable id)
	throws Exception {
		MetaMap model = getModel(pkgName, className);
		return get(model, id);
	}
	
	public static MetaMap get(MetaMap model, Serializable id)
	throws Exception {
		MetaMap result = new MetaMap();
		String sql = null;
		List<MetaMap> list = null;
		while (model != null ) {
			sql = model.get("select") + " where id = ? ";
			list = createSQLQuery(sql)
				.setParameter(0, id)
				.list();
			if (list.size() > 0) {
				MetaMap data = list.get(0);
				result.putAll( data );
			}
			
			getManyToOnes(model, result);
			
			model =(MetaMap)model.get("superClass");
		}
		
		return result;
	}

	public static void getManyToOnes(MetaMap model, MetaMap result)
	throws Exception {
		String sql;
		Object refid;
		String refName;
		MetaMap m2o;
		List<MetaMap> list;
		Object value;
		List<MetaMap> many2ones = (List<MetaMap>)model.get("many2ones");
		for (int j = 0; j < many2ones.size(); j++) {
			refName = (String)many2ones.get(j).get("name");
			if (Exp.isNull(refid = result.get(refName)) )
				continue;
			
			m2o = (MetaMap)many2ones.get(j).get("toClass");
			sql = (String)m2o.get("select") + " where id = ? ";
			list = s().createSQLQuery(sql)
				.setParameter(0, refid)
				.setResultTransformer(orm.transformer.MetaMapTransformer.instance)
				.list();
			if (list.size() > 0) {
//				Https.req().setAttribute(refName, list.get(0));
//				for (String key : list.get(0).keySet()) {
//					value = list.get(0).get(key);
//					result.put(refName + "." + key, value);
//				}
			}
			
		}
	}
	
	public static int updatePackageSchema(MetaMap pkg) throws Exception {
		try {
			Work work = ORMgr.s().sf.dialect
				.createSchemaWork(pkg);
			ORMgr.s().doWork(work);
		} finally {
		}
		return 1;
	}
	
	public static boolean saveOrUpdate(MetaMap model, MetaMap bean)
	throws Exception {
		if ( !Exp.isNull(bean.get("id"))){
			Query query = ORMgr.s().createSQLQuery(
				"select count(id) from " + model.get("tableName")
				+ " where id = ? ").setParameter(0, bean.get("id"));
			
			if (new Long(query.uniqueResult().toString()) > 0 )
				return update(model, bean) > 0;
		}
			
		return insert(model, bean) > 0;
	}
	
	public static MetaMap getModel(String name) {
		int dotIdx = name.lastIndexOf(".");
		String pkgName = name.substring(0, dotIdx);
		String className = name.substring(dotIdx + 1);
		return getModel(pkgName, className);
	}
	
	public static MetaMap getModel(String pkgName, String className) {
		MetaMap result = null;
//		String name = ORMgr.names.get();
		MetaMap pkg = packages.get(pkgName);
		if (pkg != null) {
			for (MetaMap model : pkg.listmap("classes")) {
				if (model.get("name").equals(className)) {
					result = model;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据{@param modelPkg}和{@param modelName}获取模型,接着根据属性名{@param key}对应的{@param value}查询库表.
	 * @param modelPkg 类所在包
	 * @param modelName 类名
	 * @param key 属性名
	 * @param value 属性值
	 * @return
	 */
	public static List<MetaMap> getsByField(
		String modelPkg,String modelName, String key, Object value)
		throws Exception {
		MetaMap model = getModel(modelPkg, modelName);
		return getsByField(model, key, value);
	}
	
	/**
	 * 根据模型和属性名{@param key}对应的{@param value}查询模型库表.
	 * 本查询不包含多对一对象
	 * @param model 类模型
	 * @param key 属性名
	 * @param value 属性值.
	 * @return
	 */
	public static List<MetaMap> getsByField(
		MetaMap model, String key, Object value)
		throws Exception {
		List<MetaMap> result = null;
		
		MetaMap field = ORMUtil.getField(model, key);
		
		String sql = model.get("select") + " where " + field.get("columnName") + " = ? ";
		
		result = s().createSQLQuery(sql).setParameter(0, value)
			.setResultTransformer(MetaMapTransformer.instance).list();
		return result;
	}
	public static List<MetaMap> getsBySql(
		String modelPkg,String modelName, String sql, Object ... params)
		throws Exception {
		
		MetaMap model = getModel(modelPkg, modelName);
		
		return getsBySql(model, sql, params);
	}
	
	public static List<MetaMap> getsBySql(
		MetaMap model, String sql, Object ... params)
		throws Exception {
		sql = model.get("select") + sql;
		
		Query query = createSQLQuery(sql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		return (List<MetaMap>)query.list();
	}
	
	public static orm.Query createSQLQuery(String sql) throws Exception {
		return new orm.Query(s(), sql);
	}
	
	public static void main(String[] params) throws Exception {
		String root = System.getProperty("user.dir");
		orm.ORMgr.configure(defaultName, root + "/hibernate.cfg.xml", new String[]{"bsn"});
	}
}
