package metaui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import meta.DrawBoard;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import util.MetaMap;

public class FieldsForm extends Composite {
	public String name = "";

	public MetaMap oclass = null;
	public List<MetaMap> fields = new ArrayList<MetaMap>();

	public MetaMap model = null;

	public DrawBoard board;
	public TableViewer openGrid;

	public boolean inited = false;
	
	public Button okBtn = null;

	public List<Control> editors = new ArrayList<Control>();

	public TableViewer currGrid = null;
	public MetaMap currSelectRow = null;

	public List<TableViewer> grids = new ArrayList<TableViewer>();
	public LinkedHashMap<String, TableViewer> gridmap = new LinkedHashMap<String, TableViewer>();


	public static boolean checkCurrGrid(final FieldsForm self) {
		if (self.currGrid == null) {
			MessageBox messageBox = new MessageBox(
				self.getShell(), SWT.ICON_WARNING | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
	        
	        messageBox.setText("提示");
	        messageBox.setMessage("请先左键点击一下表格，再使用右键菜单");
	        messageBox.open();
	        return false;
		}
		
		return true;
	}
	
	Menu gridMenu = null;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public FieldsForm(Composite parent, int style) {
		super(parent, SWT.NONE);
		final FieldsForm self = this;
//		addControlListener(new ControlAdapter() {
//			@Override
//			public void controlResized(ControlEvent e) {
//				int width = self.getSize().x;
//				System.out.println("width = " + width);
//				for (Control c : self.editors.values()) {
//					if (c instanceof Button)
//						continue;
//					c.setSize(width, c.getSize().y);
//				}
//			}
//		});
		setLayout(new GridLayout(2, false));

		Label fieldNameLabel = new Label(this, SWT.NONE);
		fieldNameLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		fieldNameLabel.setText("属性名");

		Label fieldValLabel = new Label(this, SWT.NONE);
		fieldValLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		fieldValLabel.setText("属性值");
		
		gridMenu = new Menu(this);
		//setMenu(gridMenu);
		
		MenuItem menuItemAdd = new MenuItem(gridMenu, SWT.NONE);
		menuItemAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!checkCurrGrid(self)) return;
				
				((List<MetaMap>)currGrid.getInput()).add(
					new MetaMap().append("id", UUID.randomUUID().toString()));
				
				currGrid.refresh();
			}
		});
		menuItemAdd.setImage(SWTResourceManager.getImage(
				FieldsForm.class, "/img/add.gif"));
		menuItemAdd.setText("添加");
		
		
		MenuItem menuItemDel = new MenuItem(gridMenu, SWT.NONE);
		menuItemDel.setImage(SWTResourceManager.getImage(
				FieldsForm.class, "/img/del.gif"));
		menuItemDel.setText("删除");
		menuItemDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!checkCurrGrid(self)) return;
				if (currSelectRow != null) {
					((List<MetaMap>)currGrid.getInput()).remove(currSelectRow);
					
					currGrid.refresh();
				}
			}
		});
		
		MenuItem menuItemUp = new MenuItem(gridMenu, SWT.NONE);
		menuItemUp.setImage(SWTResourceManager.getImage(
				FieldsForm.class, "/img/up.gif"));
		menuItemUp.setText("上移");
		menuItemUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!checkCurrGrid(self)) return;
				if (currSelectRow != null) {
					List<MetaMap> list = ((List<MetaMap>)currGrid.getInput());
					int idx = list.indexOf(currSelectRow);
					
					if (idx > 0) {
						MetaMap prevRow = list.get(idx - 1);
						list.set(idx, prevRow);
						list.set(idx - 1, currSelectRow);
					}
					
					currGrid.refresh();
				}
			}
		});
		
		MenuItem menuItemDown = new MenuItem(gridMenu, SWT.NONE);
		menuItemDown.setImage(SWTResourceManager.getImage(
				FieldsForm.class, "/img/down.gif"));
		menuItemDown.setText("下移");
		menuItemDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!checkCurrGrid(self)) return;
				if (currSelectRow != null) {
					List<MetaMap> list = ((List<MetaMap>)currGrid.getInput());
					int idx = list.indexOf(currSelectRow);
					
					if (idx < list.size() - 1) {
						MetaMap nextRow = list.get(idx + 1);
						list.set(idx, nextRow);
						list.set(idx + 1, currSelectRow);
					}
					
					currGrid.refresh();
				}
			}
		});
	}

	public void setModel(MetaMap m) {
		model = m;
		
		if (m.id() == null){
			m.put("id", UUID.randomUUID().toString());
		}

		initOClass();

		int i = 0;
		for (MetaMap field : fields) {
			String val = model.str(field.name());
			if (val == null)
				val = "";
			
			Control c = editors.get(i++);
			switch (field.str("uitype")) {
			case "TextBox":
				((Text) c).setText(val);
				break;
			case "CheckBox":
				if (model.isTrue(field.name())) {
					((Button)c).setSelection(true);
				} else {
					((Button)c).setSelection(false);
				}
				break;
			case "ComboBox":
			case "ListBox":
				Object fieldVal = model.get(field.name());
//				System.out.println("field value = " + fieldVal);
				
				List<MetaMap> datalist = field.listmap("datalist");

				String label = null;
				for (MetaMap data : datalist) {
					if (data.str("data").equals(fieldVal)) {
						label = data.label();
//						System.out.println("field label = " + label);
						break;
					}
				}
				
				
				Combo cb = ((Combo) c);
				if (label != null) {
					cb.setText(label);
				} else {
					cb.setText("");
					cb.clearSelection();
					cb.deselectAll();
				}
				break;
			case "Number":
				((Text) c).setText(val);
				break;
			case "TextArea":
				((StyledText) c).setText(val);
				break;
			}
		}

		MetaMap parent = oclass;
		while (parent != null) {
			for (MetaMap route : parent.listmap("one2manys")) {
				if (route.isTrue("bind")) { // 勾上了关联的勾
					if (!model.containsKey(route.name()))
						model.put(route.name(), new ArrayList<MetaMap>());
					TableViewer grid = gridmap.get(route.name());
					grid.setInput(model.get(route.name()));
					// FIXME 动态计算高度
				}
			}

			parent = (MetaMap) parent.get("superClass");
		}

		layout();
	}

	public void initOClass() {
		if (oclass == null || model == null || inited)
			return;

		inited = true;

		final Shell shell = this.getShell();

		MetaMap parent = oclass;
		while (parent != null) {
			for (MetaMap field : parent.listmap("fields")) {
				if ("Hidden".equals(field.str("uitype"))) {
					continue;
				}
				fields.add(field);
			}
			parent = (MetaMap) parent.get("superClass");
		}
		for (MetaMap field : fields) {
			Label label = new Label(this, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
					false, 1, 1));
			if (field.isTrue("required")) {
				label.setForeground(ColorConstants.red);
				label.setText("*" + field.label());
			} else {
				label.setText(field.label());
			}
			
			switch (field.str("uitype")) {
			case "CheckBox":
				Button check = new Button(this, SWT.CHECK);
				check.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
						false, 1, 1));
				editors.add(check);
				break;
			case "ComboBox":
			case "ListBox":
				List<MetaMap> datalist = field.datalist();
				String[] labels = new String[datalist.size()];
				int i = 0;
				for (MetaMap data : datalist) {
					labels[i++] = data.label();
				}

				Combo cb = new Combo(this, SWT.BORDER | SWT.READ_ONLY);
				cb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
						false, 1, 1));
				cb.setItems(labels);
				editors.add(cb);
				break;
			case "Number":
				Text numText = new Text(this, SWT.BORDER);
				numText.setSize(200, 30);
				numText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
						false, 1, 1));
				editors.add(numText);
				break;
			case "TextArea":
				StyledText textArea = new StyledText(this, SWT.BORDER);
				textArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
						false, 1, 1));
				editors.add(textArea);
				break;
			case "TextBox":
			default:
				Text text = new Text(this, SWT.BORDER);
				text.setSize(400, 30);
				text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
						false, 1, 1));
				editors.add(text);
				break;
			}
		}

		parent = oclass;
		while (parent != null) {
			for (MetaMap route : parent.listmap("one2manys")) {
				if (route.isTrue("bind")) {

					final MetaMap toClass = route.map("toClass");

					Label label = new Label(this, SWT.NONE);
					label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
							true, false, 1, 1));
					label.setText(toClass.label() + "列表：");

					final TableViewer grid = new TableViewer(this, SWT.BORDER
							| SWT.FULL_SELECTION);
					Table table = grid.getTable();
					table.setLinesVisible(true);
					table.setHeaderVisible(true);
					table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
							true, 2, 1));

					if (!model.containsKey(route.name()))
						model.put(route.name(), new ArrayList<MetaMap>());

					// FIXME 弹出菜单

					gridmap.put(route.name(), grid);
					grids.add(grid);

					ArrayList<MetaMap> rfields = new ArrayList<MetaMap>();
					MetaMap toParent = toClass;
					while (toParent != null) {
						for (MetaMap rfield : toParent.listmap("fields")) {
							if ("Hidden".equals(rfield.str("uitype"))) {
								continue;
							}
							rfields.add(rfield);
						}
						toParent = (MetaMap) toParent.get("superClass");
					}
					for (MetaMap rfield : rfields) {
						TableColumn col = new TableColumn(table, SWT.NONE);
						col.setWidth(100);
						col.setText(rfield.label());
					}
					grid.setContentProvider(new ListTreeContentProvider());
					grid.setLabelProvider(new FieldLabelProvider(rfields));

					grid.addDoubleClickListener(new IDoubleClickListener() {
						@Override
						public void doubleClick(DoubleClickEvent e) {
							FieldsDialog cd = new FieldsDialog(
								shell, board, grid, toClass);
							Object o = ((StructuredSelection) e.getSelection())
									.getFirstElement();
							MetaMap map = (MetaMap) o;
							cd.setModel(map);

							cd.open();
						}
					});
					grid.getTable().setMenu(gridMenu);
					grid.getTable().addMenuDetectListener(new MenuDetectListener() {
						@Override
						public void menuDetected(MenuDetectEvent e) {
							currGrid = grid;
						}
						
					});
					grid.addSelectionChangedListener(
							new ISelectionChangedListener() {
						@Override
						public void selectionChanged(SelectionChangedEvent e) {
							currGrid = grid;
							currSelectRow = (MetaMap)((StructuredSelection)e.getSelection()).getFirstElement();
						}
						
					});
//					grid.getTable().addMenuDetectListener(new MenuDetectListener() {
//						@Override
//						public void menuDetected(MenuDetectEvent event) {
//					        Table t = (Table) event.widget;  
//					        Point pt = t.getDisplay().map(null, t, event.x, event.y);  
//					        Rectangle clientArea = t.getClientArea();  
//					        t.setMenu(isHeader ? headerMenu : bodynMenu);  
//						}
//					});
					
				}
			}
			parent = (MetaMap) parent.get("superClass");
		}

		okBtn = new Button(this, SWT.PUSH);
		okBtn.setText("确定");
		okBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1));
		okBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
				ok();
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void ok() {
		int i = 0;
		for (MetaMap field : fields) {

			Control c = editors.get(i++);
			switch (field.str("uitype")) {
			case "TextBox":
				model.put(field.name(), ((Text) c).getText().trim());
				break;
			case "CheckBox":
				Button b = (Button) c;
				model.put(field.name(), b.getSelection());
				break;
			case "ComboBox":
			case "ListBox":
				List<MetaMap> datalist = field.datalist();
				String[] labels = new String[datalist.size()];
				int j = 0;
				for (MetaMap data : datalist) {
					labels[j++] = data.label();
				}

				Combo cb = (Combo) c;
				for (MetaMap data : datalist) {
					if (cb.getText().equals(data.label())) {
						model.put(field.name(), data.get("data"));
					}
				}
				break;
			case "Number":
				model.put(field.name(), ((Text) c).getText().trim());
				break;
			case "TextArea":
				model.put(field.name(), ((StyledText) c).getText().trim());
				break;
			}
		}
		
		board.addUndo();
		
		board.drawSelects();
		
		if (openGrid != null) {
			openGrid.refresh();
		}

		board.refreshOutline();
	}
}
