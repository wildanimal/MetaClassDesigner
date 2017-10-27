package ui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import metaui.FieldLabelProvider;
import metaui.ListTreeContentProvider;
import metaui.Util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import util.MetaMap;

public class MyApp3 {
	private Table table;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MyApp3 window = new MyApp3();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Map<String, Dialog> dialogs = new HashMap<String, Dialog>();

	/**
	 * Open the window.
	 */
	public void open() throws Exception {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(812, 522);
		shell.setText("SWT Application");
		
		shell.setBackgroundImage(Util.transparentImage);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		MetaMap ormPkg = MetaMap.load(new File(System.getProperty("user.dir") + "/orm/bsn.orm.orm"));
		List<MetaMap> classes = ormPkg.listmap("classes");
		MetaMap ormOClass = null;
		for (MetaMap clazz : classes) {
			if (clazz.name().equals("OField")) {
				ormOClass= clazz;
				break;
			}
		}
		List<MetaMap> fields = ormOClass.listmap("fields");
		
		TableViewer tableViewer = new TableViewer(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        Menu menu = new Menu(table);
        table.setMenu(menu);
        
        MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
        itemAdd.setImage(SWTResourceManager.getImage(MyApp3.class, "/img/add.gif"));
        itemAdd.setText("添加行");
        
        MenuItem itemEdit = new MenuItem(menu, SWT.NONE);
        itemEdit.setImage(SWTResourceManager.getImage(MyApp3.class, "/img/edit.gif"));
        itemEdit.setText("修改行");
        
        MenuItem itemDel = new MenuItem(menu, SWT.NONE);
        itemDel.setImage(SWTResourceManager.getImage(MyApp3.class, "/img/del.gif"));
        itemDel.setText("减少行");
        
        MenuItem itemUp = new MenuItem(menu, SWT.NONE);
        itemUp.setImage(SWTResourceManager.getImage(MyApp3.class, "/img/up.gif"));
        itemUp.setText("上移");
        
        MenuItem itemDown = new MenuItem(menu, SWT.NONE);
        itemDown.setImage(SWTResourceManager.getImage(MyApp3.class, "/img/down.gif"));
        itemDown.setText("下移");
        
        for (MetaMap field : fields) {
        	TableColumn col = new TableColumn(table, SWT.NONE);
        	col.setWidth(100);
        	col.setText(field.label());
        }
        tableViewer.setContentProvider(new ListTreeContentProvider());
        tableViewer.setLabelProvider(new FieldLabelProvider(fields));
        
        final List<MetaMap> data = fields;
        tableViewer.setInput(data);
        
        tableViewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent e) {
				TableViewer tv = (TableViewer)e.getViewer();
				ISelection sel = tv.getSelection();
			}
        	
        });

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
