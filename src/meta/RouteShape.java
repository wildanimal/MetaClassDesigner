package meta;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;

public class RouteShape extends MShape {
	
	public RouteShape(DrawBoard board) {
		super(board);
		this.setVisible(false);
	}
		
	@Override
	public MFigure createShape() {
		if (shape != null)
			return this;
		
		shape = new RectangleFigure();
		shape.setBorder(new LineBorder(ColorConstants.blue));
		shape.setSize(8, 8);
		
		board.addFigure(this);
		
		return this;
	}

	@Override
	public MFigure drawShape() {
		return this;
	}
}