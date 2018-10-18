package ui

import command.Remove
import command.SingleSelect
import meta.DrawBoard
import meta.MFigure
import metaui.ExportDialog
import metaui.FigureLabelProvider
import metaui.ListTreeContentProvider
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.eclipse.draw2d.LightweightSystem
import org.eclipse.draw2d.XYLayout
import org.eclipse.draw2d.geometry.Rectangle
import org.eclipse.jface.action.Action
import org.eclipse.jface.action.MenuManager
import org.eclipse.jface.action.StatusLineManager
import org.eclipse.jface.action.ToolBarManager
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.window.ApplicationWindow
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.custom.ScrolledComposite
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.events.ControlEvent
import org.eclipse.swt.events.MouseAdapter
import org.eclipse.swt.events.MouseEvent
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.*
import org.eclipse.wb.swt.ResourceManager
import orm.OClass
import orm.OPackage
import util.Consts
import util.ListMap
import util.MetaMap

import java.io.*

class AppMain2 : ApplicationWindow(null) {
    private var openFilePath: String = ""

    private var addClassToolItem: Action? = null
    private var selectToolItem: Action? = null
    private var addRouteToolItem: Action? = null
    private var delToolItem: Action? = null
    private var undoToolItem: Action? = null
    private var redoToolItem: Action? = null
    private var saveToolItem: Action? = null
    private var saveAsToolItem: Action? = null
    private var openFileToolItem: Action? = null
    private var refreshTreeItem: Action? = null
    private var dbToClassItem: Action? = null
    private var copyItem: Action? = null
    private var xlsToClassItem: Action? = null
    private var action: Action? = null
    private var listTreeContentProvider: ListTreeContentProvider? = null

    /**
     * Create the application window.
     */
    init {
        createActions()
        addToolBar(SWT.FLAT or SWT.WRAP)
        addMenuBar()
        addStatusLine()
    }

    /**
     * Create contents of the application window.
     *
     * @param top
     */
    override fun createContents(top: Composite): Control {
        val container = Composite(top, SWT.NONE)
        container.layout = FillLayout(SWT.HORIZONTAL)

        val sashForm = SashForm(container, SWT.NONE)

        val outline = TreeViewer(sashForm, SWT.BORDER)
        val tree = outline.tree
        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseUp(e: MouseEvent?) {
//                val tree = e!!.widget as Tree
                if (tree.selectionCount > 0) {
                    val item = tree.selection[0]
                    val m = item.data as MFigure
                    //					MetaMap data = (MetaMap)item.getData();
                    SingleSelect.exec(m, true)
                }
            }
        })
        listTreeContentProvider = ListTreeContentProvider()
        outline.contentProvider = listTreeContentProvider
        outline.labelProvider = FigureLabelProvider()
        var opackage: MetaMap? = null
        try {
            opackage = MetaMap.load(File(System.getProperty("user.dir") + "/orm/bsn.orm.orm"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val scrolledComposite = ScrolledComposite(sashForm,
                SWT.BORDER or SWT.H_SCROLL or SWT.V_SCROLL)
        scrolledComposite.expandHorizontal = true
        scrolledComposite.expandVertical = true
        scrolledComposite.minWidth = 3000
        scrolledComposite.minHeight = 3000

        val canvas = Canvas(scrolledComposite, SWT.NONE)
        scrolledComposite.content = canvas
        canvas.layout = null
        val lws = LightweightSystem(canvas)
        board.outline = outline
        outline.input = board
        val xyLayout = XYLayout()
        board.shape.layoutManager = xyLayout
        board.shape.bounds = Rectangle(0, 0, 3000, 3000)
        lws.setContents(board.shape)

        val propBoard = Composite(sashForm, SWT.BORDER)
        propBoard.addControlListener(object : ControlAdapter() {
            override fun controlResized(e: ControlEvent?) {
//                val propBoard = e!!.widget as Composite
                for (c in propBoard.children) {
                    c.size = propBoard.size
                }
            }
        })
        board.propBoard = propBoard
        propBoard.layout = null

        sashForm.weights = intArrayOf(100, 400, 100)

        board.setOPackage(opackage)
        //board.setModel(opackage);

        //opackage.put("classes", null);
        //opackage.put("routes", null);
        //String json =  NatualJsonEncode.toJson(board.getModel());
        //System.out.println(json);
        println("root = " + Consts.root)
        return container
    }

    fun saveAs() {
        var save_dir = Consts.config.getProperty("save_dir")

        val dlg = FileDialog(shell, SWT.SAVE)
        dlg.filterExtensions = arrayOf("*.orm")
        dlg.text = "保存为"

        if ("" == save_dir)
            save_dir = System.getProperty("user.dir") + "/orm"

        dlg.filterPath = save_dir
        var filePath: String? = dlg.open() ?: return

        filePath = filePath!!.replace("\\\\".toRegex(), "/")
        openFilePath = filePath
        save_dir = filePath.substring(0, filePath.lastIndexOf("/"))
        Consts.config.setProperty("save_dir", save_dir)
        Consts.storeConfig()

        val json = board.getModel().toJson()
        FileOutputStream(filePath).use { fos ->
            OutputStreamWriter(fos, "utf-8").use { osw ->
                BufferedWriter(osw).use {
                    it.write(json)
                }
            }
        }
    }

    fun xlsToClass() {
        val dlg = FileDialog(shell)
        dlg.filterExtensions = arrayOf("*.xls", "*.xlsx")
        dlg.text = "打开文件"

        var openDir = Consts.config.getProperty("xls_dir")
        if (openDir.isBlank())
            openDir = System.getProperty("user.dir") + "/orm"
        dlg.filterPath = openDir
        var filePath: String? = dlg.open() ?: return

        filePath = filePath!!.replace("\\\\".toRegex(), "/")

        Consts.config.setProperty("xls_dir", filePath.substring(0, filePath.lastIndexOf("/")))
        Consts.storeConfig()

        var x = 0

        //读取xlsx文件
        val xssfWorkbook : XSSFWorkbook
        //寻找目录读取文件
        val excelFile = File(filePath)
        FileInputStream(excelFile).use { fis ->
            val workBook : XSSFWorkbook? = XSSFWorkbook(fis)
            for (sheet in workBook!!.sheetIterator() ) {
                if (sheet.count()  == 0) { // 空表
                    continue
                }

                val sheetNameArr = sheet.sheetName.split("|")

                val findOClass = AppMain2.board.getFigure(sheetNameArr[0])
                if (findOClass != null) {
                    AppMain2.board.removeFigure(findOClass)
                }

                val oclass = OClass(AppMain2.board)
                val mclass = oclass.model
                mclass["name"] = sheetNameArr[0]
                x+= 100
                mclass["x"] = x
                mclass["y"] = 10
                mclass["label"] = sheetNameArr[1]
                mclass["idname"] = "id"
                mclass["idtype"] = "Long"

                val fields = ListMap()
                mclass["fields"] = fields

                val nameRow = sheet.getRow(0)
                val typeRow = sheet.getRow(1)
                val titleRow = sheet.getRow(2)
                for (index in 0 until titleRow.count()) {
                    val ofield = MetaMap()
                    fields += ofield
                    ofield["name"] = nameRow.getCell(index).stringCellValue
                    ofield["type"] = typeRow.getCell(index).stringCellValue

                    ofield["uitype"] = "TextBox"

                    ofield["label"] = titleRow.getCell(index).stringCellValue
                }
                oclass.drawShape()
            }

            AppMain2.board.refreshOutline()
        }
    }

    /**
     * Create the actions.
     */
    private fun createActions() {
        val app = this
        // Create the actions
        run {
            addClassToolItem = object : Action("新建类") {

                override fun runWithEvent(event: Event?) {
                    board.setCmd("orm.OClass", "img/class.gif")
                }

            }
            addClassToolItem!!.toolTipText = "新建类"
            addClassToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/Class.gif")

            selectToolItem = object : Action("选择") {

                override fun runWithEvent(event: Event?) {
                    board.setCmd("select", null)
                }

            }
            selectToolItem!!.toolTipText = "选择"
            selectToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/select.gif")

            addRouteToolItem = object : Action("新建路由") {

                override fun runWithEvent(event: Event?) {
                    board.setCmd("orm.ORoute", "img/route.gif")
                }


            }
            addRouteToolItem!!.toolTipText = "新建路由"
            addRouteToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/route.gif")

            delToolItem = object : Action("删除") {
                override fun runWithEvent(event: Event?) {
                    Remove.exec(board)
                }
            }
            delToolItem!!.toolTipText = "删除"
            delToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/del.gif")

            undoToolItem = object : Action("撤销") {
                override fun runWithEvent(event: Event?) {
                    board.undo()
                }
            }
            undoToolItem!!.toolTipText = "撤销"
            undoToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/undo.gif")

            redoToolItem = object : Action("重做") {
                override fun runWithEvent(event: Event?) {
                    board.redo()
                }
            }
            redoToolItem!!.toolTipText = "重做"
            redoToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/redo.gif")

            saveToolItem = object : Action("保存") {
                override fun runWithEvent(event: Event?) {
                    val openFile = File(openFilePath)
                    if (!openFile.exists() || openFile.isDirectory) {
                        app.saveAs()
                        return
                    }
                    val filePath = openFilePath

                    val json = board.getModel().toJson()
                    FileOutputStream(filePath).use { fos ->
                        OutputStreamWriter(fos, "utf-8").use { osw ->
                            BufferedWriter(osw).use {
                                it.write(json)
                            }
                        }
                    }
                }

            }
            saveToolItem!!.toolTipText = "保存"
            saveToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/save.gif")
/*
            saveAsToolItem = object : Action("另存为") {
                override fun runWithEvent(event: Event?) {
                    app.saveAs()
                }


            }
            saveAsToolItem!!.toolTipText = "另存为"
            saveAsToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/save.gif")
*/
            openFileToolItem = object : Action("打开") {
                override fun runWithEvent(event: Event?) {
                    val dlg = FileDialog(app.shell)
                    dlg.filterExtensions = arrayOf("*.orm")
                    dlg.text = "打开文件"

                    var open_dir = Consts.config.getProperty("open_dir")
                    if ("" == open_dir)
                        open_dir = System.getProperty("user.dir") + "/orm"
                    dlg.filterPath = open_dir
                    var filePath: String? = dlg.open() ?: return

                    filePath = filePath!!.replace("\\\\".toRegex(), "/")
                    openFilePath = filePath

                    open_dir = filePath.substring(0, filePath.lastIndexOf("/"))
                    Consts.config.setProperty("open_dir", open_dir)
                    Consts.storeConfig()
                    try {
                        board.setModel(MetaMap.load(File(filePath)))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

            }
            openFileToolItem!!.toolTipText = "打开"
            openFileToolItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/open.gif")

            action = object : Action("生成") {
                override fun runWithEvent(event: Event?) {
                    val dlg = ExportDialog(app.shell, board)
                    dlg.open()
                    super.runWithEvent(event)
                }
            }
            action!!.toolTipText = "生成"
            action!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/export_wiz.gif")
        }


        refreshTreeItem = object : Action("更新库表") {
            override fun runWithEvent(event: Event?) {
                val dlg = UpdateDbChooseDlg(
                        app.shell, SWT.NONE or SWT.DIALOG_TRIM)
                dlg.open()
            }
        }
        refreshTreeItem!!.toolTipText = "更新库表"
        refreshTreeItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/refresh.gif")

        dbToClassItem = object : Action("库表转类图") {
            override fun runWithEvent(event: Event?) {
                val dlg = DbToClassDlg(
                        app.shell, SWT.NONE or SWT.DIALOG_TRIM)

                dlg.open()
            }
        }
        dbToClassItem!!.toolTipText = "库表转类图"
        dbToClassItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/newint_wiz.gif")

        copyItem = object : Action("复制") {
            override fun runWithEvent(event: Event?) {
                if (board.selects.isEmpty())
                    return

                for (figure in board.selects) {
                    if (figure.type == "OClass") {
                        val oclass = OClass(board)
                        val model = figure.model.clone() as MetaMap
                        oclass.setModel(model)
                        oclass.newId()

                        oclass.drawShape()
                    }
                }

                board.refreshOutline()
            }
        }
        copyItem!!.toolTipText = "复制"
        copyItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/copy.gif")

        xlsToClassItem = object : Action("EXCEL转类图") {
            override fun runWithEvent(event: Event?) {
                xlsToClass()
            }
        }
        xlsToClassItem!!.toolTipText = "EXCEL转类图"
        xlsToClassItem!!.imageDescriptor = ResourceManager.getImageDescriptor(AppMain2::class.java, "/img/newint_wiz.gif")

    }

    /**
     * Create the menu manager.
     *
     * @return the menu manager
     */
    override fun createMenuManager(): MenuManager {
        val menuManager = MenuManager("menu")
        menuManager.addMenuListener { arg0 -> }
        return menuManager
    }

    /**
     * Create the toolbar manager.
     *
     * @return the toolbar manager
     */
    override fun createToolBarManager(style: Int): ToolBarManager {
        // ToolBar toolBar = new ToolBar(this.getShell(), SWT.FLAT | SWT.RIGHT);
        val toolBarManager = ToolBarManager(style)
        toolBarManager.add(selectToolItem!!)
        toolBarManager.add(openFileToolItem!!)
        toolBarManager.add(saveToolItem!!)
        toolBarManager.add(saveAsToolItem!!)
        toolBarManager.add(addClassToolItem!!)
        toolBarManager.add(addRouteToolItem!!)
        toolBarManager.add(delToolItem!!)
        toolBarManager.add(undoToolItem!!)
        toolBarManager.add(redoToolItem!!)
        toolBarManager.add(action!!)
        toolBarManager.add(refreshTreeItem!!)
        toolBarManager.add(dbToClassItem!!)
        toolBarManager.add(copyItem!!)
        return toolBarManager
    }

    /**
     * Create the status line manager.
     *
     * @return the status line manager
     */
    override fun createStatusLineManager(): StatusLineManager {
        return StatusLineManager()
    }

    /**
     * Configure the shell.
     *
     * @param newShell
     */
    override fun configureShell(newShell: Shell) {
        super.configureShell(newShell)
        newShell.text = "类图设计器"
    }

    /**
     * Return the initial size of the window.
     */
    override fun getInitialSize(): Point {
        return Point(1400, 1000)
    }

    companion object {

        init {
            System.setProperty("X", "startOnFirstThread")
            //System.setProperty("file.encoding", "utf-8");
        }

        @JvmField
        var board: DrawBoard = OPackage()

        /**
         * Launch the application.
         *
         * @param args
         */
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val window = AppMain2()
                window.setBlockOnOpen(true)
                window.open()
                Display.getCurrent().dispose()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
