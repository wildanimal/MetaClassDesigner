package util;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 字符串助手.
 */
@SuppressWarnings("unchecked")
public class Strings {
	public static final transient Log log = LogFactory
		.getLog(Strings.class);
	
	public static final Strings instance = new Strings();

	/**
	 * 填充变量到字符串中
	 * 
	 * @param src 源字符串
	 * @param map 对象名字-值
	 * @param expStart 表达式开始字符串
	 * @param expEnd 表达式结束字符串
	 * @return 经过填充后的对象
	 */
	public static String fill(String src, Map map, String expStart,
		String expEnd) {
		if (src == null || src.equals("")) {
			return "";
		}

		StringBuffer dist = new StringBuffer();

		try {
			int start = 0;
			int end = 0;
			int prev = 0;
			while (true) {
				prev = end;
				if ((start = src.indexOf(expStart, end)) == -1
					|| (end = src.indexOf(expEnd, start)) == -1 || start == end) {
					break;
				}

				String key = src.substring(start + expStart.length(), end);

				if (start > 0) {
					dist.append(src.substring(prev == 0 ? 0 : prev + 1,
						src.indexOf(expStart + key + expEnd, prev)));
				}

				if (map.containsKey(key)) {
					dist.append(map.get(key) == null ? "" : map.get(key)
						.toString());
				} else if (key.indexOf(".") != -1) {
					String objectName = key.substring(0, key.indexOf("."));
					String fieldName = key.substring(key.indexOf(".") + 1);

					if (map.containsKey(objectName)) {
						Object value = Reflects.getProperty(
							map.get(objectName), fieldName);
						dist.append(value == null ? "" : value.toString());
					}
				}
			}

			if (prev == 0) {
				dist.append(src);
			} else if (prev < src.length() - 1) {
				dist.append(src.substring(prev + 1));
			}
		} catch (Exception e) {
			log.error(Strings.class.getName() + " fill error ", e);
			return "";
		}

		return dist.toString();
	}

	/**
	 * 替换字符串中的子字符串
	 * 
	 * @param src
	 * @param fnd
	 * @param rep
	 * @return
	 */
	public static String replaceAll(String src, String fnd, String rep) {
		if (src == null || src.equals("")) {
			return "";
		}

		StringBuffer result = new StringBuffer();

		int prev = 0;
		int next = 0;

		while ((next = src.indexOf(fnd, prev)) >= 0) {
			result.append(src.substring(prev, next));
			result.append(rep);
			prev = next + fnd.length();
		}
		result.append(src.substring(prev));

		return result.toString();
	}

	/**
	 * 根据指定字符串分割源字符串并填充到List中
	 * 
	 * @param src 源字符串
	 * @param split 用于分割的字符串
	 * @return
	 */
	public static List<String> split(String src, String split) {
		List<String> list = new ArrayList<String>();

		if (src == null || src.equals("")) {
			return list;
		}

		int prev = 0;
		int next = 0;

		String s = src + split;

		while (prev < s.length()) {
			if ((next = s.indexOf(split, prev)) == -1) {
				break;
			}

			list.add(s.substring(prev, next));

			prev = next + 1;
		}

		return list;
	}

	/**
	 * 统计子字符串出现的次数
	 * 
	 * @param src
	 * @return
	 */
	public static int count(String src, String find) {
		int result = 0;
		int index = 0;
		while (index < src.length() - 1
			&& (index = src.indexOf(find, index)) != -1) {
			result++;
			index += find.length();
		}
		return result;
	}
	
	public String dot2slash(String src) {
		if (Exp.isNull(src))
			return "";
		
		return src.replaceAll("\\.", "/");
	}

	public static String printStackTrace(Exception e) {
		String result = "";
		CharArrayWriter caw = new CharArrayWriter();
		PrintWriter pw = new PrintWriter(caw);
		e.printStackTrace(pw);

		result = caw.toString();
		caw.close();
		pw.close();
		return result;
	}
}