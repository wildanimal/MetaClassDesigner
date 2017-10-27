package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMap extends ArrayList<MetaMap> {
	public static Map<String, Object> findBy(List<Map<String, Object>> list, String key , String value) {
		for (Map<String, Object> map : list) {
			if (value.equals(map.get(key))) {
				return map;
			}
		}
		return null;
	}
	public static MetaMap findBy(ListMap list, String key , String value) {
		for (MetaMap map : list) {
			if (value.equals(map.get(key))) {
				return map;
			}
		}
		return null;
	}

	public static ListMap load(String str) {
		ListMap pkg = (ListMap) NatualJsonDecode.fromJson(str, ListMap.class);
		return pkg;
	}
}
