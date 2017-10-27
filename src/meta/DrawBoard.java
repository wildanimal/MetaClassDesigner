package meta;

import handler.HRightBottom;
import handler.Handler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import metaui.FieldsForm;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import orm.ORMUtil;
import util.ListMap;
import util.MAP;
import util.MetaMap;
import util.NatualJsonDecode;

import command.ActRoute;
import command.ActRouteFromPoint;
import command.ActRouteLabel;
import command.ActRouteToPoint;
import command.Add;
import command.Move;
import command.MultiSelect;
import command.Select;
import command.SingleAdjust;

public class DrawBoard {

	public String cmd = "select";
	public String type = "";

	public Composite propBoard = null;
	public TreeViewer outline = null;
	public Composite prop = null;

	public SelectBox selBox = null;

	public MShape fromShape = null;
	public MShape toShape = null;
	public MShape overShape = null;

	public MLine line = null;
	public Handler selectHandler = null;

	public List<MFigure> selects = new ArrayList<MFigure>();
	public List<Handler> handlers = new ArrayList<Handler>();
	public boolean initHandlers = false;

	public MetaMap opackage = new MetaMap();
	public MetaMap oclass = new MetaMap();
	public ListMap fieldtypes = null;
	public ListMap uitypes = null;
	
	public MetaMap model = new MetaMap();
	public HashMap<String, Class> reg = new HashMap<String, Class>();

	public Pane shape = null;
	public MShape routeShape = null;
	public ChopboxAnchor routeShapeAnchor = null;
	public List<MFigure> children = new ArrayList<MFigure>();
	
	public Stack<String> undos = new Stack<String>();
	public Stack<String> redos = new Stack<String>();
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DrawBoard() {
		createShape();
		
		selBox = new SelectBox(this);
		selBox.setVisible(false);

		routeShape = new RouteShape(this);
		routeShapeAnchor = new ChopboxAnchor(routeShape.shape);
		//selBox.moveBelow(null);
		// setBackgroundImage(Util.transparentImage);
		// setBackgroundMode(SWT.INHERIT_FORCE);
	}

	public DrawBoard createShape() {
		if (shape != null)
			return this;

		shape = new Pane();
		bindEvents();
		return this;
	}
	private void bindEvents() {
		shape.addMouseListener(new MouseListener() {
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
			}

			@Override
			public void mouseHover(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				//onMouseMove(e);
			}
		});
	}

	/**
	 * 判断是否有选中的图元.
	 * 
	 * @return
	 */
	public Boolean noSelect() {
		return selects.size() == 0;
	}

	/**
	 * 判断是否只有一个选中的图元.
	 * 
	 * @return
	 */
	public Boolean singleSelect() {
		return selects.size() == 1;
	}

	/**
	 * 判断是否只有多个选中的图元.
	 * 
	 * @return
	 */
	public Boolean multiSelect() {
		return selects.size() > 1;
	}

	/**
	 * 判断选中的图元中是否包含此图元.
	 * 
	 * @param figure
	 * @return
	 */
	public Boolean hasSelect(MFigure figure) {
		return figure != null ? selects.indexOf(figure) != -1
				: selects.size() > 0;
	}

	/**
	 * 添加一个图元到选中列表.
	 * 
	 * @param figure
	 */
	public void addSelect(MFigure figure) {
		if (!hasSelect(figure)) {
			selects.add(figure);
		}
	}

	/**
	 * 取消所有选中的对象
	 */
	public void unSelect() {
		// hideSelectEffect();
		if (hasSelect(null)) {
			Boolean isSingle = singleSelect();
			for (MFigure figure : selects) {
				if (figure != null) {
					// if (isSingle) {
					// figure.model.fromProps();
					// }
					figure.unSelect();
					//figure.shape.invalidate();
				}
			}

			selects.clear();
		}
	}

	/**
	 * 显示图元的选中效果.
	 * 
	 * @param figure
	 */
	public void showSelectEffect(MFigure figure) {
		this.initSelectEffect();
		selBox.copyBoxRect(figure);
		moveSelectEffect();
	}

	/**
	 * 隐藏选中效果.
	 */
	public void hideSelectEffect() {
		initSelectEffect();

		selBox.setVisible(false);

		hideHandlers();

		if (selects.size() == 1) {
			selects.get(0).unSelect();
		}
	}

	/**
	 * 初始化选中状态. 显示选中框. 显示8个
	 */
	public void initSelectEffect() {
		if (!initHandlers) {
			initHandlers = true;
			Handler hdl;
			handlers.add(hdl = new HRightBottom(this));
			hdl.shape.invalidate();
			hideHandlers();

			// Dock dck ;
			// docks.push(dck = new DLeft(this));
			// dck.draw();
			// docks.push(dck = new DRight(this));
			// dck.draw();
			// docks.push(dck = new DTop(this));
			// dck.draw();
			// docks.push(dck = new DBottom(this));
			// dck.draw();
			//
			// hideDocks();
		}
	}

	/**
	 * 移动选中效果. 选中框和形状控制小方块.
	 */
	public void moveSelectEffect() {
		int idx = children.size() - 1;
		selBox.shape.invalidate();
		// FIXME 设置z-index
		// setChildIndex(selBox, idx);
		selBox.setVisible(true);
		for (Handler hdl : handlers) {
			// setChildIndex(hdl, idx);
			hdl.calcXY();
			hdl.setVisible(true);
		}
	}

	/**
	 * 显示形状控制小方块.
	 */
	public void showHandlers() {
		for (Handler hdl : handlers) {
			hdl.calcXY();
			hdl.setVisible(true);
		}
	}

	/**
	 * 隐藏形状控制小方块.
	 */
	public void hideHandlers() {
		for (Handler hdl : handlers) {
			hdl.setVisible(false);
		}
	}

	public void addUndo() {
		undos.push( toJson() );
	}
	
	public String toJson() {
		return model.toJson();
	}

	public void undo() {
		if (!undos.empty()) {
			String json = undos.pop();
			redos.push(json);
			
			MetaMap m = (MetaMap)NatualJsonDecode.fromJson(json, MetaMap.class);
			this.setModel(m);
		}
	}

	public void redo() {
		if (!redos.empty()) {
			String json = redos.pop();
			undos.push(json);
			
			MetaMap m = (MetaMap)NatualJsonDecode.fromJson(json, MetaMap.class);
			this.setModel(m);
		}
	}

	public void addFigure(MFigure figure) {
		if (!this.children.contains(figure))
			this.children.add(figure);
		
		if (!shape.getChildren().contains(figure.shape))
			shape.add(figure.shape);
	}

	public void removeFigure(MFigure f) {
		f.shape.setVisible(false);
		if (f.shape.getParent() != null)
			f.shape.getParent().remove(f.shape);
		else 
			f.shape.erase();
		
		f.shape = null;
		f.destroy();
		if (this.children.contains(f))
			children.remove(f);
		
		shape.invalidate();
		
		refreshOutline();
	}
	
	public void refreshOutline() {
		if (outline != null) {
			//outline.setInput(this.getModel());
			outline.refresh();
		}
	}

	/**
	 * 绘制所有连线
	 * 
	 * @param figure
	 */
	public void drawLines(MFigure figure) {
		if (figure != null) {
			for (MLine route : figure.lines) {
				route.calcSize(0, 0);
				route.shape.invalidate();
			}
		}
	}

	public void setCmd(String cmd, String iconPath) {
		this.cmd = cmd;
		removeCursor();
		if (iconPath != null) {
			InputStream stream = this.getClass().getResourceAsStream(iconPath);
//			Cursor cursor = new Cursor(Display.getCurrent(), new Image(Display.getCurrent(), stream));
//			Display.getCurrent().s.setCursor(cursor);
		}
	}

	public void removeCursor() {
//		this.getShell().setCursor(
//				new Cursor(Display.getCurrent(), SWT.CURSOR_ARROW));
	}

	public FieldsForm getProps(String pid) {
		if (propBoard == null)
			return null;

		hideAllProps();

		FieldsForm p = null;
		for (Control c : propBoard.getChildren()) {
			if (c instanceof FieldsForm) {
				if (((FieldsForm) c).name.equals(pid)) {
					p = (FieldsForm) c;
					break;
				}
			}
		}

		if (p == null) {
			p = new FieldsForm(propBoard, SWT.NORMAL);
			p.setLocation(0, 0);
			p.setSize(propBoard.getSize());
			p.name = pid;
			p.oclass = selects.size() > 0 ? selects.get(0).oclass : this.oclass;
			p.model = selects.size() > 0 ? selects.get(0).model : this.model;
		} else {
			p.setModel(selects.size() > 0 ? selects.get(0).model : this.model);
		}
		
		p.board = this;

		p.setVisible(true);
		propBoard.layout();

		prop = p;
		return p;
	}

	/**
	 * 将属性填充到属性面板.
	 */
	public void toProps(FieldsForm p) {
		if (p == null && (p = getProps(type)) == null)
			return;

		p.model = this.model;
	}

	/**
	 * 显示形状停靠点.
	 */
	public void showDocks() {
		// TODO 停靠点
		// for (Dock hdl : docks) {
		// hdl.calcXY();
		// hdl.setVisible(true);
		// }
	}

	/**
	 * 隐藏形状停靠点.
	 */
	public void hideDocks() {
		// for (Dock hdl : docks) {
		// hdl.resetStyle();
		// hdl.setVisible(false);
		// }
	}

	/**
	 * 点击事件.
	 * 
	 * @param event
	 */
	public void onClick(MouseEvent event) {
		Add.exec(this, event);
	}

	/**
	 * 鼠标按下事件.
	 * 
	 * @param event
	 */
	public void onMouseDown(MouseEvent event) {
		// trace("board down status = " + status);
		if (Select.isme(cmd)) {
			MultiSelect.start(this, event);
		}
	}

	/**
	 * 鼠标移动事件.
	 * 
	 * @param event
	 */
	public void onMouseMove(MouseEvent event) {
		_onMouseMove(event);

		// if (!scrollTimer.running) {
		// checkScroll();
		// }
	}

	/**
	 * 因为会被调用多次
	 */
	private void _onMouseMove(MouseEvent event) {
		// System.out.println("board mouse move");
		if (ActRouteLabel.isme(cmd)) {
			ActRouteLabel.exec(this, event);
		} else if (ActRouteFromPoint.isme(cmd)) {
			ActRouteFromPoint.exec(this);
		} else if (ActRouteToPoint.isme(cmd)) {
			ActRouteToPoint.exec(this);
		} else if (ActRoute.isme(cmd) && this.fromShape instanceof MFigure) {
			ActRoute.exec(this, event);
		} else if (SingleAdjust.isme(cmd)) {
			SingleAdjust.exec(this, event);
		} else if (MultiSelect.isme(cmd)) {
			MultiSelect.exec(this, event);
		} else if (Move.isme(cmd)) {
			Move.exec(this, event);
		}
	}

	/**
	 * 鼠标弹起事件.
	 * 
	 * @param event
	 */
	public void onMouseUp(MouseEvent event) {
		// System.out.println("board mouse up: " + cmd);
		if (ActRouteLabel.isme(cmd)) {
			ActRouteLabel.end(this);
		} else if (ActRouteFromPoint.isme(cmd)) {
			ActRouteFromPoint.end(this, event);
		} else if (ActRouteToPoint.isme(cmd)) {
			ActRouteToPoint.end(this, event);
		} else if (ActRoute.isme(cmd)) {
			ActRoute.end(this, event);
		} else if (Move.isme(cmd)) {
			Move.end(this, event);
		} else if (MultiSelect.isme(cmd)) {
			MultiSelect.end(this);
		} else if (SingleAdjust.isme(cmd)) {
			SingleAdjust.end(this, event);
		} else {
			Add.exec(this, event);
		}
		// trace("up status = " + status);
	}

	public void setModel(MetaMap m) {
		clear();
		this.model = m;
	}

	/**
	 * 清空所有子图元.
	 */
	public void clear() {
		for (MFigure c : children) {
			if (c instanceof Handler 
				|| c instanceof SelectBox 
				|| c instanceof RouteShape) {
				continue;
			}
			//c.shape.setVisible(false);
			if (c.shape.getParent() != null)
				c.shape.getParent().remove(c.shape);
			else 
				c.shape.erase();
			
			c.shape = null;
			
		}
		this.shape.invalidate();
		this.children.clear();
		this.selects.clear();

		fromShape = null;
		toShape = null;
		overShape = null;
		
		this.hideSelectEffect();
		
		model.clear();
		this.toProps(null);
		

		refreshOutline();
	}
	
	public MetaMap getModel() {
		return model;
	}

	public void fromProps(FieldsForm p) {
		if (p == null && (p = getProps(type)) == null)
			return;

		refreshOutline();
	}
	
	public void moveShape(MFigure figure, int x, int y) {
		figure.shape.setVisible(true);
		figure.shape.setLocation(new Point(x, y));
		shape.getLayoutManager().setConstraint(figure.shape, new Rectangle(x, y, -1, -1));
		Rectangle rect = figure.shape.getBounds();
		figure.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 隐藏属性面板所有子面板.
	 */
	public void hideAllProps() {
		for (Control c : propBoard.getChildren()) {
			c.setVisible(false);
		}
	}

	public void setOPackage(MetaMap pkg) {
		opackage = pkg;
		
		ORMUtil.arrange(opackage);
		List<Map<String, Object>> classes = MAP.listmap(opackage, "classes");
		oclass = (MetaMap) ListMap.findBy(classes, "name", this.type);
		
		MetaMap ofield = (MetaMap) ListMap.findBy(classes, "name", "OField");
		
		List<Map<String, Object>> fields = MAP.listmap(ofield, "fields");
		MetaMap dist = (MetaMap)ListMap.findBy(fields, "name", "type");
		fieldtypes = ListMap.load(dist.str("datasource"));

		dist = (MetaMap)ListMap.findBy(fields, "name", "uitype");
		uitypes = ListMap.load(dist.str("datasource"));
		
	}
	

	/**
	 * 按照数据库序号获取图元.
	 * @param aid
	 * @return 
	 */
	public MFigure getFigure(String id) {
		for (MFigure c : children) {
			if ( id.equals(c.model.id()) || id.equals(c.model.name())) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * 按照数据库序号获取图元.
	 * @param aid
	 * @return 
	 */
	public MShape getShape(String id) {

		for (MFigure c : children) {
			if (c instanceof MShape) {
				MShape f = (MShape)c;

				if ( id.equals(c.model.id()) || id.equals(c.model.name())) {
					return f;
				}
			}
		}
		
		return null;
	}
	
	public void drawSelects() {
		for (MFigure f : selects) {
			f.drawShape();
			f.invalidate();
		}
	}
}
