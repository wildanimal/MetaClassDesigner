package ui;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

import util.Consts;
import util.MetaMap;
import util.NatualJsonDecode;

public class FieldEditingSupport extends EditingSupport 
{
	public MetaMap field; 
	public Table table; 
    public FieldEditingSupport(ColumnViewer viewer, Table table, MetaMap field) {
        super(viewer);
        this.field = field;
        this.table = table;
    }
    protected CellEditor getCellEditor(Object element) {
    	CellEditor editor = null;
    	switch (field.str("uitype")) {
		case "TextBox":
			editor = new TextCellEditor(table);
		break;
		case "TextArea":
			editor = new TextCellEditor(table);
		break;
		case "Number":
			editor = new TextCellEditor(table);
		break;
		case "CheckBox":
			editor = new CheckboxCellEditor(table);
		break;
		case "ComboBox":
		case "ListBox":
			List<MetaMap> datalist = (List<MetaMap>) 
				NatualJsonDecode.fromJson(field.str("datasource"), Consts.ListMetaMapType);
			String[] labels = new String[datalist.size()];
			int i = 0;
			for (MetaMap data : datalist) {
				labels[i++] = data.label();
			}
			editor = new ComboBoxCellEditor(
				table, labels);
			
		break;
    	}
        return editor;
    }
    protected boolean canEdit(Object element) {
        return true;
    }
    protected Object getValue(Object element) {
    	if((element instanceof MetaMap)) {
			MetaMap data = (MetaMap)element;
			return data.get(field.name());
        }
        return "";
    }
    protected void setValue(Object element, Object value) 
    {
        if((element instanceof MetaMap)) {
			MetaMap data = (MetaMap)element;
			data.put(field.name(), value);
			
            getViewer().update(element, null);
        }
    }
}