package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 比较数据库表与当前图形中的一个类
 * @author peter
 *
 */
public class CmpTableDlg extends Dialog {

	protected Object result;
	protected Shell shlSql;
	protected String code;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CmpTableDlg(Shell parent, int style, String code) {
		super(parent, style);
		setText("数据库比较窗口");
		this.code = code;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlSql.open();
		shlSql.layout();
		Display display = getParent().getDisplay();
		while (!shlSql.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlSql = new Shell(getParent(), getStyle());
		shlSql.setSize(675, 578);
		shlSql.setText("SQL显示窗口");
		
		StyledText codeText = new StyledText(shlSql, SWT.BORDER);
		codeText.setBounds(10, 10, 649, 471);
		codeText.setText(code);
		
		Button btnClose = new Button(shlSql, SWT.NONE);
		btnClose.setBounds(272, 494, 114, 34);
		btnClose.setText("关闭(&C)");
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				shlSql.close();
			}
		});

	}
}
