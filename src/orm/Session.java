package orm;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.Id;
import javax.persistence.Transient;

import util.Consts;
import util.MetaMap;
import util.Reflects;

/**
 * 会话
 * @author chenmin
 *
 */
public class Session implements Cloneable {
	
	public SessionFactory sf = null;
	public SessionFactory getSessionFactory() {
		return sf;
	}
	
	public Connection conn = null;
	
	public Object conn2 = null;
	
	public Session(){
	
	}
	
	public Session(SessionFactory sf) throws Exception {
		this.sf = sf;
		if (sf.ds != null)
			this.conn = sf.ds.getConnection();
	}
	
	public void doWork(Work work) throws Exception {
		work.execute(this.conn);
	}
	
	public void close() {
		if (conn != null) {
			try { conn.close(); 
			//Consts.log.info("回收数据库联接");
			} catch (Exception e) {
				Consts.log.warn("关闭数据库连接错误:" );
			}
		}
	}
	
	public Object get(MetaMap model, Serializable id) throws Exception {
		return ORMgr.get(model, id);
	}
	
	public Object get(Class model, Serializable id) throws Exception {
		MetaMap data = ORMgr.get(
			model.getPackage().getName(), model.getSimpleName(), id);
			
		Object obj = null;
		try {
			obj = model.newInstance();
		} catch (Exception e) 
		{}
		
		for (Field field : model.getDeclaredFields()) {
			Id iden = field.getAnnotation(Id.class);
			if (iden != null) {
				continue;
			}
			
			Transient t = field.getAnnotation(Transient.class);
			if (t != null) {
				continue;
			}
			
			field.setAccessible(true);
			try {
				field.set(obj, data.get(field.getName()));
			} catch(Exception e) {
				Consts.log.error("设置类"+model.getName()+"属性错误" + field.getName());
			}
		}
		return obj;
	}
	
	public void open() {
		
	}
	
	public Query createQuery(String sql) {
		return new Query(this, sql);
	}
	
	public Query createSQLQuery(String sql) {
		return new Query(this, sql);
	}
	
	public Transaction beginTransaction() throws Exception {
		return new Transaction(this);
	}
	
	public void saveOrUpdate(Object bean) throws Exception {
		try {
			Field id = bean.getClass().getDeclaredField("id");
			if (id.get(bean) == null) {
				save(bean);
			} else {
				update(bean);
			} 
		} catch (Exception e) {
			throw new Exception("读取持久化类"+bean.getClass().getName()+"的id属性错误");
		}
	}
	
	/**新增.
	 * @param bean
	 * @throws Exception
	 */
	public void save(MetaMap model, MetaMap bean) throws Exception {
		ORMgr.insert(model, bean);
	}
	
	/**更新.
	 * @param bean
	 * @throws Exception
	 */
	public void update(MetaMap model, MetaMap bean) throws Exception {
		ORMgr.update(model, bean);
	}
	
	/**删除
	 * @param bean
	 * @throws Exception
	 */
	public void delete(MetaMap model,MetaMap bean) throws Exception {
		ORMgr.deletes(model, new Serializable[]{bean.id()});
	}
	
	/**新增.
	 * TODO 需要考虑继承关系
	 * @param bean
	 * @throws Exception
	 */
	public void save(Object bean) throws Exception {
		MetaMap model = ORMgr.getModel(bean.getClass().getName());
		MetaMap data = null;
		try {
			data = Reflects.object2map(bean, false);
		} catch (IllegalAccessException e) {
			throw new Exception("持久化类"+bean.getClass().getName()+"内容转换Map时错误");
		}
		ORMgr.insert(model, data);
	}
	
	/**更新.
	 * TODO 需要考虑继承关系
	 * @param bean
	 * @throws Exception
	 */
	public void update(Object bean) throws Exception {
		MetaMap model = ORMgr.getModel(bean.getClass().getName());
		MetaMap data = null;
		try {
			data = Reflects.object2map(bean, false);
		} catch (IllegalAccessException e) {
			throw new Exception("持久化类"+bean.getClass().getName()+"内容转换Map时错误");
		}
		ORMgr.update(model, data);
	}
	
	/**删除
	 * TODO 需要考虑继承关系
	 * @param bean
	 * @throws Exception
	 */
	public void delete(Object bean) throws Exception {
		MetaMap model = ORMgr.getModel(bean.getClass().getName());
		try {
			Field id = bean.getClass().getDeclaredField("id");
			
			ORMgr.deletes(model, new Serializable[]{
				(Serializable)id.get(bean)});
		} catch(Exception e) {
			throw new Exception("读取持久化类"+bean.getClass().getName()+"的id属性错误");
		}
	}
	
	public boolean isOpen() {
		try {
			return !conn.isClosed();
		} catch (Exception e) {
			
		}
		
		return false;
	}
}
