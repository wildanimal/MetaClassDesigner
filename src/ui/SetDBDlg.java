package ui;

import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import util.Consts;

public class SetDBDlg extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text urlText;
	private Text userText;
	private Text pwdText;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SetDBDlg(Shell parent, int style) {
		super(parent, style);
		setText("设定数据库");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 280);
		shell.setText("设置数据库");
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("数据库链接：");
		label.setBounds(10, 24, 114, 24);
		
		urlText = new Text(shell, SWT.BORDER);
		urlText.setText(Consts.DBINI.getProperty("url"));
		urlText.setBounds(130, 21, 297, 30);
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("用户名：");
		label_1.setBounds(10, 82, 131, 24);
		
		userText = new Text(shell, SWT.BORDER);
		userText.setText(Consts.DBINI.getProperty("username"));
		userText.setBounds(130, 79, 297, 30);
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("密码：");
		label_2.setBounds(10, 132, 131, 24);
		
		pwdText = new Text(shell, SWT.BORDER);
		pwdText.setText(Consts.DBINI.getProperty("password"));
		pwdText.setBounds(130, 129, 297, 30);
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Consts.DBINI.put("url", urlText.getText());
				Consts.DBINI.put("username", userText.getText());
				Consts.DBINI.put("password", pwdText.getText());
				try {
					Consts.DBINI.store(new FileOutputStream(Consts.DBINI_FILE), "");
					shell.dispose();
					
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
				}
				
			}
		});
		btnOk.setBounds(10, 193, 114, 34);
		btnOk.setText("保存(&S)");
		
		Button btnc = new Button(shell, SWT.NONE);
		btnc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnc.setText("关闭(&C)");
		btnc.setBounds(147, 193, 114, 34);

	}
}
