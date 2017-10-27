package ui;

import java.util.HashMap;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

public class MyCellModifier implements ICellModifier {
	private TableViewer tv;
	public MyCellModifier(TableViewer tv) {
		this.tv = tv;
	}

	public boolean canModify(Object element, String property) {
		//System.out.println("getValue: " + property);
		return true;
	}

	public Object getValue(Object element, String property) {
		//System.out.println("getValue: " + property);
		return "";
	}

	private int getNameIndex(String name) {
		//System.out.println("getNameIndex: " + name);
		return 0;
	}

	public void modify(Object element, String property, Object value) {
		//System.out.println("getNameIndex: " + property + "=" + value);
		TableItem item = (TableItem) element;
		HashMap<String, Object> p = (HashMap<String, Object>) item.getData();
		tv.update(p, null);
	}

}