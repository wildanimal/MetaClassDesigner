package ui

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.*
import orm.Configuration
import orm.Session
import orm.SessionFactory
import util.Consts
import org.eclipse.jface.util.Geometry.setLocation
import javax.swing.Spring.height



class UpdateDbChooseDlg
/**
 * Create the dialog.
 * @param parent
 * @param style
 */
(parent: Shell, style: Int) : Dialog(parent, style) {

    protected var result: Any? = null
    protected var shell: Shell? = null
    private var urlText: Text? = null
    private var userText: Text? = null
    private var pwdText: Text? = null

    init {
        text = "更新库表"
    }

    /**
     * Open the dialog.
     * @return the result
     */
    fun open(): Any? {
        createContents()
        val bounds = shell!!.display.primaryMonitor.bounds
        val rect = shell!!.bounds

        val x = bounds.x + (bounds.width - rect.width) / 2
        val y = bounds.y + (bounds.height - rect.height) / 2

        shell?.setLocation(x, y)

        shell?.open()
        shell?.layout()
        val display = parent.display
        while (!shell!!.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
        return result
    }

    /**
     * Create contents of the dialog.
     */
    private fun createContents() {
        shell = Shell(parent, style)
        shell?.setSize(450, 280)
        shell?.text = "更新库表"

        val label = Label(shell, SWT.NONE)
        label.text = "数据库链接："
        label.setBounds(10, 24, 114, 24)

        var dbUrl = Consts.config.getProperty("dbUrl")
        if (dbUrl.isNullOrBlank()) {
            dbUrl = "localhost:3306/db"
        }
        urlText = Text(shell, SWT.BORDER)
        urlText!!.text = dbUrl
        urlText!!.setBounds(150, 21, 260, 30)

        val label_1 = Label(shell, SWT.NONE)
        label_1.text = "用户名："
        label_1.setBounds(10, 82, 131, 24)

        var dbUser = Consts.config.getProperty("dbUser")
        if (dbUser.isNullOrBlank()) {
            dbUser = "root"
        }
        userText = Text(shell, SWT.BORDER)
        userText!!.text = dbUser
        userText!!.setBounds(150, 79, 260, 30)

        val label_2 = Label(shell, SWT.NONE)
        label_2.text = "密码："
        label_2.setBounds(10, 132, 131, 24)

        var dbPwd = Consts.config.getProperty("dbPwd")
        if (dbPwd.isNullOrBlank()) {
            dbPwd = "123"
        }
        pwdText = Text(shell, SWT.BORDER)
        pwdText!!.text = dbPwd
        pwdText!!.setBounds(150, 129, 260, 30)

        val btnOk = Button(shell, SWT.NONE)
        btnOk.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                val cfg = Configuration()
                cfg.dialect = "mysql"
                cfg.driver = "com.mysql.jdbc.Driver"//driverText.getText();
                cfg.url = "jdbc:mysql://" + urlText!!.text + "?useUnicode=true&characterEncoding=UTF8&jdbcCompliantTruncation=false"
                cfg.username = userText!!.text
                cfg.password = pwdText!!.text

                Consts.config.setProperty("dbUrl", urlText!!.text)
                Consts.config.setProperty("dbUser", userText!!.text)
                Consts.config.setProperty("dbPwd", pwdText!!.text)
                Consts.storeConfig()

                var sf: SessionFactory? = null
                var s: Session? = null
                try {
                    sf = cfg.buildSessionFactory()

                    val work = sf!!.dialect.createSchemaWork(AppMain2.board.getModel())

                    s = sf.openSession()
                    s!!.doWork(work)

                    shell?.dispose()

                    val dlg = ShowSqlDlg(parent, style, work.sql)
                    dlg.open()
                } catch (e1: Exception) {
                    e1.printStackTrace()
                } finally {
                    s?.close()

                    //					if (sf != null)
                    //						sf.close();
                }

            }
        })
        btnOk.setBounds(10, 193, 114, 34)
        btnOk.text = "更新库表(&G)"

        val btnc = Button(shell, SWT.NONE)
        btnc.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                shell?.close()
            }
        })
        btnc.text = "关闭(&C)"
        btnc.setBounds(147, 193, 114, 34)

    }
}
