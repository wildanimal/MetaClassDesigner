package ui

import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.*
import orm.Configuration
import orm.Session
import orm.SessionFactory
import util.Consts
import org.eclipse.swt.widgets.TableItem
import orm.OClass
import util.ListMap
import util.MetaMap


class DbToClassDlg
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
        shell?.setSize(450, 700)
        shell?.text = "库表转换类图"

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

        val table = Table(shell, SWT.MULTI or SWT.FULL_SELECTION or SWT.CHECK)
        table.headerVisible = true
        table.linesVisible = true
        table.setBounds(10, 250, 400, 400)

        val tc1 = TableColumn(table, SWT.CENTER)
        tc1.data = "TABLE_NAME"
        tc1.text = "表名"
        tc1.width = 200

        val tc2 = TableColumn(table, SWT.CENTER)
        tc2.data = "TABLE_COMMENT"
        tc2.text = "备注"
        tc2.width = 200

        val btnOk = Button(shell, SWT.NONE)
        btnOk.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                val cfg = Configuration()
                cfg.dialect = "mysql"
                cfg.driver = "com.mysql.jdbc.Driver"//driverText.getText();
                cfg.url = "jdbc:mysql://" + urlText!!.text + "?useUnicode=true&characterEncoding=UTF8&jdbcCompliantTruncation=false"
                cfg.username = userText!!.text
                cfg.password = pwdText!!.text
                val dbSchema = dbUrl.substring(dbUrl.lastIndexOf("/") + 1)

                Consts.config.setProperty("dbUrl", urlText!!.text)
                Consts.config.setProperty("dbUser", userText!!.text)
                Consts.config.setProperty("dbPwd", pwdText!!.text)
                Consts.storeConfig()


                var sf: SessionFactory? = null
                var s: Session? = null
                try {
                    sf = cfg.buildSessionFactory()

                    //val work = sf!!.dialect.createSchemaWork(AppMain2.board.getModel())

                    s = sf.openSession()
                    val query = s.createSQLQuery("select * from information_schema.tables where table_schema = '$dbSchema'")
                    //s!!.doWork(work)
                    val list = query.list()
                    var item: TableItem?
                    for (row in list) {
                        item = TableItem(table, SWT.NONE)
                        item.setText(0, (row as MetaMap)["TABLE_NAME"].toString())
                        item.setText(1, row["TABLE_COMMENT"].toString())
                    }
                } catch (e1: Exception) {
                    e1.printStackTrace()
                } finally {
                    s?.close()
                }

            }
        })
        btnOk.setBounds(10, 193, 114, 34)
        btnOk.text = "获取库表(&G)"

        val btnt = Button(shell, SWT.NONE)
        btnt.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                val cfg = Configuration()
                cfg.dialect = "mysql"
                cfg.driver = "com.mysql.jdbc.Driver"//driverText.getText();
                cfg.url = "jdbc:mysql://" + urlText!!.text + "?useUnicode=true&characterEncoding=UTF8&jdbcCompliantTruncation=false"
                cfg.username = userText!!.text
                cfg.password = pwdText!!.text
                val dbSchema = dbUrl.substring(dbUrl.lastIndexOf("/") + 1)

                var changed = false
                val sf = cfg.buildSessionFactory()
                var s : Session? = null
                try {
                    s = sf.openSession()
                    for (item in table.items) {
                        if (item.checked) {
                            val tableName = item.getText(0)
                            val query = s!!.createSQLQuery("select * from information_schema.columns where table_schema = '$dbSchema' and table_name= '$tableName'")
                            val columns = query.list()
                            if (columns.isNotEmpty()) {
                                changed = true
                                val mclass = MetaMap()
                                mclass["name"] = tableName
//                                mclass["width"] = 100
//                                mclass["height"] = 100
                                mclass["x"] = 100
                                mclass["y"] = 100
                                mclass["label"] = tableName
                                mclass["id"] = "id"

                                val fields = ListMap()
                                mclass["fields"] = fields
                                for (row in columns) {
                                    val ofield = MetaMap()
                                    fields += ofield
                                    ofield["name"] = (row as MetaMap)["COLUMN_NAME"]
                                    val dt = row["DATA_TYPE"]
                                    ofield["type"] = when (dt) {
                                        "int" -> "Long"
                                        "varchar" -> "String"
                                        else -> "String"
                                    }

                                    ofield["uitype"] = "TextBox"

                                    ofield["label"] = if((row["COLUMN_COMMENT"] as String).isNullOrBlank() )
                                        row["COLUMN_NAME"] else row["COLUMN_COMMENT"]
                                }
                                val oclass = OClass(AppMain2.board)
                                oclass.setModel(mclass)
                            }
                        }
                    }
                } catch(e : Exception){

                }finally {
                    s?.close()
                }

                if (changed)
                    AppMain2.board.refreshOutline()
            }
        })
        btnt.text = "库表转类图"
        btnt.setBounds(147, 193, 114, 34)

        val btnc = Button(shell, SWT.NONE)
        btnc.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                shell?.close()
            }
        })
        btnc.text = "关闭(&C)"
        btnc.setBounds(267, 193, 114, 34)

    }
}
