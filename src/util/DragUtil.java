package util;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;

public class DragUtil {
	public static void setDragDrop(final Composite drager) {

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_NONE;

		final DragSource source = new DragSource(drager, operations);
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				//event.doit = true;//(composite.getText().length() != 0);
			}

			public void dragSetData(DragSourceEvent event) {
				//event.data = "";//composite.getText();
			}

			public void dragFinished(DragSourceEvent event) {
				//if (event.detail == DND.DROP_MOVE)
				//	composite.setText("");
			}
		});

		DropTarget target = new DropTarget(drager, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent e) {
			}

			@Override
			public void dragLeave(DropTargetEvent e) {
			}

			@Override
			public void dragOperationChanged(DropTargetEvent e) {
				e.detail = DND.DROP_NONE;
			}

			@Override
			public void dragOver(DropTargetEvent e) {
				
			}

			@Override
			public void dropAccept(DropTargetEvent e) {
			}

			public void drop(DropTargetEvent e) {
				e.detail = DND.DROP_NONE;
			}
		});
	}
}
