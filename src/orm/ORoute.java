package orm;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;

import command.ActRouteLabel;
import meta.DrawBoard;
import meta.MFigure;
import meta.MLine;
import metaui.FieldsForm;
import util.ListMap;
import util.MetaMap;

public class ORoute extends MLine {
	public ORoute(DrawBoard board) {
		super(board);
		model.put("type", "ManyToOne");
		model.put("required", false);

		model.put("img", "img/route.gif");
		model.put("label", "关联");

		loadOClass(this.type = "ORoute");
	}

	// public static var Types : Array = [
	// {data:'ManyToOne', label:'多对一'}
	// ,{data:'OneToMany', label:'一对多'}
	// , {data:'Extend', label:'继承'}
	// ];

	// @Override public function drawLabel() : void {
	// textLabel.text = model.label ? model.label
	// : Util.getLabel(Types, model.type);
	// //自动根据文本设置尺寸
	// var metrics : TextLineMetrics = textLabel.measureText(textLabel.text);
	// textLabel.width= metrics.width + 10;
	// textLabel.height= metrics.height + 2;
	//
	// textLabel.x=(fromPoint.x+toPoint.x - textLabel.width)/2;
	// textLabel.y=(fromPoint.y+toPoint.y - textLabel.height)/2;
	// }
	
	@Override
	public MFigure createShape() {
		if (shape != null)
			return this;

		_createShape();
		
		return this;
	}

	@Override
	public MFigure drawShape() {
		if (shape != null) {
//			if (!(shape instanceof PolylineConnection)) {
//				if (shape.getParent() == null)
//					shape.erase();
//				else
//					shape.getParent().remove(shape);
//				
//				_createShape();
//			}
		} else {
			_createShape();
		}

		MetaMap uitype = ListMap.findBy(board.uitypes, "data", model.str("uitype"));
		label.setText(model.label() + "(" + model.name() + ")");
		
		PolylineConnection conn = (PolylineConnection) shape;

		conn.setForegroundColor(selected ? ColorConstants.blue
				: ColorConstants.black);

		if (toid == board.routeShape) {
			// 设置连线目标的锚点
			conn.setTargetAnchor(board.routeShapeAnchor);
		} else {
			// 设置连线目标的锚点
			conn.setTargetAnchor(new ChopboxAnchor(toid.getShape()));
		}
		
		// 设置连线起点的锚点
		conn.setSourceAnchor(new ChopboxAnchor(fromid.getShape()));
		
		switch(model.str("shape")) {
		case "VertRect":
			conn.setConnectionRouter(new ManhattanConnectionRouter());
			break;
		case "HorzRect":
			conn.setConnectionRouter(new BendpointConnectionRouter());
			break;
			
		case "Straight":
			conn.setConnectionRouter(new FanRouter());
		}

		calcSize(0, 0);
//		switch (model.str("shape")) {
//		case "HorzRect":
//		case "VertRect":
//			conn.setConnectionRouter(new ORouteRouter(this));
//			break;
//		}
		return super.drawShape();
	}

	private void _createShape() {
		PolylineConnection conn = new PolylineConnection();
		shape = conn;
		bindEvents();
		board.addFigure(this);
		// 设置连线目标的装饰器,即箭头
		conn.setTargetDecoration(new PolygonDecoration());
		label = new Label();
		label.setOpaque(true);
		label.setBackgroundColor(ColorConstants.buttonLightest);
		label.setBorder(new LineBorder());
		
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				onMouseDown(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				onMouseUp(e);
			}

		});

		label.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				onMouseMove(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseHover(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				onMouseMove(e);
			}
		});
		// 添加连线的Locator
		conn.add(label, new MidpointLocator(conn, 0));
		
//		// 设置连线起点的锚点
//		conn.setSourceAnchor(new ChopboxAnchor(fromid.getShape()));
		// conn.setSourceDecoration(new PolygonDecoration());
		
//		conn.setConnectionRouter(new FanRouter());
	}

	public void onMouseDown(MouseEvent e) {
		ActRouteLabel.start(this, e);
	}

	@Override
	public void fromProps(FieldsForm p) {
		super.fromProps(p);

		shape.invalidate();
	}

	@Override
	public void toProps(FieldsForm p) {
		super.toProps(p);
	}

	@Override
	public void setModel(MetaMap m) {

		if (!m.containsKey("img")) {
			m.put("img", "img/route.gif");
		}
		super.setModel(m);

	}
}
