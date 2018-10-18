package ui

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

class ShowSqlDlg
/**
 * Create the dialog.
 *
 * @param parent
 * @param style
 */
(parent: Shell, style: Int, protected var code: String) : Dialog(parent, style) {

    protected var result: Any? = null
    protected var shlSql: Shell? = null

    init {
        this.text = "SQL显示窗口"
    }

    /**
     * Open the dialog.
     *
     * @return the result
     */
    fun open(): Any? {
        this.createContents()
        this.shlSql!!.open()
        this.shlSql!!.layout()
        val display = this.parent.display
        while (!this.shlSql!!.isDisposed) {
            if (!display.readAndDispatch()) {
                display.sleep()
            }
        }
        return this.result
    }

    /**
     * Create contents of the dialog.
     */
    private fun createContents() {
        this.shlSql = Shell(this.parent, this.style)
        this.shlSql!!.setSize(675, 578)
        this.shlSql!!.text = "SQL显示窗口"

        val codeText = StyledText(this.shlSql, SWT.BORDER)
        codeText.setBounds(10, 10, 649, 471)
        codeText.text = this.code

        val btnClose = Button(this.shlSql, SWT.NONE)
        btnClose.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent?) {
                shlSql!!.close()
            }
        })
        btnClose.setBounds(272, 494, 114, 34)
        btnClose.text = "关闭(&C)"

    }
}
