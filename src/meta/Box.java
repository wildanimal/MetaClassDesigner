package meta;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class Box extends Figure {
	public Box() {
		setBorder(new BoxBorder());
		ToolbarLayout toolbarLayout = new ToolbarLayout();
		toolbarLayout.setStretchMinorAxis(false);
		setLayoutManager(toolbarLayout);
	}

	private class BoxBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(1, 0, 0, 0);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle rect = getPaintRectangle(figure, insets);
			graphics.drawLine(rect.getTopLeft(), rect.getTopRight());
		}
	}
}