package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import orm.Configuration;
import orm.Session;
import orm.SessionFactory;
import orm.Work;

public class UpdateDatabaseSchemaDlg extends Dialog {

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
	public UpdateDatabaseSchemaDlg(Shell parent, int style) {
		super(parent, style);
		setText("更新库表");
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
		shell.setText("更新库表");
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("数据库链接：");
		label.setBounds(10, 24, 114, 24);
		
		urlText = new Text(shell, SWT.BORDER);
		urlText.setText("localhost:3306/db");
		urlText.setBounds(130, 21, 297, 30);
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("用户名：");
		label_1.setBounds(10, 82, 131, 24);
		
		userText = new Text(shell, SWT.BORDER);
		userText.setText("root");
		userText.setBounds(130, 79, 297, 30);
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("密码：");
		label_2.setBounds(10, 132, 131, 24);
		
		pwdText = new Text(shell, SWT.BORDER);
		pwdText.setText("123");
		pwdText.setBounds(130, 129, 297, 30);
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Configuration cfg = new Configuration();
				cfg.dialect = "mysql";
				cfg.driver = "com.mysql.jdbc.Driver";//driverText.getText();
				cfg.url = "jdbc:mysql://" + urlText.getText() + "?useUnicode=true&characterEncoding=UTF8&jdbcCompliantTruncation=false";
				cfg.username = userText.getText();
				cfg.password = pwdText.getText();
				
				SessionFactory sf = null;
				Session s = null;
				try {
					sf = cfg.buildSessionFactory();
					
					Work work = sf.dialect.createSchemaWork(AppMain2.board.getModel());
					
					s = sf.openSession();
					s.doWork(work);
					
					shell.dispose();
					
					ShowSqlDlg dlg = new ShowSqlDlg(getParent(), getStyle(), work.getSql());
					dlg.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					if (s != null)
						s.close();
					
//					if (sf != null)
//						sf.close();
				}
				
			}
		});
		btnOk.setBounds(10, 193, 114, 34);
		btnOk.setText("更新库表(&G)");
		
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
