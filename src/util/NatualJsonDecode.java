package util;

import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

//@SuppressWarnings("unchecked")
public class NatualJsonDecode implements JsonDeserializer<Object> {
	public Object deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
		if (json.isJsonNull())
			return null;
		else if (json.isJsonPrimitive())
			return handlePrimitive(json.getAsJsonPrimitive());
		else if (json.isJsonArray())
			return handleArray(json.getAsJsonArray(), context);
		else
			return handleObject(json.getAsJsonObject(), context);
	}

	private Object handlePrimitive(JsonPrimitive json) {
		if (json.isBoolean())
			return json.getAsBoolean();
		else if (json.isString())
			return json.getAsString();
		else {
			BigDecimal bigDec = json.getAsBigDecimal();
			// Find out if it is an int type
			try {
				bigDec.toBigIntegerExact();
				try {
					return bigDec.intValueExact();
				} catch (ArithmeticException e) {
				}
				return bigDec.longValue();
			} catch (ArithmeticException e) {
			}
			// Just return it as a double
			return bigDec.doubleValue();
		}
	}

	private Object handleArray(JsonArray json,
			JsonDeserializationContext context) {
//		Object[] array = new Object[json.size()];
		int size = json.size();
		List<Object> array = new ArrayList<Object>();
		if (size > 0) {
			if (json.get(0).isJsonPrimitive()) {
				JsonPrimitive jp = null;
				for (int i = 0; i < size; i++) {
					jp = json.get(i).getAsJsonPrimitive();
					if (jp.isString()) {
						array.add(jp.getAsString());
					} else if ( jp.isBoolean() ) {
						array.add(jp.getAsBoolean());
					} else if ( jp.isNumber() ) {
						array.add(jp.getAsNumber());
					}
				}
			} else {
				for (int i = 0; i < size; i++) {
					array.add(context.deserialize(json.get(i)
						, MetaMap.class));
				}
			}
		}
		return array;
	}

	private Object handleObject(JsonObject json,
			JsonDeserializationContext context) {
		MetaMap map = new MetaMap();
		for (Map.Entry<String, JsonElement> entry : json.entrySet())
			map.put(entry.getKey(),
					context.deserialize(entry.getValue(), Object.class));
		return map;
	}
	
	public static Object fromJson(String src, Type type) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Object.class, new NatualJsonDecode());
		Gson gson = gsonBuilder.create();

		return gson.fromJson(src, type);
	}
	
	public static Object fromJson(Reader reader, Type type) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Object.class, new NatualJsonDecode());
		Gson gson = gsonBuilder.create();

		return gson.fromJson(reader, type);
	}
	
	public static void main(String[] args) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Object.class, new NatualJsonDecode());
		Gson gson = gsonBuilder.create();

		gson.fromJson("", Object.class);
	}
}