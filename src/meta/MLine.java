package meta;

import metaui.Util;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import util.Exp;
import util.MetaMap;
import command.ActRouteFromPoint;
import command.ActRouteLabel;
import command.ActRouteToPoint;
import command.Select;
import command.SingleSelect;

/**
 * 连接线.s
 */
public class MLine extends MFigure {
	
	public Label label;

//	public 
	
	/**
	 * 开始图元.
	 * 
	 * @default
	 */
	public MShape fromid;
	/**
	 * 结束图元
	 * 
	 * @default
	 */
	public MShape toid;

	public void setFromTo(MShape fromid, MShape toid) {
		this.fromid = fromid;
		model.put("fromid", fromid.model.id());
		this.toid = toid;
		model.put("toid", toid.model.id());
		
//		System.out.println(fromid.model.id() + "\n" + toid.model.id());
//		System.out.println(fromid.model.name() + "\n" + toid.model.name());

		if (fromid.lines.indexOf(this) == -1)
			fromid.lines.add(this);
		if (toid.lines.indexOf(this) == -1)
			toid.lines.add(this);

	}
	
	public Point fromPoint = new Point(0, 0);
	public Point toPoint = new Point(0, 0);

	// public Point crossFromPoint = new Point;
	// public Point crossToPoint = new Point;

	/**
	 * 构造方法
	 * 
	 * @param board
	 * @param xml
	 */
	public MLine(DrawBoard board) {
		super(board);

		// FIXME z-Index
		// board.setChildIndex(this, 0);

		// RouteName="测试线";
		// this.addEventListener(MouseEvent.CLICK,onClick);
		// this.addEventListener(MouseEvent.MOUSE_DOWN,onMouseDown);

		// textLabel.setSize(0, 0);

		model.put("shape", "Straight");
		model.put("offset", 50);
	}
	
	@Override
	public MFigure createShape() {
		if (shape != null)
			return this;

		shape = new Polyline();

		bindEvents();
		
		board.addFigure(this);
		
		return this;
	}

	@Override
	public MFigure drawShape() {
		calcSize(0, 0);
		return this;
	}

	public void drawLabel(Graphics gc) {
		Dimension size = shape.getSize();
		gc.drawText(model.label(), (toPoint.x - size.width) / 2,
				(toPoint.y - size.height) / 2);
	}

	public void drawPointRect(Graphics gc, Point point) {
		gc.drawRectangle(point.x - 3, point.y - 3, 6, 6);
		gc.setBackgroundColor(new Color(null, 0x7C, 0xFC, 0x00));
		gc.fillRectangle(point.x - 3, point.y - 3, 6, 6);
	}

	public void calcFromXY(int ex, int ey) {
		if (fromid != null) {
			String str = Exp.str(model.str("fromPoint"));
			switch (str) {
			case "right":
				fromPoint.x = fromid.getBoxRight();
				fromPoint.y = fromid.getBoxCenterY();
				break;
			case "left":
				fromPoint.x = fromid.getBoxX();
				fromPoint.y = fromid.getBoxCenterY();
				break;
			case "top":
				fromPoint.x = fromid.getBoxCenterX();
				fromPoint.y = fromid.getBoxY();
				break;
			case "bottom":
				fromPoint.x = fromid.getBoxCenterX();
				fromPoint.y = fromid.getBoxBottom();
			default:
				fromPoint.x = ex;
				fromPoint.y = ey;
			}
			//setLocation(fromPoint);
		}
	}

	/**
		 * 
		 */
	public void calcToXY(int ex, int ey) {
		if (toid != null) {
			String str = Exp.str(model.str("toPoint"));
			switch (str) {
			case "right":
				toPoint.x = toid.getBoxRight();
				toPoint.y = toid.getBoxCenterY();
				break;
			case "left":
				toPoint.x = toid.getBoxX();
				toPoint.y = toid.getBoxCenterY();
				break;
			case "top":
				toPoint.x = toid.getBoxCenterX();
				toPoint.y = toid.getBoxY();
				break;
			case "bottom":
				toPoint.x = toid.getBoxCenterX();
				toPoint.y = toid.getBoxBottom();
				break;
			default:
				toPoint.x = ex;
				toPoint.y = ey;
			}
		} else {
			toPoint.x = ex;
			toPoint.y = ey;
		}

//		toPoint.x -= fromPoint.x;
//		toPoint.y -= fromPoint.y;

		//setSize(Math.abs(toPoint.x - fromPoint.x) + 20, Math.abs(toPoint.y - fromPoint.y) + 20);
		//setLocation(Math.min(fromPoint.x, toPoint.x) - 10, Math.min(fromPoint.y, toPoint.y) - 10);
		//System.out.println(model.label() + " " + model.name() + " size " + getSize());
	}

	public void calcFromPoint(int ex, int ey) {
		MetaMap m = model;
		if (Math.abs(ex - fromid.getBoxCenterX()) <= 6) {
			if (Math.abs(ey - fromid.getBoxY()) <= 6) {
				m.put("fromPoint", "top");
			} else if (Math.abs(ey - fromid.getBoxBottom()) <= 6) {
				m.put("fromPoint", "bottom");
			} else {
				m.put("fromPoint", "bottom");
			}
		} else if (Math.abs(ey - fromid.getBoxCenterY()) <= 6) {
			if (Math.abs(ex - fromid.getBoxX()) <= 6) {
				m.put("fromPoint", "left");
			} else if (Math.abs(ex - fromid.getBoxRight()) <= 6) {
				m.put("fromPoint", "right");
			} else {
				m.put("fromPoint", "right");
			}
		} else {
			m.put("fromPoint", "right");
		}
	}

	public void calcToPoint(int ex, int ey) {
		MetaMap m = model;
		if (Math.abs(ex - toid.getBoxCenterX()) <= 6) {
			if (Math.abs(ey - toid.getBoxY()) <= 6) {
				m.put("toPoint", "top");
			} else if (Math.abs(ey - toid.getBoxBottom()) <= 6) {
				m.put("toPoint", "bottom");
			} else {
				m.put("toPoint", "bottom");
			}
		} else if (Math.abs(ey - toid.getBoxCenterY()) <= 6) {
			if (Math.abs(ex - toid.getBoxX()) <= 6) {
				m.put("toPoint", "left");
			} else if (Math.abs(ex - toid.getBoxRight()) <= 6) {
				m.put("toPoint", "right");
			} else {
				m.put("toPoint", "left");
			}
		} else {
			m.put("toPoint", "left");
		}
	}

	public void calcSize(int ex, int ey) {
		calcFromXY(ex, ey);
		calcToXY(ex, ey);
	}

	/**
	 * 绘制
	 * 
	 */
	public void draw(Graphics gc) {
		if (fromid == null) {
			throw new Error("路由线中非法的开始节点");
		}

		MetaMap m = model;
		this.setLineColor(gc);

		calcSize(0, 0);
		// calcCrossXY();
		int offset = m.num("offset");
		
		switch (m.str("shape")) {
		case "Straight": // 直线
			gc.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);

			// 透明线
//			gc.setLineWidth(3);
//			gc.setAlpha(0);
//			gc.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
//
//			gc.setAlpha(1);
			drawArrow(gc, fromPoint.x, fromPoint.y);
			break;
		case "HorzRect":
			gc.drawLine(fromPoint.x, fromPoint.y, fromPoint.x + offset, fromPoint.y);
			gc.drawLine(fromPoint.x + offset, fromPoint.y, fromPoint.x + offset, toPoint.y);
			gc.drawLine(fromPoint.x + offset, toPoint.y, toPoint.x, toPoint.y);

			// 透明线
//			gc.setLineWidth(3);
//			gc.setAlpha(0);
//			gc.drawLine(fromPoint.x, fromPoint.y, fromPoint.x + offset, fromPoint.y);
//			gc.drawLine(fromPoint.x + offset, fromPoint.y, fromPoint.x + offset, toPoint.y);
//			gc.drawLine(fromPoint.x + offset, toPoint.y, toPoint.x, toPoint.y);
//
//			gc.setAlpha(1);
			drawArrow(gc, fromPoint.x + offset, toPoint.y);
		case "VertRect":
			gc.drawLine(fromPoint.x, fromPoint.y, fromPoint.x, fromPoint.y + offset);
			gc.drawLine(fromPoint.x, fromPoint.y + offset, toPoint.x, fromPoint.y + offset);
			gc.drawLine(toPoint.x, fromPoint.y + offset, toPoint.x, toPoint.y);

			// 透明线
//			gc.setLineWidth(3);
//			gc.setAlpha(0);
//			gc.drawLine(fromPoint.x, fromPoint.y, fromPoint.x, fromPoint.y + offset);
//			gc.drawLine(fromPoint.x, fromPoint.y + offset, toPoint.x, fromPoint.y + offset);
//			gc.drawLine(toPoint.x, fromPoint.y + offset, toPoint.x, toPoint.y);
//
//			gc.setAlpha(1);
			drawArrow(gc, toPoint.x, fromPoint.y + offset);
		}

		drawLabel(gc);

		if (this.selected) {
			this.drawPointRect(gc, fromPoint);
			this.drawPointRect(gc, toPoint);
		}
	}

	public void drawArrow(Graphics gc, int fromX, int fromY) {
		// 箭头
		double slopy = Math.atan2((fromY - toPoint.y), (fromX - toPoint.x));
		double cosy = Math.cos(slopy);
		double siny = Math.sin(slopy);
		int Par = 12;

		Double x = toPoint.x + (Par * cosy - (Par / 2.0 * siny));
		Double y = toPoint.y + (Par * siny + (Par / 2.0 * cosy));
		Point point1 = new Point(x.intValue(), y.intValue());

		x = toPoint.x + (Par * cosy + Par / 2.0 * siny);
		y = toPoint.y - (Par / 2.0 * cosy - Par * siny);
		Point point2 = new Point(x.intValue(), y.intValue());

		gc.drawLine(toPoint.x, toPoint.y, point1.x, point1.y);
		gc.drawLine(point1.x, point1.y, point2.x, point2.y);
		gc.drawLine(point2.x, point2.y, toPoint.x, toPoint.y);
		// gc.drawCircle(crosstoPoint.x,crosstoPoint.y,3);//圆
	}

	/**
	 * 点击事件
	 * 
	 * @param event
	 */
	public void onClick(MouseEvent event) {
	}

	/**
	 * 鼠标按下事件
	 * 
	 * @param event
	 */
	public void onMouseDown(MouseEvent e) {
		Rectangle rect = shape.getBounds();
		GC gc = new GC(Display.getCurrent());
		int labelLength = Util.strlen(gc, model.label());
		gc.dispose();

		rect.x += (rect.width - labelLength) / 2;
		rect.y += (rect.height - 16) / 2;
		int labelHalfWidth = rect.width / 2;
		int labelHalfHeight = rect.height / 2;
		// TODO 如果在开始点或结束点区域内20x20为移动端点
		// 否则为点击
		// 落在终点区域内
		if (Math.abs(e.x - fromPoint.x - rect.x - labelHalfWidth) <= labelHalfWidth
				&& Math.abs(e.y - fromPoint.y - rect.y - labelHalfHeight) <= labelHalfHeight) {
			ActRouteLabel.start(this, e);
		} else if (Math.abs(e.x - fromPoint.x) <= 8
				&& Math.abs(e.y - fromPoint.y) <= 8) {
			// TODO zIndex
//			this.moveAbove(null);
			ActRouteFromPoint.start(this, e);
		} else if (Math.abs(e.x - toPoint.x - fromPoint.x) <= 8
				&& Math.abs(e.y - toPoint.y - fromPoint.y) <= 8) {
//			this.moveAbove(null);
			ActRouteToPoint.start(this, e);
		} else if (Select.isme(board.cmd)) {
			SingleSelect.exec(this, true);
		}
	}

	/**
	 * 销毁. 需要在外部使用.
	 */
	public void destroy() {
		fromid.lines.remove(this);
		toid.lines.remove(this);
	}

	public void onSelect() {
		super.onSelect();
		// FIXME z-Index
		// board.setChildIndex(this, board.numChildren - 1);
	}

	public void unSelect() {
		super.unSelect();
		// FIXME z-Index
		// board.setChildIndex(this, 0);
	}

	/**
	 * 设置边框线条颜色
	 */
	public void setLineColor(Graphics gc) {
		if (selected) {
			gc.setForegroundColor(Display.getCurrent()
					.getSystemColor(SWT.COLOR_BLUE));
		} else {
			gc.setForegroundColor(Display.getCurrent().getSystemColor(
					SWT.COLOR_BLACK));
		}
	}

	public void setModel(MetaMap m) {

		setFromTo(board.getShape(m.str("fromid")),
				board.getShape(m.str("toid")));

		String str = m.str("promPoint");
		if (Exp.isNull(str))
			model.put("fromPoint", "right");

		str = m.str("toPoint");
		if (Exp.isNull(str))
			model.put("toPoint", "left");

		str = m.str("offset");
		if (Exp.isNull(str))
			model.put("offset", 50);

		super.setModel(m);
		
		calcSize(0, 0);
	}
}
