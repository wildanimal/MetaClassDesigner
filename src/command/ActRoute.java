package command;

import java.lang.reflect.Constructor;

import meta.DrawBoard;
import meta.MLine;
import meta.MShape;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Point;

/**
 * 画路由线
 */
public class ActRoute extends Command {
	public static Boolean isme(String cmd) {
		return cmd.indexOf("Route") != -1;
	}

	public static String tome() {
		return "Route";
	}

	public static void start(MShape figure, MouseEvent e) {
		// trace("start line ");
		// figure.board.cmd = tome();
		figure.board.fromShape = figure;
		createLine(figure.board);

		figure.board.line.calcFromPoint(e.x, e.y);
		
		figure.board.routeShape.shape.setLocation(e.getLocation());

	}

	public static void exec(DrawBoard board, MouseEvent e) {
		// trace("draw line ");
		createLine(board);
//		System.out.println(e.x + "," + e.y);
		Point p = e.getLocation();
		p.x -= 5;
		p.y -= 5;
		board.routeShape.shape.setLocation(p);
		board.line.calcSize(p.x , p.y);
		board.line.drawShape();
		board.line.invalidate();
	}

	public static void createLine(DrawBoard board) {
		if (board.line == null) {
			Class clazz = board.reg.get(board.cmd);
			try {
				Constructor c = clazz
						.getConstructor(DrawBoard.class);
				board.line = (MLine) c.newInstance(board);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// FIXME z-Index
			// board.setChildIndex(board.line, 0);
			board.line.fromid = board.fromShape;
			board.line.toid = board.routeShape;
		}
	}

	public static void end(DrawBoard board, MouseEvent e) {
		// trace("finish line ");
		board.cmd = "select";

		if (board.line != null && board.fromShape != null) {
			if (board.toShape != null) {
				MLine route = board.line;

				if (board.fromShape == board.toShape) {
					route.model.put("shape", "VertRect");
					route.model.put("offset", -100);
				}

				// if (board.toFigure.canIn(route, board.fromFigure)
				// && board.fromFigure.canOut(route, board.toFigure)){
				route.setFromTo(board.fromShape, board.toShape);
				route.calcSize(e.x, e.y);
				route.drawShape();
				route.invalidate();
				
				//board.addFigure(route);

					board.refreshOutline();

				board.addUndo();
				board.cmd = "select";
			} else {
				if (board.line != null) {
					board.removeFigure(board.line);
				}
			}
		} else {
			if (board.line != null) {
				board.removeFigure(board.line);
			}
		}
		board.line = null;
		board.fromShape = null;
		board.toShape = null;
		board.hideDocks();
		
		board.refreshOutline();
	}
}
