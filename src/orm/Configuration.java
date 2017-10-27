package orm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import util.Consts;
import util.Exp;
import util.MetaMap;
import util.Reflects;

/**
 * 配置.
 * @author chenmin
 *
 */
@SuppressWarnings("unchecked")
public class Configuration {
	public Configuration configure(File file) throws IOException {
		Properties p = new Properties();
//		ClassLoader loader = this.getClass().getClassLoader();
		p.load(new FileInputStream(file));
		
		username = p.getProperty("hibernate.connection.username");
		password = p.getProperty("hibernate.connection.password");
		driver = p.getProperty("hibernate.connection.driver_class");
		url = p.getProperty("hibernate.connection.url");
		dialect = p.getProperty("hibernate.dialect");
		return this;
	}
	
	public String username = "";
	public String password = "";
	public String dialect = "";
	public String url = "";
	public String driver = "";
	
	public String session_factory = "";
	
	public SessionFactory sf = null;
	
	public SessionFactory buildSessionFactory() {
		if (Exp.isNull(session_factory)) {
			sf = new SessionFactory(this);
		} else {
			try {
			Class clazz = Reflects.classForName(session_factory);
			Constructor c = clazz.getConstructor(new Class[]{Configuration.class});
			
			sf = (SessionFactory)c.newInstance(new Object[]{this});
			} catch (Exception e) {
				Consts.log.error("无法实例化会话工厂类:"+session_factory);
			}
		}
		return sf;
	}
	
	/**
	 * 根据注解添加类
	 * TODO 未实现继承和多对一
	 * @param model
	 * @throws Exception
	 */
	public void addAnnotatedClass(Class model) throws Exception {
		String pkgName = model.getPackage().getName();

		MetaMap pkg = null;
		if ( (pkg = ORMgr.packages.get(pkgName)) == null) {
			pkg = new MetaMap();
			pkg.put("name", pkgName);
			ORMgr.packages.put(pkg.str("name"), pkg);
		}
		
		MetaMap oclass = new MetaMap();
		oclass.put("name", model.getSimpleName());
		Table table = (Table)model.getAnnotation(Table.class);
		oclass.put("table", table != null ? table.name() : model.getSimpleName());

		List<MetaMap> ofields = new ArrayList<MetaMap>();
		oclass.put("fields", ofields);
		
		Field[] fields = model.getDeclaredFields();
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				continue;
			}
			
			Transient t = field.getAnnotation(Transient.class);
			if (t != null) {
				continue;
			}
			
			MetaMap ofield = new MetaMap();
			ofields.add(ofield);
			ofield.put("name", field.getName());

			Column col = field.getAnnotation(Column.class);
			ofield.put("column", col != null ? col.name() : field.getName());
			
			ofield.put("type", field.getType().getSimpleName());
		}
		
//		ORMgr.updatePackageSchema(
//			ORMgr.dialects.get(ORMgr.defaultName), pkg);
	};
}
