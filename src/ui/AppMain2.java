package ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;

import command.Remove;
import command.SingleSelect;
import meta.DrawBoard;
import meta.MFigure;
import metaui.ExportDialog;
import metaui.FigureLabelProvider;
import metaui.ListTreeContentProvider;
import orm.OPackage;
import util.Consts;
import util.Exp;
import util.MetaMap;

public class AppMain2 extends ApplicationWindow {
	
	static {
		System.setProperty("X", "startOnFirstThread");
		//System.setProperty("file.encoding", "utf-8");
	}

	static DrawBoard board = new OPackage();
	private String open_file_path = "";
	
	private Action addClassToolItem;
	private Action selectToolItem;
	private Action addRouteToolItem;
	private Action delToolItem;
	private Action undoToolItem;
	private Action redoToolItem;
	private Action saveToolItem;
	private Action saveAsToolItem;
	private Action openFileToolItem;
	private Action refreshTreeItem;
	private Action action;
	ListTreeContentProvider listTreeContentProvider = null;
	
	/**
	 * Create the application window.
	 */
	public AppMain2() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param top
	 */
	@Override
	protected Control createContents(Composite top) {
		Composite container = new Composite(top, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(container, SWT.NONE);

		TreeViewer outline = new TreeViewer(sashForm, SWT.BORDER);
		Tree tree = outline.getTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Tree tree = (Tree)e.widget;
				if (tree.getSelectionCount() > 0) {
					TreeItem item = tree.getSelection()[0];
					MFigure m = (MFigure)item.getData();
//					MetaMap data = (MetaMap)item.getData();
					SingleSelect.exec(m, true);
				}
			}
		});
		listTreeContentProvider = new ListTreeContentProvider();
		outline.setContentProvider(listTreeContentProvider);
		outline.setLabelProvider(new FigureLabelProvider());
		MetaMap opackage = null;
		try {
			opackage = MetaMap.load(new File(System.getProperty("user.dir")
					+ "/orm/bsn.orm.orm"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(3000);
		scrolledComposite.setMinHeight(3000);

		Canvas canvas = new Canvas(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(canvas);
		canvas.setLayout(null);
		LightweightSystem lws = new LightweightSystem(canvas);
		board.outline = outline;
		outline.setInput(board);
		XYLayout xyLayout = new XYLayout();
		board.shape.setLayoutManager(xyLayout);
		board.shape.setBounds(new Rectangle(0, 0, 3000, 3000));
		lws.setContents(board.shape);

		Composite propBoard = new Composite(sashForm, SWT.BORDER);
		propBoard.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Composite propBoard = (Composite)e.widget;
				for (Control c : propBoard.getChildren()) {
					c.setSize(propBoard.getSize());
				}
			}
		});
		board.propBoard = propBoard;
		propBoard.setLayout(null);

		sashForm.setWeights(new int[] { 100, 400, 100 });

		board.setOPackage(opackage);
		//board.setModel(opackage);
		
		//opackage.put("classes", null);
		//opackage.put("routes", null);
		//String json =  NatualJsonEncode.toJson(board.getModel());
		//System.out.println(json);
		System.out.println("root = " + Consts.root);
		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		final AppMain2 app = this;
		// Create the actions
		{
			addClassToolItem = new Action("新建类") {

				@Override
				public void runWithEvent(Event event) {
					board.setCmd("orm.OClass", "img/class.gif");
				}
				
			};
			addClassToolItem.setToolTipText("新建类");
			addClassToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/Class.gif"));
		}
		{
			selectToolItem = new Action("选择") {

				@Override
				public void runWithEvent(Event event) {
					board.setCmd("select", null);
				}
			};
			selectToolItem.setToolTipText("选择");
			selectToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/select.gif"));
		}
		{
			addRouteToolItem = new Action("新建路由") {

				@Override
				public void runWithEvent(Event event) {
					board.setCmd("orm.ORoute", "img/route.gif");
				}
				
			};
			addRouteToolItem.setToolTipText("新建路由");
			addRouteToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/route.gif"));
		}
		{
			delToolItem = new Action("删除") {
				@Override
				public void runWithEvent(Event event) {
					Remove.exec(board);
				}
			};
			delToolItem.setToolTipText("删除");
			delToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/del.gif"));
		}
		{
			undoToolItem = new Action("撤销") {
				@Override
				public void runWithEvent(Event event) {
					board.undo();
				}			};
			undoToolItem.setToolTipText("撤销");
			undoToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/undo.gif"));
		}
		{
			redoToolItem = new Action("重做") {
				@Override
				public void runWithEvent(Event event) {
					board.redo();
				}			};
			redoToolItem.setToolTipText("重做");
			redoToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/redo.gif"));
		}
		{
			saveToolItem = new Action("保存") {
				@Override
				public void runWithEvent(Event event) {
					if (Exp.isEmpty(open_file_path))
						return;
					String filePath = open_file_path;
					
					BufferedWriter writer = null;
					try {
						String json = app.board.getModel().toJson();
						writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
						writer.write(json);
						//app.board.setModel(MetaMap.load(new File(file)));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (writer != null) {
							try {
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
				}

			};
			saveToolItem.setToolTipText("保存");
			saveToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/save.gif"));
		}
		{
			saveAsToolItem = new Action("另存为") {
				@Override
				public void runWithEvent(Event event) {
					String save_dir = Consts.config.getProperty("save_dir");
					
					FileDialog dlg = new FileDialog(app.getShell(), SWT.SAVE);
					dlg.setFilterExtensions(new String[] {"*.orm"});
					dlg.setText("保存为");
					
					if ("".equals(save_dir))
						save_dir = System.getProperty("user.dir") + "/orm";
					
					dlg.setFilterPath(save_dir);
					String filePath = dlg.open();
					if (filePath == null)
						return;
					filePath = filePath.replaceAll("\\\\", "/");
					open_file_path = filePath;
					save_dir = filePath.substring(0, filePath.lastIndexOf("/"));
					Consts.config.setProperty("save_dir", save_dir);
					Consts.storeConfig();
					
					BufferedWriter writer = null;
					try {
						String json = app.board.getModel().toJson();
						writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
						writer.write(json);
						//app.board.setModel(MetaMap.load(new File(file)));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (writer != null) {
							try {
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
				}

			};
			saveAsToolItem.setToolTipText("另存为");
			saveAsToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/save.gif"));
		}
		{
			openFileToolItem = new Action("打开") {
				@Override
				public void runWithEvent(Event event) {
					FileDialog dlg = new FileDialog(app.getShell());
					dlg.setFilterExtensions(new String[] {"*.orm"});
					dlg.setText("打开文件");
					
					String open_dir = Consts.config.getProperty("open_dir");
					if ("".equals(open_dir))
						open_dir = System.getProperty("user.dir") + "/orm";
					dlg.setFilterPath(open_dir);
					String filePath = dlg.open();
					if (filePath == null)
						return;
					
					filePath = filePath.replaceAll("\\\\", "/");
					open_file_path = filePath;
					
					open_dir = filePath.substring(0, filePath.lastIndexOf("/"));
					Consts.config.setProperty("open_dir", open_dir);
					Consts.storeConfig();
					try {
						app.board.setModel(MetaMap.load(new File(filePath)));
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}				
			};
			openFileToolItem.setToolTipText("打开");
			openFileToolItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/open.gif"));
		}
		{
			action = new Action("生成") {
				@Override
				public void runWithEvent(Event event) {
					ExportDialog dlg = new ExportDialog(app.getShell(), app.board);
					dlg.open();
					super.runWithEvent(event);
				}
			};
			action.setToolTipText("生成");
			action.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/export_wiz.gif"));
		}
		

		refreshTreeItem = new Action("更新库表") {
			@Override
			public void runWithEvent(Event event) {
				UpdateDatabaseSchemaDlg dlg = new UpdateDatabaseSchemaDlg(
					app.getShell(), SWT.NONE | SWT.DIALOG_TRIM);
				dlg.open();
			}
		};
		refreshTreeItem.setToolTipText("更新库表");
		refreshTreeItem.setImageDescriptor(ResourceManager.getImageDescriptor(AppMain2.class, "/img/refresh.gif"));
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager arg0) {
			}
		});
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		// ToolBar toolBar = new ToolBar(this.getShell(), SWT.FLAT | SWT.RIGHT);
		ToolBarManager toolBarManager = new ToolBarManager(style);
		toolBarManager.add(selectToolItem);
		toolBarManager.add(openFileToolItem);
		toolBarManager.add(saveToolItem);
		toolBarManager.add(saveAsToolItem);
		toolBarManager.add(addClassToolItem);
		toolBarManager.add(addRouteToolItem);
		toolBarManager.add(delToolItem);
		toolBarManager.add(undoToolItem);
		toolBarManager.add(redoToolItem);
		toolBarManager.add(action);
		toolBarManager.add(refreshTreeItem);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			AppMain2 window = new AppMain2();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("类图设计器");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1400, 1000);
	}
}
