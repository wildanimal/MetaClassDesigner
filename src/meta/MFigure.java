package meta;

import java.util.ArrayList;
import java.util.UUID;

import metaui.FieldsForm;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import util.MetaMap;

import command.ActRoute;
import command.ActRouteFromPoint;
import command.ActRouteLabel;
import command.ActRouteToPoint;
import command.Move;
import command.MultiSelect;
import command.SingleAdjust;
import command.SingleSelect;

/**
 * 图元类. 一切图元基础.
 **/
public abstract class MFigure {
	public String type = "";
//	public CLabel textLabel = null;
	public MetaMap model = new MetaMap();
	public Figure shape = null;
	/*
	 * { fillColor 0xD9D9D9 , lineColor 0x000000 , shape "Rect" , id genId() };
	 */

	public MetaMap oclass = new MetaMap();
	public MetaMap render = new MetaMap();

	public void loadOClass(String type) {
		for (MetaMap map : board.opackage.listmap("classes")) {
			if (type.equals(map.name())) {
				oclass = map;
				return;
			}

		}
	}

	/**
	 * 所属绘图板
	 * 
	 * @default
	 */
	public DrawBoard board;
	public static final int LINE_SELECT_COLOR = 0x0000FF;
	public static final int LINE_UNSELECT_COLOR = 0x000000;
	/**
	 * 是否选中.
	 * 
	 * @default
	 */
	public boolean selected = false;
	/**
	 * 相关路由.
	 * 
	 * @default
	 */
	public ArrayList<MLine> lines = new ArrayList<MLine>();

	/**
	 * 构造方法.
	 * 
	 * @param board
	 * @param xml
	 */
	public MFigure(DrawBoard board) {
		super();
		this.board = board;

		newId();
//		this.model.put("id", genId());
		
		createShape();
//		textLabel = new CLabel(this, SWT.NONE);
		// FIXME z-Index
		// board.setChildIndex(this,board.getChildren().length-1);
	}

	public Figure getShape() {
		if (shape != null) {
			createShape();
		}
		
		return this.shape;
	}
	
	public abstract MFigure createShape();
	public abstract MFigure drawShape();
	
	public void bindEvents() {
		shape.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClicked(MouseEvent e) {
				onDoubleClick(e);
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

		shape.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				onMouseMove(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
//				onMouseOut(e);
			}

			@Override
			public void mouseHover(MouseEvent e) {
				onMouseOver(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				onMouseMove(e);
			}
		});
	}

	public void onDoubleClick(MouseEvent e) {
	}

	public void onMouseDown(MouseEvent e) {
	}

	public void onClick(MouseEvent e) {
	}

	public void onMouseOver(MouseEvent e) {
	}

	public void onMouseOut(MouseEvent e) {
	}
	
	public void onMouseMove(MouseEvent event) {
		//System.out.println("shape mouse move");
		if (ActRouteLabel.isme(board.cmd)) {
			ActRouteLabel.exec(board, event);
		} else if (ActRouteFromPoint.isme(board.cmd)) {
			ActRouteFromPoint.exec(board);
		} else if (ActRouteToPoint.isme(board.cmd)) {
			ActRouteToPoint.exec(board);
		} else if (ActRoute.isme(board.cmd) 
			&& board.fromShape instanceof MFigure) {
			ActRoute.exec(board, event);
		} else if (SingleAdjust.isme(board.cmd)) {
			SingleAdjust.exec(board, event);
		} else if (MultiSelect.isme(board.cmd)) {
			MultiSelect.exec(board, event);
		} else if (Move.isme(board.cmd)) {
			Move.exec(board, event);
		}
	}
	
	public void onMouseUp(MouseEvent e) {
		//System.out.println("figure mouse up: " + this.getClass().getName() + "," + board.cmd);
		if (Move.isme(board.cmd)) {
			Move.end(board, e);
		} else if (ActRouteLabel.isme(board.cmd)) {
			ActRouteLabel.end(board);
		} else if (ActRouteFromPoint.isme(board.cmd)) {
			ActRouteFromPoint.end(board, e);
		} else if (ActRouteToPoint.isme(board.cmd)) {
			ActRouteToPoint.end(board, e);
		} else if (ActRoute.isme(board.cmd)) {
			if (this instanceof MShape) {
				board.toShape = (MShape) this;
			}
			ActRoute.end(board, e);
		} else if (SingleAdjust.isme(board.cmd)) {
			SingleAdjust.end(board, e);
		} else if (MultiSelect.isme(board.cmd)) {
			MultiSelect.end(board);
		} else {
			SingleSelect.exec(this, true);
		}
	}

	public void onInit(Event event) {
	}

	/**
	 * 设置边框线条颜色
	 */
	public void setLineColor(Graphics gc) {
		if (selected) {
			gc.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		} else {
			gc.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	/**
	 * 获取选中框区域的x坐标.
	 * 
	 * @return
	 */
	public int getBoxX() {
		return shape.getLocation().x;
	}

	/**
	 * 获取选中框区域的y坐标.
	 * 
	 * @return
	 */
	public int getBoxY() {
		return shape.getLocation().y;
	}

	/**
	 * 获取选中框区域的宽度.
	 * 
	 * @return
	 */
	public int getBoxWidth() {
		return shape.getSize().width;
	}

	/**
	 * 获取选中框区域的高度.
	 * 
	 * @return
	 */
	public int getBoxHeight() {
		return shape.getSize().height;
	}

	/**
	 * 获取选中框区域的宽度.
	 * 
	 * @return
	 */
	public int getBoxRight() {
		Rectangle rect = shape.getBounds();
		return rect.x + rect.width;
	}

	/**
	 * 获取选中框区域的高度.
	 * 
	 * @return
	 */
	public int getBoxBottom() {
		Rectangle rect = shape.getBounds();
		return rect.y + rect.height;
	}

	/**
	 * 获取选中框区域的中间横坐标.
	 * 
	 * @return
	 */
	public int getBoxCenterX() {
		Rectangle rect = shape.getBounds();
		return rect.x + rect.width / 2;
	}

	/**
	 * 获取选中框区域的中间纵坐标.
	 * 
	 * @return
	 */
	public int getBoxCenterY() {
		Rectangle rect = shape.getBounds();
		return rect.y + rect.height / 2;
	}

	/**
	 * 复制选中框的矩形区域.
	 * 
	 * @param srcFigure
	 */
	public void copyBoxRect(MFigure srcFigure) {
		Rectangle rect = srcFigure.shape.getBounds();
		//System.out.println("copy box rect:" + rect.toString());
		shape.setBounds(rect);
		board.moveShape(this, rect.x, rect.y);
	}
	
	public void setBounds(int x, int y, int width, int height) {
//		System.out.println(x + "," + y + "," + width + "," + height);
//		shape.setBounds(new Rectangle(x, y, width, height));
		model.put("x", x);
		model.put("y", y);
		model.put("width", width);
		model.put("height", height);
	}
	
	public void setLocation(int x, int y) {
		shape.setLocation(new Point(x, y));
		model.put("x", x);
		model.put("y", y);
	}

	/**
	 * 显示选中效果
	 * 
	 * @param select
	 */
	public void onSelect() {
		this.selected = true;
		this.drawShape();
	}

	/**
	 * 显示选中效果
	 * 
	 * @param select
	 */
	public void unSelect() {
		this.selected = false;
		this.drawShape();
	}
	
	public void setVisible(boolean v) {
		shape.setVisible(v);
	}
	
	/**
	 * 析构前要做的事情.
	 */
	public void invalidate() {
		shape.invalidate();
	}

	/**
	 * 析构前要做的事情.
	 */
	public void destroy() {

	}

	/**
	 * 讲属性填充到属性面板.
	 */
	public void fromProps(FieldsForm p) {
		if (p == null && (p = board.getProps(type)) == null)
			return;

		board.refreshOutline();
	}

	/**
	 * 讲属性填充到属性面板.
	 */
	public void toProps(FieldsForm p) {
		if (p == null && (p = board.getProps(type)) == null)
			return;

		p.setModel(this.model);
	}

	public void setModel(MetaMap m) {
		model = m;
		
		if (!m.containsKey("fillColor")) {
			m.put("fillColor", "0xD9D9D9");
		}

		if (!m.containsKey("lineColor")) {
			m.put("lineColor", "0x000000");
		}

		if (!m.containsKey("shape")) {
			if (this instanceof MShape)
				m.put("shape", "Rect");
			else
				m.put("shape", "Straight");
		}
		
		//this.setToolTipText(m.name() != null ? m.name() : "");

		board.refreshOutline();

		drawShape();
	}
	
	public Rectangle getBounds() {
		return this.shape.getBounds();
	}

	public String genId() {
		return UUID.randomUUID().toString();
	}

	public void newId() {
		model.put("id", genId());
	}
}
