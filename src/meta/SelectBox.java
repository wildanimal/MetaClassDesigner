package meta;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class SelectBox extends MFigure {
	public int oldX;
	public int oldY;

	public int oldMouseX;
	public int oldMouseY;
	
	public SelectBox(DrawBoard board) {
		super(board);
		this.setVisible(false);
	}
		
	@Override
	public MFigure createShape() {
		if (shape != null)
			return this;
		
		shape = new RectangleFigure();
		shape.setBorder(new LineBorder(ColorConstants.blue));
		//shape.setBackgroundColor(ColorConstants.yellow);
		board.addFigure(this);
		
		return this;
	}

	@Override
	public MFigure drawShape() {
		return this;
	}

	/**
	 * 绘制
	 */
	public void draw(Graphics gc) {
		gc.setLineWidth(1); 
		gc.setLineStyle(SWT.LINE_DOT); 
		gc.setForegroundColor(
				Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		
		Rectangle rect = shape.getBounds();
		//gc.setAlpha(0x0);
		gc.setFillRule(SWT.TRANSPARENCY_ALPHA);
		gc.drawRectangle(rect.x + 1 , rect.y + 1, rect.width - 2, rect.height - 2);
	}
	
	public void storePos(MouseEvent e) {
		Point p = shape.getLocation();
		oldX = p.x;
		oldY = p.y;

		oldMouseX = e.x;// + oldX;
		oldMouseY = e.y;// + oldY;
		
		//System.out.println(oldX + "," + oldY + "," + oldMouseX + "," + oldMouseY);
	}
}