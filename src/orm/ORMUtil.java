package orm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.dialect.Dialect;
import util.Exp;
import util.ListMap;
import util.MAP;
import util.MetaMap;

/**
 * 模型通用类.
 * 
 * @author chenmin
 * 
 */
@SuppressWarnings("unchecked")
public class ORMUtil {

	public static final Log log = LogFactory.getLog(ORMUtil.class);

	public static final ORMUtil instance = new ORMUtil();

	public static boolean isJoinedSubClass(Map<String, Object> entity) {
		MetaMap parent = (MetaMap) entity.get("superClass");
		if (parent != null) {
			return true;
		}

		return false;
	}

	public static String getTypeName(String type) {
		if (type.equals("Date"))
			return "java.util.Date";
		else if (type.equals("DateTime"))
			return "java.util.Date";
		else if (type.equals("Clob"))
			return "String";

		return type;
	}

	public static Class<?> getJavaType(String type) throws Exception {
		if (type.equals("Date"))
			return java.util.Date.class;
		else if (type.equals("DateTime"))
			return java.util.Date.class;
		else if (type.equals("Clob"))
			return String.class;

		return Class.forName("java.lang." + type);
	}

	public static MetaMap getEntity(MetaMap pkg, String id) {
		List<MetaMap> classes = (List<MetaMap>) pkg.get("classes");
		for (MetaMap oc : classes) {
			if (oc.get("id").equals(id))
				return oc;
		}
		return null;
	}

	public static MetaMap getClass(MetaMap pkg, MetaMap entity, String id) {
		if (entity != null && entity.get("id").equals(id))
			return entity;

		return getEntity(pkg, id);
	}

	public static MetaMap getEntityByKey(MetaMap pkg, String id, String key) {
		List<MetaMap> classes = (List<MetaMap>) pkg.get("classes");
		for (MetaMap oc : classes) {
			if (oc.get(key).equals(id))
				return oc;
		}
		return null;
	}

	public static String getPackageName(MetaMap pkg, MetaMap clazz) {
		if (Exp.isNull(clazz.get("pkg"))) {
			return pkg.str("name");
		}

		return clazz.str("pkg");
	}

	public static Boolean isSubClass(Map<String, Object> pkg, Map<String, Object> entity) {
		// List<MetaMap> routes = (List<MetaMap>)pkg.get("routes");
		// for (MetaMap r : routes) {
		// if (r.get("fromid").equals(entity.get("id"))
		// && r.get("type").equals("Extend")) {
		// return true;
		// }
		// }

		return entity.get("superClass") != null;
	}

	public static String getExtendString(Map<String, Object> entity) {
		MetaMap parent = (MetaMap) entity.get("superClass");
		if (parent != null) {
			return " extends " + parent.get("pkg") + "." + parent.get("name");
		}
		return "";
	}

	public static void arrange(MetaMap pkg) {
		pkg.put("path", pkg.get("name").toString().replaceAll("\\.", "/"));

		// XXX 目前固定MySql Dialet
		Dialect dialect = Dialect.create("mysql");

		List<MetaMap> classes = pkg.listmap("classes");
		for (MetaMap oclass : classes) {
			oclass.put("many2ones", null);
			oclass.put("one2manys", null);
		}

		MetaMap clazz = null;
		MetaMap toClass = null;
		List<MetaMap> routes = pkg.listmap("routes");

		for (MetaMap r : routes) {
			clazz = getClass(pkg, clazz, r.str("fromid"));
			clazz.put("many2ones", null);
			clazz.put("one2manys", null);
			toClass = getClass(pkg, toClass, r.str("toid"));
			r.put("toTypeName", getPackageName(pkg, toClass) + "." + toClass.get("name"));
			r.put("toSimpleName", toClass.get("name"));

			if (Exp.isTrue(toClass.get("outside"))) {
				// XXX 外部类在此暂不处理
				// r.put("toClass",
				// pkg.getModel(toClass.str("pkg"), toClass.str("name")));
			} else {
				r.put("toClass", toClass);
			}

			String type = r.str("type");
			if (type.equals("Extend")) {
				Map<String, Object> p = getEntity(pkg, r.str("toid"));
				if (Exp.isTrue(p.get("outside"))) {
					p = ListMap.findBy(MAP.listmap(pkg, "classes"), "name", MAP.str(p, "name"));
				}
				clazz.put("superClass", p);
			} else if (type.equals("ManyToOne")) {
				clazz.listmap("many2ones").add(r);
			} else if (type.equals("OneToMany")) {
				clazz.listmap("one2manys").add(r);
			}
		}

		StringBuffer inserts = new StringBuffer();
		for (MetaMap oclass : classes) {
			if (Exp.isTrue(oclass.get("outside"))) // 外部类不处理
				continue;

			if (Exp.isNull(oclass.get("idtype"))) {
				oclass.put("idtype", "Long");
			}
			if (Exp.isNull(oclass.get("idgentype"))) {
				oclass.put("idgentype", "auto");
			}

			oclass.put("tableName", Exp.isNull(oclass.get("dbtable")) ? oclass.get("name") : oclass.get("dbtable"));

			oclass.put("pkgName", Exp.isNull(oclass.get("pkg")) ? pkg.get("name") : oclass.get("pkg"));

			if (oclass.get("many2ones") == null)
				oclass.put("many2ones", new ArrayList<MetaMap>());

			if (oclass.get("one2manys") == null)
				oclass.put("one2manys", new ArrayList<MetaMap>());

			oclass.put("opackage", pkg);

			oclass.put("pkg", pkg.get("name"));
			oclass.put("path", (oclass.str("pkg")).replaceAll("\\.", "/"));

			if (inserts.length() > 0)
				inserts.delete(0, inserts.length());

			StringBuffer insert = new StringBuffer();
			insert.append("insert into " + oclass.get("tableName") + " (");
			StringBuffer update = new StringBuffer();
			update.append("update " + oclass.get("tableName") + " set ");
			StringBuffer delete = new StringBuffer();
			delete.append("delete from " + oclass.get("tableName") + " where " + oclass.get("idname") + "");
			StringBuffer select = new StringBuffer();
			select.append("select "+ oclass.get("idname") + " ");

			if (dialect.needInsertId(oclass)) {
				inserts.append("?,");
				insert.append(" " + oclass.get("idname") + ",");
			}

			for (MetaMap field : oclass.listmap("fields")) {
				if (Exp.isTrue(field.get("notcolumn")))
					continue;

				String columnName = Exp.isNull(field.get("column")) ? field.str("name") : field.str("column");
				field.put("columnName", columnName);

				if (Exp.isNull(field.get("type"))) {
					field.put("type", "String");
				}

				if (field.get("type").equals("String") && Exp.isNull(field.get("length"))) {
					field.put("length", "255");
				}
				// else if (field.get("type").equals("Boolean") ) {
				// field.put("length", "1");
				// } else if (field.get("type").equals("Integer") &&
				// Exp.isNull(field.get("length")) ) {
				// field.put("length", "10");
				// } else if (field.get("type").equals("Long") &&
				// Exp.isNull(field.get("length")) ) {
				// field.put("length", "19");
				// }

				inserts.append("?,");
				insert.append(columnName + ",");
				update.append(columnName + " = ?,");

				select.append(", " + columnName + " as \"" + field.get("name") + "\" ");
			}
			for (MetaMap fields : oclass.listmap("many2ones")) {
				if (Exp.isTrue(fields.get("notcolumn")))
					continue;

				String columnName = Exp.isNull(fields.get("column")) ? fields.str("name") : fields.str("column");

				fields.put("columnName", columnName);

				inserts.append("?,");
				insert.append(columnName + ",");
				update.append(columnName + " = ?,");

				select.append(", " + columnName + " as \"" + fields.get("name") + "\"");
			}

			if (inserts.length() == 0) {
				// log.error ("类" + pkg.get("name") + "." + oc.get("name") +
				// "没有属性");
			} else {
				inserts.deleteCharAt(inserts.length() - 1);
				insert.deleteCharAt(insert.length() - 1);
			}
			insert.append(") values (" + inserts + ")");

			update.deleteCharAt(update.length() - 1);
			update.append(" where id = ");

			// select.deleteCharAt(select.length() - 1);
			select.append(" from " + oclass.get("tableName"));// +
																// " where id =
																// ? ");

			oclass.put("insert", insert.toString());
			oclass.put("update", update.toString());
			oclass.put("delete", delete.toString());
			oclass.put("select", select.toString());
		}
	}

	public static MetaMap getField(MetaMap model, String name) {
		for (MetaMap field : model.listmap("fields")) {
			if (field.get("name").equals(name))
				return field;
		}
		for (MetaMap field : model.listmap("many2ones")) {
			if (field.get("name").equals(name))
				return field;
		}
		for (MetaMap field : model.listmap("one2manys")) {
			if (field.get("name").equals(name))
				return field;
		}
		return null;
	}

	public static MetaMap getMap(MetaMap map, String key) {
		return (MetaMap) map.get(key);
	}

	public static List<MetaMap> getList(MetaMap map, String key) {
		return (List<MetaMap>) map.get(key);
	}

	public static Map<String, MetaMap> getMapMap(MetaMap map, String key) {
		return (Map<String, MetaMap>) map.get(key);
	}

	public static MetaMap getManyToOne(MetaMap model, String name) {
		for (MetaMap field : model.listmap("many2ones")) {
			if (field.get("name").equals(name))
				return field;
		}

		return null;
	}
}
