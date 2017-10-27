package util;

import java.util.ArrayList;

public class PageParam {
	public PageParam() {
		
	}
	public PageParam(String name, String column
		, String operate, Class<?> type) {
		this.name = name;
		this.column = column;
		this.operate = operate;
		this.type = type;
	}
	/** 从request中去参数的参数名称 */
	public String name;
	/** 添加到sql中的名称 */
	public String column;
	/** 操作 */
	public String operate;
	/** 值的类型,用于转换从request取到的字符串值 */
	public Class<?> type;
	/** 值 */
	public Object value;
	
	public int compareTo(Object o) {
		PageParam param = (PageParam)o;
		return param.name.equals(name) ? 0 : -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		PageParam param = (PageParam)obj;
		boolean result = param.name.equals(this.name);
		return result;
	}
	
	public static void main(String[] args) {
		ArrayList<PageParam> list = new ArrayList<PageParam>();
		PageParam param = new PageParam("p1", null, null, null);
		list.add(param);

		param = new PageParam("p2", null, null, null);
		list.add(param);

		param = new PageParam("p3", null, null, null);
		list.add(param);
		
		PageParam param2 = new PageParam("p1", null, null, null);
		
		int index = list.indexOf(param2);
		System.out.println(index);
		
	}
}
