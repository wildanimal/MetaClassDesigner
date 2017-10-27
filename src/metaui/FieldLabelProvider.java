package metaui;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import util.Exp;
import util.MetaMap;

public class FieldLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	public List<MetaMap> fields = null;

	public FieldLabelProvider(List<MetaMap> fields) {
		this.fields = fields;
	}

	public String getColumnText(Object element, int columnIndex) {
		HashMap<String, Object> datapack = (HashMap<String, Object>) element;
		try {
			MetaMap field = fields.get(columnIndex);
			Object data = datapack.get(field.name());
			String type = field.str("type");
			if (data == null) {
				if ("Boolean".equals(type)) {
					return "否";
				}
				return "";
			} else {
				if ("Boolean".equals(type)) {
					return (Boolean) data ? "是" : "否";
				} else if (!Exp.isNull(field.get("datasource"))) {
					List<MetaMap> datalist = field.datalist();
					
					for (MetaMap row : datalist) {
						if (data.equals(row.str("data")))
							return row.str("label");
					}
				}
				return data.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
}
