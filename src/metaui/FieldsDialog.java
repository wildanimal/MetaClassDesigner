package metaui;

import meta.DrawBoard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import util.MetaMap;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class FieldsDialog extends Dialog {
	public FieldsForm fieldsForm = null;
	public DrawBoard opener = null;
	public TableViewer openGrid = null;
	
	public MetaMap oclass = null;
	public MetaMap model = null;
	
	public void setModel(MetaMap model) {
		this.model = model;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public FieldsDialog(Shell parentShell, DrawBoard opener, TableViewer openGrid, MetaMap oclass) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE);
		this.oclass = oclass;
		this.openGrid = openGrid;
		this.opener = opener;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ScrolledComposite sc = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		//sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		//sc.setMinHeight(2000);
		sc.setMinHeight(2000);
		
		fieldsForm = new FieldsForm(sc, SWT.NULL);
		sc.setContent(fieldsForm);
		fieldsForm.setSize(600, 2000);
		fieldsForm.oclass = oclass;
		fieldsForm.board = opener;
		fieldsForm.openGrid = openGrid;
		
//		parent.getChildren()[1].dispose();

		return container;
	}
	
	
	
	@Override
	public void create() {
		super.create();
		fieldsForm.setModel(model);
		fieldsForm.okBtn.setVisible(false);
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, "确定",
				true);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				fieldsForm.ok();
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
		return new Point(600, 700);
	}

}
