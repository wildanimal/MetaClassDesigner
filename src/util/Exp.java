package util;

import java.util.Collection;
import java.util.Map;


/**
 * 用来简化表达式的编写
 */
public class Exp {
	public static final Exp instance = new Exp();
	
	/**
	 * 是否相等
	 * @param src
	 * @param dist
	 * @return
	 */
	public static boolean eq(Object src, Object dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src.toString().equals(dist.toString())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 是否为空
	 * @param o
	 * @return
	 */
	public static boolean isNull(Object o) {
		String str = null;
		if (o == null 
			|| (str = o.toString().trim()).equals("")
			|| str.equalsIgnoreCase("null")
			|| str.equalsIgnoreCase("0"))
			return true;
		
		return false;
	}
	
	/**
	 * 是否为空
	 * @param o
	 * @return
	 */
	public static boolean isObjectNull(Object o) {
		if (o == null)
			return true;
		
		return false;
	}
	
	/**
	 * 判断数字小于
	 * @param src
	 * @param dist
	 * @return
	 */
	public static boolean less(Integer src, Integer dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	public static boolean less(Long src, Long dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	public static boolean less(Float src, Float dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	public static boolean less(Double src, Double dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	/**
	 * 判断数字大于
	 * @param src
	 * @param dist
	 * @return
	 */
	public static boolean greater(Integer src, Integer dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	public static boolean greater(Long src, Long dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	public static boolean greater(Float src, Float dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	public static boolean greater(Double src, Double dist) {
		if (isNull(src) || isNull(dist))
			return false;
		
		if (src < dist)
			return true;
		
		return false;
	}
	
	/**
	 * 字符串转数字.
	 * @param src
	 * @return
	 */
	public static Integer num(String src) {
		return atoi(src);
	}
	
	/**
	 * 字符串转数字.
	 * @param src
	 * @return
	 */
	public static Integer atoi(String src) {
		if (isObjectNull(src))
			return null;
		
		return new Integer(src);
	}
	
	/**
	 * 字符串转float.
	 * @param src
	 * @return
	 */
	public static Float atof(String src) {
		if (isObjectNull(src))
			return null;
		
		return new Float(src);
	}
	
	/**
	 * 字符串转double.
	 * @param src
	 * @return
	 */
	public static String otos(Object src) {
		if (isNull(src))
			return "";
		
		return src.toString();
	}
	
	/**
	 * 字符串转double.
	 * @param src
	 * @return
	 */
	public static Double atod(String src) {
		if (isNull(src))
			return null;
		
		return new Double(src);
	}
	
	/**
	 * 字符串转数字.
	 * @param src
	 * @return
	 */
	public static Integer num(String src, Integer init) {
		return atoi(src, init);
	}
	
	/**
	 * 字符串转数字.
	 * @param src
	 * @return
	 */
	public static Integer atoi(String src, Integer init) {
		if (isNull(src))
			return init;
		
		return new Integer(src);
	}
	
	/**
	 * 字符串转float.
	 * @param src
	 * @return
	 */
	public static Float atof(String src, Float init) {
		if (isNull(src))
			return init;
		
		return new Float(src);
	}
	
	/**
	 * 字符串转double.
	 * @param src
	 * @return
	 */
	public static Double atod(String src, Double init) {
		if (isNull(src))
			return init;
		
		return new Double(src);
	}

	/**
	 * 转换变量的值为变量定义的类型.
	 * @param val
	 * @param var
	 * @return
	 */
	public static Object convert(String type, String value) throws Exception {
		Object val = value;
		if (type.equalsIgnoreCase("String")) {
			return isNull(value) ? "" : value;
		} else if (type.equalsIgnoreCase("Boolean")) {
			val = isNull(value) ? false 
				: value.equalsIgnoreCase("true");
		} else if (type.equalsIgnoreCase("Integer")) {
			val = isNull(value) ? 0 
				: new Integer(value);
		} else if (type.equalsIgnoreCase("Long")) {
			val = isNull(value) 
				? 0L : new Long(value);
		} else if (type.equalsIgnoreCase("Double")) {
			val = isNull(value) 
				? 0D : new Double(value);
		} else if (type.equalsIgnoreCase("Float")) {
			val = isNull(value) 
			? 0F : new Float(value);
	}
		return val;
	}
	
	public static String str(Object val) {
		if (Exp.isNull(val))
			return "";
		
		return val.toString();
	}
	
	public static boolean isTrue(Object val) {
		if (Exp.isNull(val))
			return false;
		
		return val.toString().equalsIgnoreCase("true");
	}
	
	public static boolean isEmpty(Object obj) {
		if (obj == null)
			return true;
		
		if (obj.getClass().isArray()) {
			return ((Object[])obj).length == 0;
		}
		
		if (obj instanceof Collection) {
			return ((Collection)obj).isEmpty();
		}
		
		if (obj instanceof Map) {
			return ((Map)obj).isEmpty();
		}
		
		return false;
	}
}
