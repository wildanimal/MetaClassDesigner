package command;

import org.eclipse.draw2d.MouseEvent;

import meta.*;

public class ActRouteToPoint extends Command {
	public static Number startX = 0;

	public static Number startY = 0;

	public static Boolean move = false;

	public static String tome() {
		return "RouteToPoint";
	}

	public static Boolean isme(String cmd) {
		return cmd.indexOf("RouteToPoint") != -1;
	}

	public static void start(MLine line, MouseEvent e) {
		line.board.cmd = "RouteToPoint";
		line.board.line = line;
		startX = e.x;
		startY = e.y;
	}

	public static void exec (DrawBoard board) {
			board.line.model.put("toPoint", "");
			
			move = true;
			board.line.invalidate();
		}

	public static void end(DrawBoard board, MouseEvent e) {
		move = false;
		board.line.calcToPoint(e.x, e.y);
		board.line.invalidate();
		board.addUndo();
		SingleSelect.exec(board.line, true);
		board.line = null;
		board.cmd = Select.tome();
		board.hideDocks();
	}
}
