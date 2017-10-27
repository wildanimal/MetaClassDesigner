package command;

import java.lang.reflect.Constructor;

import meta.DrawBoard;
import meta.MFigure;

import org.eclipse.draw2d.MouseEvent;

/**
 * 添加一个图元.
 */
public class Add extends Command {
	// public static var LineTypeXML:XML = describeType(meta.Line);
	public static void exec(DrawBoard board, MouseEvent e) {
		String cmd = "";

		if (board.cmd.indexOf(".") != -1) {
			cmd = board.cmd;
		}

		if (cmd.length() == 0) {
			return;
		}

		Class clazz = board.reg.get(cmd);
		if (meta.MLine.class.isAssignableFrom(clazz)) {
			return;
		}

		// 切换的时候先保存原来的属性
		// if (board.selects.length == 1) {
		// board.selects[0].model.fromProps();
		// }
		MFigure figure = null;
		try {
			Constructor c = clazz.getConstructor(
				DrawBoard.class);
			figure = (MFigure)c.newInstance(board);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (figure != null) {
			board.addFigure(figure);
			figure.drawShape();
			board.moveShape(figure, e.x, e.y);
			SingleSelect.exec(figure, true);
			board.showSelectEffect(figure);
			board.addUndo();

			board.refreshOutline();
		}
		board.cmd = "select";
		board.removeCursor();

		board.refreshOutline();
	}
}
