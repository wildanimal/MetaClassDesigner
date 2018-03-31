package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ShowSqlDlg extends Dialog {

	protected Object result;
	protected Shell shlSql;
	protected String code;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ShowSqlDlg(Shell parent, int style, String code) {
		super(parent, style);
		this.setText("SQL显示窗口");
		this.code = code;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		this.createContents();
		this.shlSql.open();
		this.shlSql.layout();
		Display display = this.getParent().getDisplay();
		while (!this.shlSql.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return this.result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		this.shlSql = new Shell(this.getParent(), this.getStyle());
		this.shlSql.setSize(675, 578);
		this.shlSql.setText("SQL显示窗口");

		StyledText codeText = new StyledText(this.shlSql, SWT.BORDER);
		codeText.setBounds(10, 10, 649, 471);
		codeText.setText(this.code);

		Button btnClose = new Button(this.shlSql, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShowSqlDlg.this.shlSql.close();
			}
		});
		btnClose.setBounds(272, 494, 114, 34);
		btnClose.setText("关闭(&C)");

	}
}
