package command;

import handler.Handler;
import meta.DrawBoard;
import meta.MFigure;
import meta.MShape;
import meta.SelectBox;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class MultiSelect extends Command {
	public static Boolean isme(String cmd) {
		return cmd == "MultiSelect";
	}

	public static void start(DrawBoard board, MouseEvent e) {
		board.initSelectEffect();
		// unSelect();
		board.cmd = "MultiSelect";

		board.selBox.setVisible(true);
		board.selBox.oldX = e.x;
		board.selBox.oldY = e.y;
		board.selBox.setLocation(e.x, e.y);
		board.selBox.shape.setSize(1,1);
		board.selBox.shape.setLocation(e.getLocation());
		//board.selBox.invalidate();
	}

	public static void exec(DrawBoard board, MouseEvent e) {
		// board.checkScrollBar();

		board.selBox.setVisible(true);
		
		Rectangle rect = new Rectangle(Math.min(board.selBox.oldX, e.x),
				Math.min(board.selBox.oldY, e.y),
				Math.abs(board.selBox.oldX - e.x),
				Math.abs(board.selBox.oldY - e.y));
		board.selBox.setBounds(rect.x, rect.y, rect.width, rect.height);
		board.selBox.shape.setBounds(rect);

		//System.out.println(board.selBox.shape.getBounds());
		//board.selBox.invalidate();

	}

	public static void end (DrawBoard board)  {
			board.cmd = "select";
			Point selBoxLoc = board.selBox.shape.getLocation();
			int boxRight = board.selBox.getBoxRight();
			int boxBottom = board.selBox.getBoxBottom();
			int minX = 0, minY = 0, maxX = 0, maxY = 0;
			int figureX, figureY, figureRight, figureBottom;
			Boolean first = true;
			
			//Rectangle rect = board.selBox.getRect(board);
			board.unSelect();
			
			//奇怪的事情,unSelect后selBox变成上次的矩形
			//board.selBox.x = rect.x;
			//board.selBox.y = rect.y;
			//board.selBox.width = rect.width;
			//board.selBox.height = rect.height;
			for (MFigure figure : board.children) {
				if (figure instanceof Handler || figure instanceof SelectBox)
					continue;
				if ( figure instanceof MShape) {
//					MLine route = (MLine)figure ;
//					if (route.fromPoint.x > selBoxLoc.x
//						&& route.fromPoint.y > selBoxLoc.y
//						&& route.toPoint.x < boxRight
//						&& route.toPoint.y < boxBottom) {
//						figure.onSelect();
//						//figure.draw();
//						board.addSelect(figure);
//						// XXX: 为防止自己定位到自己的线条不可见，特别定位到最前端
//						if (route.fromid == route.toid) {
//							// FIXME : z-Index
//							//board.setChildIndex(route, board.numChildren - 1);
//						}
//					}
//				} else {
					Point p = figure.shape.getLocation();
					if ( (figureX = p.x) > selBoxLoc.x
						&& (figureRight = figure.getBoxRight()) 
							< boxRight
						&& (figureY = p.y) > selBoxLoc.y
						&& (figureBottom = figure.getBoxBottom()) 
							< boxBottom) {
						figure.onSelect();
						figure.board.moveShape(figure, p.x, p.y);
						figure.invalidate();
						board.addSelect(figure);
						
						if (first) {
							minX = p.x;
							minY = p.y;
							maxX = figureRight;
							maxY = figureBottom;
							first = false;
						} else {
							if (figureX<minX)
								minX = figureX;
							
							if (figureY<minY)
								minY = figureY;
								
							if (figureRight > maxX)
								maxX = figureRight;
								
							if (figureBottom > maxY)
								maxY = figureBottom;
						}
					}
				}
			}
			
			if (board.noSelect()) {
				if (board.propBoard != null) {
					board.toProps(null);
				}
				board.selBox.setVisible(false);
				return;
			} else if (board.singleSelect()) {
				board.showSelectEffect(board.selects.get(0));
				if (board.propBoard != null) {
					board.selects.get(0).toProps(null);
				}
			} else if (board.multiSelect()) {
				Rectangle rect = new Rectangle(minX, minY
					, maxX - minX, maxY - minY);
				System.out.println(rect);
				board.selBox.setBounds(rect.x, rect.y, rect.width, rect.height);
				board.selBox.shape.setSize(rect.width, rect.height);
				//board.selBox.shape.setLocation(new Point(rect.x, rect.y));
				board.moveShape(board.selBox, rect.x, rect.y);
				board.selBox.setVisible(true);
			}
		}
}
