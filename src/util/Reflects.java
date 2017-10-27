package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import annot.Params;
/**
 * 反射助手.
 */
@SuppressWarnings("unchecked")
public class Reflects {
	public static final transient Log log = LogFactory.getLog(Reflects.class);
	
	public static final Reflects instance = new Reflects();

	/**
	 * 调用指定对象的方法.
	 * 
	 * @param clazz 对象所属类
	 * @param object 对象
	 * @param methodName 方法名
	 * @param parameterTypes 参数类型
	 * @param args 参数
	 * @return 调用结果
	 * @throws Exception
	 */
	public static Object invoke(Class<?> clazz, Object object,
		String methodName, Class<?>[] parameterTypes, Object[] args)
		throws Exception {
		Method method = clazz.getMethod(methodName, parameterTypes);

		return method.invoke(object, args);
	}

	public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes,
		Object[] args) throws Exception {
		Constructor<?> c = clazz.getConstructor(parameterTypes);

		return c.newInstance(args);
	}

	/**
	 * 将字符串转换为其他类.
	 * 
	 * @param value 值
	 * @param clazz 类
	 * @return 转换后的对象
	 */
	public static Object convert(String value, Class<?> clazz) throws Exception {
		if (value == null || clazz == null) {
			return null;
		} else if (!java.util.Date.class.isAssignableFrom(clazz)) {
			return ConvertUtils.convert(value, clazz);
		} else if (java.sql.Date.class.isAssignableFrom(clazz)) {
			return java.sql.Date.valueOf(value);
		}
		/*
		 * else if (java.sql.Blob.class.isAssignableFrom(clazz)) { return new
		 * SerialBlob(value.getBytes()); }
		 */
		else {
			try {
				String standard = "0000-00-00 00:00:00";

				standard = value
					+ standard
						.substring(value.length() > standard.length() ? standard
							.length() : value.length());

				if (standard.endsWith("00:00")) {
					return org.apache.commons.beanutils.ConvertUtils.convert(
						standard.substring(0, 10), java.sql.Date.class);
				} else {
					return org.apache.commons.beanutils.ConvertUtils.convert(
						standard, java.sql.Timestamp.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * 设置对象的属性值.<br>
	 * 支持2级设置
	 * 
	 * @param object 对象
	 * @param name 属性名
	 * @param value 属性值
	 * @throws Exception
	 */
	public static void setProperty(Object object, String name, Object value)
		throws Exception {
		if (object == null) {
			return;
		}

		if (value != null
			&& (value instanceof Collection || value instanceof Map || value
				.getClass().isArray())) {
			setFieldValue(object, name, value);
			return;
		}

		// 属性名中不包含".", 表示这是一个普通属性
		if (name.indexOf(".") == -1) {
			setFieldValue(
				object,
				name,
				value instanceof String ? convert(
					value == null ? null : value.toString(),
					getFieldType(object, name)) : value);
		}// 属性名中仅包含一个".", 表示这是一个引用对象
		else if (name.indexOf(".") != -1) {
			Object property = object;
			String[] names = name.split("\\.");
			for (int i = 0; i < names.length - 1; i++) {
				property = getProperty(property, names[i]);
			}

			if (property != null) {
				if (property instanceof Map) {
					((MetaMap) property).put(
						names[names.length - 1], value);
				} else if (property instanceof List) {
					((List<Object>) property).add(
						Integer.parseInt(names[names.length - 1]), value);
				} else {
					setFieldValue(
						property,
						names[names.length - 1],
						convert(value != null ? value.toString() : null,
							getPropertyType(property, names[names.length - 1])));
				}

			}
		}
	}

	public static void setFieldValue(Object object, String name, Object value)
		throws NoSuchFieldException, IllegalAccessException {
		Field field = getField(object, name);
		field.set(object, value);
	}

	public static Field getField(Object object, String name)
		throws NoSuchFieldException {
		Class<?> clazz = object.getClass();
		while (true) {
			Field[] fields = clazz.getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				if (fields[i].getName().equals(name)) {
					return fields[i];
				}
			}

			clazz = clazz.getSuperclass();
			if (clazz == null || clazz == Object.class || clazz.isInterface()) {
				break;
			}
		}
		return null;
	}

	/**
	 * 获得对象属性值.<br>
	 * 支持无限级属性值获取。以.号表示分割
	 * 
	 * @param object 对象
	 * @param name 属性名
	 * @return 属性值
	 * @throws Exception
	 */
	public static Object getProperty(Object object, String name)
		throws Exception {
		if (object == null)
			return null;

		// 引用属性
		if (name.indexOf(".") != -1) {
			String[] ref = name.split("\\.");

			Object prev = null;
			Object next = object;

			for (int i = 0; i < ref.length - 1; i++) {
				prev = next;

				if (prev instanceof Map) {
					next = ((MetaMap) prev).get(ref[i]);
				} else if (prev instanceof Collection) {
					next = ((Collection<?>) prev).toArray()[Integer
						.parseInt(ref[i])];
				} else {
					next = getFieldValue(prev, ref[i]);
				}

				if (next == null) {
					return null;
				}
			}

			return getFieldValue(next, ref[ref.length - 1]);
		} else // 普通属性
		{
			return getFieldValue(object, name);
		}
	}

	public static Object getFieldValue(Object object, String name)
		throws Exception {
		Field field = getField(object, name);

		return field.get(object);
	}

	public static Class<?> getFieldType(Object object, String name)
		throws Exception {
		Field field = getField(object, name);

		return field.getType();
	}

	/**
	 * 获得对象属性类型.<br>
	 * 支持无限级属性类型获取。以.号表示分割
	 * 
	 * @param object
	 * @param name
	 * @return
	 */
	public static Class<?> getPropertyType(Object object, String name)
		throws Exception {
		log.debug("判断对象属性类型");
		log.debug("{");
		if (name.indexOf(".") == -1) {
			return getFieldType(object, name);
		} else {
			Object property = object;
			String[] names = name.split("\\.");
			for (int i = 0; i < names.length; i++) {
				if (getFieldValue(property, names[i]) == null
					&& i != names.length - 2) {
					String current = "";
					for (int j = 0; j < i; j++) {
						current += names[j] + ".";
					}
					throw new NullPointerException("查找对象" + object + "属性值"
						+ name + "类型时" + current + "处为null值");
				}

				property = getFieldValue(property, names[i]);
				if (property instanceof Map) {
					log.debug("	父级为" + Map.class);
					if (((Map<?, ?>) property).size() == 0
						|| ((Map<?, ?>) property).values().toArray()[0] == null) {
						log.debug("		没找到子元素，返回" + String.class);
						log.debug("}");
						return String.class;
					} else {
						log.debug("		找到子元素，返回"
							+ ((Map<?, ?>) property).values().toArray()[0]
								.getClass());
						log.debug("}");
						return ((Map<?, ?>) property).values().toArray()[0]
							.getClass();
					}
				} else if (property instanceof List) {
					log.debug("	父级为" + List.class);
					if (((List<?>) property).size() == 0
						|| ((List<?>) property).get(0) == null) {
						log.debug("		没找到子元素，返回" + String.class);
						log.debug("}");
						return String.class;
					} else {
						log.debug("		找到子元素，返回"
							+ ((List<?>) property).get(0).getClass());
						log.debug("}");
						return ((List<?>) property).get(0).getClass();
					}
				} else if (i == names.length - 2) {
					log.debug("		已至倒数第2级，用反射方法返回"
						+ getFieldType(property, names[i]));
					log.debug("}");
					return getFieldType(property, names[i]);
				}
			}

			log.debug("	正常遍历结束，返回" + property.getClass());
			log.debug("}");
			return property.getClass();
		}
	}

	/**
	 * 判断某个类是否包含这个属性.
	 * 
	 * @param clazz 类
	 * @param name 属性名
	 * @return 包含返回true, 不包含返回false
	 */
	public static boolean hasField(Class<?> clazz, String name)
		throws Exception {
		while (true) {
			Field[] fields = clazz.getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().equals(name)) {
					return true;
				}
			}

			clazz = clazz.getSuperclass();
			if (clazz == null || clazz == Object.class || clazz.isInterface()) {
				break;
			}
		}
		return false;
	}

	/**
	 * 复制对象的值.<br>
	 * 有同名属性就复制，不同名则不处理
	 * 
	 * @param orig
	 * @param dist
	 * @throws Exception
	 */
	public static void copy(Object orig, Object dist, boolean withAllSuperClass)
		throws Exception {
		if (orig == null || dist == null) {
			return;
		}
		if (orig instanceof Map && !((MetaMap) orig).isEmpty()) {
			MetaMap map = (MetaMap) orig;
			Field[] fields = dist.getClass().getDeclaredFields();

			if (fields != null) {
				for (int i = 0; i < fields.length; i++) {
					if (map.containsKey(fields[i].getName())
						&& (null == map.get(fields[i].getName()) || fields[i]
							.getType().isAssignableFrom(
								map.get(fields[i].getName()).getClass()))) {
						/*
						 * log.debug("    copy field -> name:'" +
						 * fields[i].getName() + "' value:'" +
						 * (map.get(fields[i].getName()) == null ? "" :
						 * map.get(fields[i].getName()).toString()) + "'");
						 */
						fields[i].setAccessible(true);
						fields[i].set(dist, map.get(fields[i].getName()));
						// setProperty(dist, fields[i].getName(),
						// map.get(fields[i].getName()));
					}
				}
			}

			if (withAllSuperClass) {
				Class<?> parent = dist.getClass().getSuperclass();
				while (!parent.isInterface() && !parent.equals(Object.class)) {
					/* log.debug("    parent class: " + parent.getName()); */
					Field[] parentFields = parent.getDeclaredFields();

					if (parentFields != null) {
						for (int i = 0; i < parentFields.length; i++) {
							if (map.containsKey(parentFields[i].getName())
								&& (null == map.get(parentFields[i].getName()) || parentFields[i]
									.getType().isAssignableFrom(
										map.get(parentFields[i].getName())
											.getClass()))) {
								/*
								 * log.debug("    copy field -> name:'" +
								 * parentFields[i].getName() + "' value:'" +
								 * (map.get(parentFields[i].getName()) == null ?
								 * "" :
								 * map.get(parentFields[i].getName()).toString
								 * ()) + "'");
								 */

								parentFields[i].setAccessible(true);
								parentFields[i].set(dist,
									map.get(parentFields[i].getName()));
							}
						}
					}

					parent = parent.getSuperclass();
				}
			}
		} else {
			/*
			 * log.debug("copy: " + orig.getClass().getName() + " -> " +
			 * dist.getClass().getName()); log.debug("{");
			 */
			copy(object2map(orig, withAllSuperClass), dist, withAllSuperClass);
			/* log.debug("}"); */
		}
	}

	/**
	 * 将对象转换成fieldName=fieldValue形式的map.
	 * 
	 * @param object 对象
	 * @param withAllSuperClass 是否包含所有父类属性
	 * @return 转换后的map
	 * @throws Exception
	 */
	public static MetaMap object2map(Object object,
		boolean withAllSuperClass) throws IllegalAccessException {
		MetaMap map = new MetaMap();

		if (object != null) {
			Field[] fields = object.getClass().getDeclaredFields();
			if (fields != null) {
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].toString().indexOf("static") == -1) {
						fields[i].setAccessible(true);
						map.put(fields[i].getName(), fields[i].get(object));
					}
				}
			}

			if (withAllSuperClass) {
				Class<?> parent = object.getClass().getSuperclass();
				while (!parent.isInterface() && !parent.equals(Object.class)) {
					Field[] parentFields = parent.getDeclaredFields();

					if (parentFields != null) {
						for (int i = 0; i < parentFields.length; i++) {
							if (parentFields[i].toString().indexOf("static") == -1) {
								parentFields[i].setAccessible(true);
								map.put(parentFields[i].getName(),
									parentFields[i].get(object));
							}
						}
					}

					parent = parent.getSuperclass();
				}
			}
		}

		return map;
	}

	/**
	 * 获得对象的属性列表
	 * 
	 * @param clazz 类
	 * @param withAllSuperClass 是否包含所有父类属性
	 * @return 属性列表
	 * @throws Exception
	 */
	public static Field[] getDeclaredFields(Class<?> clazz,
		boolean withAllSuperClass) throws Exception {
		// Map map = new HashMap();
		List<Field> list = new ArrayList<Field>();

		Field[] fields = clazz.getDeclaredFields();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].toString().indexOf("static") == -1) {
					fields[i].setAccessible(true);
					list.add(fields[i]);
				}
			}
		}

		if (withAllSuperClass) {
			Class<?> parent = clazz.getSuperclass();
			while (!parent.isInterface() && !parent.equals(Object.class)) {
				Field[] parentFields = parent.getDeclaredFields();

				if (parentFields != null) {
					for (int i = 0; i < parentFields.length; i++) {
						if (parentFields[i].toString().indexOf("static") == -1) {
							parentFields[i].setAccessible(true);
							list.add(parentFields[i]);
						}
					}
				}

				parent = parent.getSuperclass();
			}
		}

		Field[] result = new Field[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = (Field) list.get(i);
		}

		return result;
	}

	public static Object getPrimitiveObject(Class<?> clazz) {
		if (int.class.isAssignableFrom(clazz)) {
			return Integer.valueOf(0);
		} else if (short.class.isAssignableFrom(clazz)) {
			// short s = 0;
			return Short.valueOf("0");
		} else if (long.class.isAssignableFrom(clazz)) {
			return Long.valueOf(0);
		} else if (float.class.isAssignableFrom(clazz)) {
			return Float.valueOf(0);
		} else if (double.class.isAssignableFrom(clazz)) {
			return Double.valueOf(0);
		} else if (char.class.isAssignableFrom(clazz)) {
			return Character.valueOf(' ');
		} else if (byte.class.isAssignableFrom(clazz)) {
			return Byte.valueOf("0");
		} else if (boolean.class.isAssignableFrom(clazz)) {
			return Boolean.TRUE;
		}

		return null;
	}

//	public static Object eval(String src, MetaMap params,
//		Session session) throws Exception {
//		src = src.trim();
//
//		if (src.toLowerCase().indexOf("return") != -1 && src.endsWith(";")
//			|| src.startsWith("\"") && src.endsWith("\"")) {
//			return Scripts.bsh(src, params);
//		}
//
//		if (src.toLowerCase().startsWith("select")
//			&& src.toLowerCase().indexOf("from") != -1) {
//			return session.createSQLQuery(src).list();
//		}
//
//		throw new IllegalArgumentException(
//			"this method doesn't support this expression: " + src);
//	}
//
//	/**
//	 * 动态运行，使用BeanShell.
//	 * 
//	 * @param src
//	 * @param session
//	 * @return
//	 * @throws Exception
//	 */
//	public static Object eval(String src, Session session) throws Exception {
//		src = src.trim();
//
//		if ((src.toLowerCase().indexOf("return") != -1 && src.endsWith(";"))
//			|| (src.startsWith("\"") && src.endsWith("\""))) {
//			return Scripts.bsh(src, null);
//		}
//
//		if (src.toLowerCase().startsWith("select")
//			&& src.toLowerCase().indexOf("from") != -1) {
//			return session.createSQLQuery(src).list();
//		}
//
//		return src;
//	}

	/**
	 * 判断一个类是否包含指定的方法
	 * 
	 * @param clazz
	 * @param method
	 * @return
	 */
	public static boolean doesClassContainsMethod(Class<?> clazz,
		String method, boolean isReload) {
		String[] names = null;

		if (-1 == method.indexOf(".")) {
			names = new String[] { method };
		} else {
			names = method.split("\\.");
		}

		Class<?> next = clazz;
		for (int i = 0; i < names.length; i++) {
			Method[] methods = next.getDeclaredMethods();
			methods[i].getReturnType();
		}
		return true;
	}

	public static Class<?> classForName(String name)
		throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader()
				.loadClass(name);
		} catch (Exception e) {
			return Class.forName(name);
		}
	}

	/**
	 * 看看类是不是一个包装类
	 * 
	 * @param wrapperClass
	 * @return
	 */
	public static <T> Class<T> isWrapperClass(Class<T> wrapperClass) {
		try {
			return (Class<T>) wrapperClass.getField("TYPE").get(null);
		} catch (Throwable e) {
		}

		return null;
	}

	public static Method getMethod(Class<?> clazz, String name) {
		while (true) {
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.getName().equals(name))
					return m;
			}

			clazz = clazz.getSuperclass();
			if (clazz == null || clazz == Object.class || clazz.isInterface()) {
				break;
			}
		}
		return null;
	}
}