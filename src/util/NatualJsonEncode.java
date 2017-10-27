package util;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NatualJsonEncode implements JsonSerializer<Object> {

	@Override
	public JsonElement serialize(Object obj, Type type,
			JsonSerializationContext ctx) {
		JsonElement result = null;
		if (obj instanceof List) {
			JsonArray ja = dealList((List) obj);
			result = ja;
		} else if (obj instanceof Map) {
			JsonObject jo = dealMap((Map) obj);
			result = jo;
		}
		return result;
	}
	
	public JsonArray dealList(List<Object> list) {
		JsonArray ja = new JsonArray();
		for (Object o : list) {
			if (o instanceof Map) {
				ja.add(dealMap((Map)o));
			} else if (o instanceof List) {
				ja.add(dealList((List)o));
			} else {
				ja.add(dealSimpleValue(o));
			}
		}
		return ja;
	}
	
	public JsonObject dealMap(Map<String, Object> map) {
		JsonObject jo = new JsonObject();
		for (String key : map.keySet()) {
			Object val = map.get(key);
			if (val == null || ((val instanceof String) && "".equals(val)))
				continue;
			
			if (val instanceof List) {
				jo.add(key, dealList((List) val));
			} else if (val instanceof Map) {
				jo.add(key, dealMap((Map) val));
			} else {
				jo.add(key, dealSimpleValue(val));
			}
		}
		return jo;
	}

	private JsonPrimitive dealSimpleValue(Object val) {
		switch (val.getClass().getSimpleName()) {
		case "String":
			return new JsonPrimitive(Exp.str(val));
		case "int":
		case "Integer":
		case "long":
		case "Long":
			Integer i = Exp.num(val.toString());
			return new JsonPrimitive(i);
		case "boolean":
		case "Boolean":
			return new JsonPrimitive(Exp.isTrue(val));
		case "float":
		case "Float":
		case "double":
		case "Double":
			return new JsonPrimitive(Exp.atof(val.toString()));
		}

		return new JsonPrimitive(Exp.str(val));
	}
	
	public static String toJson(Object obj) {
		GsonBuilder b = new GsonBuilder();
		b.registerTypeAdapter(Object.class, new NatualJsonEncode());
//		b.registerTypeAdapter(MetaMap.class, new NatualJsonEncode());
		Gson gson = b.create();

		return gson.toJson(obj);
	}
	
}
