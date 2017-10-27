package handler;

import meta.DrawBoard;
import meta.MFigure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import command.Select;
import command.SingleAdjust;

public class Handler extends MFigure {
	// public var board:DrawBoard;
	public int refX;
	public int refY;

	public Handler(DrawBoard board) {
		super(board);

		final Handler self = this;

		createShape();
	}
	
	@Override
	public MFigure createShape() {
		if (shape != null)
			return this;
		
		shape = new Figure();
		shape.setSize(8, 8);
		
		return this;
	}



	@Override
	public MFigure drawShape() {
		return this;
	}

	@Override
	public void onMouseDown(MouseEvent e) {
		if (Select.isme(board.cmd)) {
			SingleAdjust.start(this, e);
			// this.startDrag();
		}// else if (board.status=="route") {
			// board.fromElement=this.target;
			// }
		super.onMouseDown(e);
	}

	@Override
	public void onMouseOver(MouseEvent e) {
		super.onMouseOver(e);
	}

	@Override
	public void onMouseUp(MouseEvent e) {
		if (SingleAdjust.isme(board.cmd)) {
			// this.stopDrag();
			SingleAdjust.end(board, e);
		}// else if (board.status =="route") {
			// board.toElement=this.target;
			// }
			// event.stopPropagation();
		super.onMouseUp(e);
	}

	/**
	 * 绘制
	 * @throws Error
	 */
	public void draw(PaintEvent e) {
		// e.gc.lineStyle(1,0x000000);
		e.gc.setBackground(new Color(null, 0x3C, 0xB3, 0x71)); // 3CB371

		// self.setLocation(x, y);
		e.gc.drawRectangle(0, 0, 8, 8);
	}
	
	public void calcXY() {
	}

	public void adjust(MouseEvent e) {
		// board.selectBox.draw();
		board.showSelectEffect(board.selBox);
	}
}