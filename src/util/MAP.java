package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

public class MAP {
	public static String name(Map<String, Object> map ) {
		return (String) map.get("name");
	}

	public static String label(Map<String, Object> map) {
		return (String) map.get("label");
	}

	public static BigInteger bigint(Map<String, Object> map, String key) {
		return (BigInteger) map.get(key);
	}

	public static BigDecimal bigdec(Map<String, Object> map, String key) {
		return (BigDecimal) map.get(key);
	}

	public static String str(Map<String, Object> map, String key) {
		if (map.get(key) == null)
			return null;
		return map.get(key).toString();
	}

	public static Integer num(Map<String, Object> map, String key) {
		if (map.get(key) == null)
			return 0;
		return Integer.valueOf(map.get(key).toString());
	}

	public static Long getLong(Map<String, Object> map, String key) {
		if (map.get(key) == null)
			return 0L;
		return Long.valueOf(map.get(key).toString());
	}

	public static Date date(Map<String, Object> map, String key) {
		return (Date) map.get(key);
	}

	public static Double getDouble(Map<String, Object> map, String key) {
		if (map.get(key) == null)
			return null;
		return Double.valueOf(map.get(key).toString());
	}

	public static Float getFloat(Map<String, Object> map, String key) {
		if (map.get(key) == null)
			return null;
		return Float.valueOf(map.get(key).toString());
	}

	public static Map<String, Object> map(Map<String, Object> map, String key) {
		return (Map<String, Object>)map.get(key);
	}

	public static List<Map<String, Object>> listmap(Map<String, Object> map, String key) {
		return (List<Map<String, Object>>)map.get(key);
	}

	public static LinkedHashMap<String, Map<String, Object>> mapmap(Map<String, Object> map, String key) {
		return (LinkedHashMap<String, Map<String, Object>>)map.get(key);
	}

	public static void set(Map<String, Object> map, Map<String, Object> src, String key) {
		map.put(key, src.get(key));
	}

	public static boolean isTrue(Map<String, Object> map, String key) {
		return Exp.isTrue(map.get(key));
	}

	public static String toJson(Map<String, Object> map) {
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		return Consts.toJson(map, type);
	}

	public static Map<String, Object> append(Map<String, Object> map,
			String key, Object value) {
		map.put(key, value);

		return map;
	}

	public static Map<String, Object> create() {
		Map<String, Object> instance = new HashMap<String, Object>();
		return instance;
	}

	public static Map<String, Object> create(String key, Object value) {
		Map<String, Object> instance = new HashMap<String, Object>();
		instance.put(key, value);
		return instance;
	}

	public static void createListMap(Map<String, Object> map, String key) {
		map.put(key, new ArrayList<Map<String, Object>>());
	}

	public static List<Map<String, Object>> datalist(Map<String, Object> map) {
		List<Map<String, Object>> datalist = null;
		if (map.containsKey("datalist")) {
			datalist = listmap(map, "datalist");
		} else {
			datalist = (List<Map<String, Object>>) NatualJsonDecode.fromJson(
					str(map, "datasource"), Consts.ListMapStrObjType);

			map.put("datalist", datalist);
		}
		return datalist;
	}

	public static Map<String, Object> load(File file) throws IOException {
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
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> pkg = (Map<String, Object>) NatualJsonDecode
				.fromJson(content.toString(), type);
		return pkg;
	}
}
