package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class MetaMap extends LinkedHashMap<String, Object> {
	public static final long serialVersionUID = -1L;

	public MetaMap() {
		super();
	}

	public MetaMap(int length) {
		super(length);
	}

	public Serializable id() {
		return (Serializable) get("id");
	}

	public String name() {
		String result = (String) get("name");
		if (result == null) result = "";
		return result;
	}

	public String label() {
		String result = (String) get("label");
		if (result == null) result = "";
		return result;
	}

	public BigInteger bigint(String key) {
		return (BigInteger) get(key);
	}

	public BigDecimal bigdec(String key) {
		return (BigDecimal) get(key);
	}

	public String str(String key) {
		if (get(key) == null)
			return "";
		return get(key).toString();
	}

	public boolean bool(String key) {
		if (get(key) == null)
			return false;
		return (boolean)get(key);
	}

	public Integer num(String key) {
		if (get(key) == null)
			return 0;
		return Integer.valueOf(get(key).toString());
	}

	public Long getLong(String key) {
		if (get(key) == null)
			return 0L;
		return Long.valueOf(get(key).toString());
	}

	public Date date(String key) {
		return (Date) get(key);
	}

	public Double getDouble(String key) {
		if (get(key) == null)
			return null;
		return Double.valueOf(get(key).toString());
	}

	public Float getFloat(String key) {
		if (get(key) == null)
			return null;
		return Float.valueOf(get(key).toString());
	}

	public MetaMap map(String key) {
		return (MetaMap) get(key);
	}

	public List<MetaMap> listmap(String key) {
		List<MetaMap> result = (List<MetaMap>) get(key);
		if (result == null) {
			result = new ArrayList<MetaMap>();
			put(key, result);
		}
		return result;
	}

	public LinkedHashMap<String, MetaMap> mapmap(String key) {
		return (LinkedHashMap<String, MetaMap>) get(key);
	}

	public void set(MetaMap src, String key) {
		put(key, src.get(key));
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Object value = null;
		for (String key : this.keySet()) {
			value = this.get(key);
			if ("label".equals(key)) {
				buffer.append((String)value + "-");
			} else if ("name".equals(key)) {
				buffer.append((String)value + "-");
			}
//			if (value instanceof MetaMap || value instanceof List) {
//				continue;
//			}
//			buffer.append(key).append("=").append(value).append(" , ");
		}

		return buffer.toString();
	}

	public boolean isTrue(String key) {
		return Exp.isTrue(get(key));
	}

	public String toJson() {
		return Consts.toJson(this, MetaMap.class);
	}

	public MetaMap append(String key, Object value) {
		this.put(key, value);

		return this;
	}

	public static MetaMap create() {
		MetaMap instance = new MetaMap();
		return instance;
	}

	public static MetaMap create(String key, Object value) {
		MetaMap instance = new MetaMap();
		instance.put(key, value);
		return instance;
	}

	public void createListMap(String key) {
		put(key, new ArrayList<MetaMap>());
	}

	public List<MetaMap> datalist() {
		List<MetaMap> datalist = null;
		if (containsKey("datalist")) {
			datalist = listmap("datalist");
		} else {
			datalist = (List<MetaMap>) NatualJsonDecode
					.fromJson(str("datasource"),
							Consts.ListMetaMapType);
			
			put("datalist", datalist);
		}
		return datalist;
	}

	public static MetaMap load(File file) throws IOException {
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
		MetaMap pkg = (MetaMap) NatualJsonDecode.fromJson(content.toString(),
				MetaMap.class);
		return pkg;
	}

	public static MetaMap load(String str) {
		MetaMap pkg = (MetaMap) NatualJsonDecode.fromJson(str, MetaMap.class);
		return pkg;
	}
}
