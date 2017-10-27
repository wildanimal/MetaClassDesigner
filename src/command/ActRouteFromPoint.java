package command;

import meta.DrawBoard;
import meta.MLine;

import org.eclipse.draw2d.MouseEvent;

public class ActRouteFromPoint extends Command {
	public static Number startX = 0;

	public static Number startY = 0;

	public static Boolean move = false;

	public static String tome() {
		return "RouteFromPoint";
	}

	public static Boolean isme(String cmd) {
		return cmd.indexOf("RouteFromPoint") != -1;
	}

	public static void start(MLine line, MouseEvent e) {
		line.board.cmd = "RouteFromPoint";
		line.board.line = line;
		startX = e.x;
		startY = e.y;
	}

	public static void exec(DrawBoard board) {
		board.line.model.put("fromPoint", "");

		move = true;
		board.line.invalidate();
	}

	public static void end(DrawBoard board, MouseEvent e) {
		move = false;

		board.line.calcFromPoint(e.x, e.y);
		board.line.invalidate();

		board.addUndo();
		SingleSelect.exec(board.line, true);
		board.line = null;
		board.cmd = Select.tome();
		board.hideDocks();
	}
}
