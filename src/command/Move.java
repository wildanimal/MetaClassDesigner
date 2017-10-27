package command;

import meta.DrawBoard;
import meta.MFigure;
import meta.MLine;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Point;

public class Move extends Command {
	public static Boolean isme(String cmd) {
		return cmd == "move";
	}

	public static void start(MFigure figure, MouseEvent e) {
		if (figure.board.noSelect()) {
			SingleSelect.exec(figure, true);
		} else if (figure.board.hasSelect(figure)) {
			SingleSelect.exec(figure, false);
		} else {
			SingleSelect.exec(figure, true);
		}

		figure.board.hideHandlers();

		// 单选模式时才需要调整选择框
		if (figure.board.singleSelect()) {
			figure.onSelect();
			figure.board.selBox.copyBoxRect(figure);
		}

		figure.board.selBox.setVisible(true);
		figure.board.selBox.storePos(e);
		// figure.board.selBox.startDrag();
		figure.board.cmd = "move";
	}

	public static void exec(DrawBoard board, MouseEvent e) {
		Point p = board.selBox.shape.getLocation();
		p.x = board.selBox.oldX + e.x - board.selBox.oldMouseX;
		p.y = board.selBox.oldY + e.y - board.selBox.oldMouseY;
//		System.out.println(p.x + "," + p.y);
		board.selBox.shape.setLocation(p);
//		board.moveShape(board.selBox, p.x, p.y);
	}

	public static void end(DrawBoard board, MouseEvent e) {
		// board.selBox.stopDrag();
		MFigure figure;
		if (board.noSelect()) {
			return;
		} else if (board.singleSelect()) {
			figure = board.selects.get(0);
			figure.copyBoxRect(board.selBox);
			board.drawLines(figure);
			board.showSelectEffect(figure);

			if (figure.board.propBoard != null) {
				figure.toProps(null);
			}
		} else {
			Point p = board.selBox.shape.getLocation();
			int distX = p.x - board.selBox.oldX;
			int distY = p.y - board.selBox.oldY;
			Point p2 = null;
			for (MFigure f : board.selects) {
				if (f instanceof MLine) {
					continue;
				}
				p2 = f.shape.getLocation();
				p2.x += distX;
				p2.y += distY;
				f.shape.setLocation(p2);
				f.board.moveShape(f, p2.x, p2.y);
				//f.invalidate();
				board.drawLines(f);
			}
			
			board.moveShape(board.selBox, p.x, p.y);
		}

		board.selBox.setVisible(true);
		// board.addUndo();
		board.cmd = "select";
	}

}