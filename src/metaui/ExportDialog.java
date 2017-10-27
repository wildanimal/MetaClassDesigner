package metaui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import meta.DrawBoard;
import service.OrmService;
import util.Consts;
import util.MetaMap;

public class ExportDialog extends Dialog {
	
	public DrawBoard board;
	List<MetaMap> options = null;
	List<MetaMap> classes = null;
	
	Group gOptions = null;
	Group gClasses = null;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ExportDialog(Shell parentShell, DrawBoard board) {
		super(parentShell);
		this.board = board;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
//		parent.setBackground(ColorConstants.lightBlue);
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(container, SWT.NONE);
		
		SashForm sashForm_1 = new SashForm(sashForm, SWT.VERTICAL);
		
		Button cbOptions = new Button(sashForm_1, SWT.CHECK);
		cbOptions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control c : gOptions.getChildren()) {
					Button b = (Button) c;
					b.setSelection(cbOptions.getSelection());
				}
			}
		});
		cbOptions.setText("全选/清空选项");
		
		ScrolledComposite scOptions = new ScrolledComposite(sashForm_1, SWT.BORDER | SWT.V_SCROLL);
		scOptions.setExpandHorizontal(true);
		scOptions.setExpandVertical(true);
		scOptions.setMinWidth(300);
		scOptions.setMinHeight(1000);
		
		gOptions = new Group(scOptions, SWT.NONE);
		scOptions.setContent(gOptions);
		gOptions.setSize(300, 1000);
		gOptions.setLayout(new GridLayout(1, false));
		
		gOptions.setText("选项：");
		sashForm_1.setWeights(new int[] {20, 200});
		
		SashForm sashForm_2 = new SashForm(sashForm, SWT.VERTICAL);
		
		Button cbClasses = new Button(sashForm_2, SWT.CHECK);
		cbClasses.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control c : gClasses.getChildren()) {
					Button b = (Button) c;
					b.setSelection(cbOptions.getSelection());
				}
			}
		});
		cbClasses.setText("全选/清空类");
		
		
		ScrolledComposite scClasses = new ScrolledComposite(sashForm_2, SWT.BORDER | SWT.V_SCROLL);
		scClasses.setExpandHorizontal(true);
		scClasses.setExpandVertical(true);
		scClasses.setMinWidth(300);
		scClasses.setMinHeight(1000);
		
		gClasses = new Group(scClasses, SWT.NONE);
		scClasses.setContent(gClasses);
		gClasses.setSize(300, 1000);
		gClasses.setLayout(new GridLayout(1, false));
		
		gClasses.setText("类列表：");
		sashForm_2.setWeights(new int[] {20, 200});
		
		sashForm.setWeights(new int[] {1, 1});
		
		List<MetaMap> classes2 = board.opackage.listmap("classes");
		MetaMap optionsClass = null;
		for (MetaMap clazz : classes2) {
			if (clazz.name().equals("Options")) {
				optionsClass = clazz;
				break;
			}
		}
		
		options = optionsClass.listmap("fields");
		for (MetaMap option : options) {
			Button b = new Button(gOptions, SWT.CHECK);
			b.setData(option.name());
			b.setText(option.label() + "(" + option.name() + ")");
			b.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
					false, 1, 1));
		}
		
		classes = board.getModel().listmap("classes");
		for (MetaMap clazz : classes) {
			if (clazz.bool("outside")) {
				continue;
			}

			Button b = new Button(gClasses, SWT.CHECK);
			String className = clazz.name();
			if (clazz.bool("outside")) {
				className = clazz.str("pkg") + "." + className;
			}
			b.setData(clazz.name());
			b.setText(clazz.label() + "(" + className + ")");
			b.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
					false, 1, 1));
		}

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		final Dialog self = this; 
		Button button = createButton(parent, IDialogConstants.OK_ID, "确定",
				true);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(self.getShell());

				String save_dir = Consts.config.getProperty("save_dir");
				if ("".equals(save_dir))
					save_dir = "c:/";
				dlg.setFilterPath(save_dir);
				dlg.setText("选择输出目录");
				
				if ( (save_dir = dlg.open()) != null) {
					List<MetaMap> selectedOptions = new ArrayList<MetaMap>();
					List<String> selectedClassNames = new ArrayList<String>();
					int i = 0;
					for (Control c : gOptions.getChildren()) {
						Button b = (Button) c;
						if (b.getSelection()) {
							selectedOptions.add(options.get(i));
						}
						i++;
					}
					
					i = 0;
					classes = board.getModel().listmap("classes");
					for (Control c : gClasses.getChildren()) {
						Button b = (Button) c;
						if (b.getSelection()) {
							selectedClassNames.add((String)b.getData());
						}
					}
					try {
						Consts.config.setProperty("save_dir", save_dir);
						Consts.storeConfig();
						OrmService.save(board.getModel(), selectedClassNames, selectedOptions, save_dir + "/");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				self.close();
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID,
				"取消", false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(700, 500);
	}

}
