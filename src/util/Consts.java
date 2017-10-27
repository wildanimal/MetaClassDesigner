package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;


/**
 * 全局常量.
 */
public class Consts
{
	public static Log log = LogFactory.getLog(Consts.class);
	
	public static Consts instance = new Consts();
	
	public static String root = null;
	public static Properties config = null;
	static  {
		root = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/";
		config = new Properties();
		
		try {
			config.load(new FileInputStream(root + "/config.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void storeConfig()  {
		try {
			config.store(new FileOutputStream(root + "/config.properties"), "utf-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, String> SQLS = new HashMap<String, String>();
	public static String sql(String name) {
		return SQLS.get(name);
	}
	
	/**
	 * google json
	 */
	public static Gson Gson = new Gson();
	
	/**
	 * 转换成json
	 * @param element
	 * @return
	 */
	public static String toJson(JsonElement element) {
		StringWriter writer = new StringWriter();
		JsonWriter jw = new JsonWriter(writer);
		jw.setIndent("\t");
		jw.setLenient(true);
		
		Gson.toJson(element, jw);
		
		return writer.toString();
	}
	public static String toJson(List<Map<String, Object>> data) {
		StringWriter writer = new StringWriter();
		JsonWriter jw = new JsonWriter(writer);
		jw.setIndent("\t");
		jw.setLenient(true);
		
		Gson.toJson(data, Consts.ListMapStrObjType, jw);
		
		return writer.toString();
	}
	/**
	 * 转换成json
	 * @param object
	 * @param type
	 * @return
	 */
	public static String toJson(Object object, Type type) {
		StringWriter writer = new StringWriter();
		JsonWriter jw = new JsonWriter(writer);
		jw.setIndent("\t");
		jw.setLenient(true);
		
		Gson.toJson(object, type, jw);
		
		return writer.toString();
	}
	
	public static JsonParser JsonParser = new JsonParser();
	/** 列表,元素为图. */
	public static Type ListMetaMapType = new TypeToken<List<MetaMap>>(){}.getType();
	/** 列表,元素为图. */
	public static Type ListMapStrObjType = new TypeToken<List<Map<String, Object>>>(){}.getType();
	/** 列表,元素为对象数组. */
	public static Type ListObjArrType = new TypeToken<List<Object[]>>(){}.getType();
		
	/** 列表,元素为对象 */
	public static Type ListObjType = new TypeToken<List<Object>>(){}.getType();
		
	/** 列表,元素为字符串数组. */
	public static Type ListStrArrType = new TypeToken<List<String[]>>(){}.getType();

	/** 列表,元素为字符串. */
	public static Type ListStrType = new TypeToken<List<String>>(){}.getType();

	/** 列表,元素为字符串. */
	public static Type ListLongType = new TypeToken<List<Long>>(){}.getType();

	/** 列表,元素为图内嵌字符串-对象. */
	public static Type MapStrObjType = new TypeToken<Map<String, Object>>(){}.getType();

	/** 列表,元素为图内嵌字符串. */
	public static Type MapStrType = new TypeToken<Map<String, String>>(){}.getType();

	public static HashMap<String, Object> services = new HashMap<String, Object>();
	public static Object service(String name) {
		return services.get(name);
	}
	
	public static Properties DBINI = new Properties();
	public static File DBINI_FILE = null;
	static {
		String path = System.getProperty("user.dir") + "/db.ini";
		DBINI_FILE = new File(path);
		try {
			if ( !DBINI_FILE.exists()) {
				DBINI_FILE.createNewFile();
				DBINI.put("url", "localhost:3306/db");
				DBINI.put("username", "root");
				DBINI.put("password", "123");
				DBINI.store(new FileOutputStream(DBINI_FILE), "");
			} else {
				DBINI.load(new InputStreamReader(new FileInputStream(DBINI_FILE)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Gson getGson() {
		return Gson;
	}
	public static Consts getInstance() {
		return instance;
	}
	public static JsonParser getJsonParser() {
		return JsonParser;
	}
	public static Type getListLongType() {
		return ListLongType;
	}
	public static Type getListMapStrObjType() {
		return ListMapStrObjType;
	}
	public static Type getListObjArrType() {
		return ListObjArrType;
	}
	public static Type getListObjType() {
		return ListObjType;
	}
	public static Type getListStrArrType() {
		return ListStrArrType;
	}
	public static Type getListStrType() {
		return ListStrType;
	}
	public static Log getLog() {
		return log;
	}
	public static Type getMapStrObjType() {
		return MapStrObjType;
	}
	public static Type getMapStrType() {
		return MapStrType;
	}
	public static HashMap<String, Object> getServices() {
		return services;
	}
	public static HashMap<String, String> getSQLS() {
		return SQLS;
	}
}