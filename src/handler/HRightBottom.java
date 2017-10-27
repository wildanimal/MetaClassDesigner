package handler;

import meta.DrawBoard;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public class HRightBottom extends Handler {
	public HRightBottom(DrawBoard board) {
		super(board);
	}

	public void calcXY() {
		Dimension size = shape.getSize();
		Rectangle selBoxBounds = board.selBox.shape.getBounds();
		setLocation(selBoxBounds.x + selBoxBounds.width - size.width / 2,
				selBoxBounds.y + selBoxBounds.height - size.height / 2);
	}

	public void adjust(MouseEvent e) {
		Rectangle selBoxBounds = board.selBox.shape.getBounds();
		int width = e.x - selBoxBounds.x;
		if (width >= 0) {
			board.selBox.shape.setSize(width, selBoxBounds.height);
		}

		int height = e.y - selBoxBounds.y;
		if (height >= 0) {
			board.selBox.shape.setSize(selBoxBounds.width, height);
		}

		super.adjust(e);
	}
}
