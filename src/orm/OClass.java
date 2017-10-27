package orm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import meta.Box;
import meta.DrawBoard;
import meta.MFigure;
import meta.MShape;
import metaui.FieldsForm;
import metaui.Util;
import util.ListMap;
import util.MetaMap;

public class OClass extends MShape {
//	public Image iconImg = null;
//
	public Image outsideImg = null;

	public OClass(DrawBoard board) {
		super(board);

		model.put("img", "img/class.gif");

		loadOClass(this.type = "OClass");

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("img/ref.gif");
		outsideImg = new Image(Display.getCurrent(), stream);
//
//		stream = classLoader.getResourceAsStream("img/class.gif");
//		iconImg = new Image(Display.getCurrent(), stream);

		// model.fillColor = 0xFEFEC8;
		// if (this.width == 0 && this.height == 0) {
		// model.name = this.name = "Class";
		// model.label = "类";
		// model.width = this.width=120;
		// model.height = this.height= 40;
		// }
		model.listmap("fields");
	}
	
	public void delField(String name) {
	}

	public Box fieldBox = null;
	public Box methodBox = null;
	public Label title = null;
	@Override
	public MFigure createShape() {
		if (shape != null)
			return this;

		shape = new RectangleFigure();

		bindEvents();

		board.addFigure(this);
		
		shape.setLayoutManager(new ToolbarLayout());
		shape.setBackgroundColor(ColorConstants.yellow);
		shape.setOpaque(true);
		
		return this;
	}

	@Override
	public MFigure drawShape() {
		getShape().removeAll();
		shape.add(title = new Label());
		shape.add(fieldBox = new Box());
		shape.add(methodBox = new Box());

		((RectangleFigure)shape).setLineWidth(selected ? 2 : 1);
		shape.setBorder(new LineBorder(selected 
			? ColorConstants.blue : ColorConstants.black));
		
		title.setText(model.label() + "(" + model.name() + ")");
		// 画类名
		List<MetaMap> fields = model.listmap("fields");
		String label = null;
		for (MetaMap field : fields) {
			MetaMap dataType = ListMap.findBy(board.fieldtypes, "data", field.str("type"));
			MetaMap uitype = ListMap.findBy(board.uitypes, "data", field.str("uitype"));
			
			label = field.label() + "(" + field.name() + ") : "
					+ (dataType == null ? "" : dataType.label())
					+ "|" + (uitype == null ? "" : uitype.label());
			fieldBox.add(new Label(label));
		}

		// TODO 还得绘制方法
		List<MetaMap> methods = model.listmap("methods");
		for (MetaMap method :  methods) {
			methodBox.add(new Label(method.name()));
		}
		
		board.moveShape(this, model.num("x"), model.num("y"));

		Dimension size = this.shape.getSize();
		if (model.isTrue("outside")) {
			ImageFigure outside = new ImageFigure(outsideImg);
			outside.setAlignment(PositionConstants.LEFT);
			outside.setLocation(new Point (0, size.height - 18));
			shape.add(outside);
		}
		
		this.board.drawLines(this);

		return super.drawShape();
	}

	public void fromProps(FieldsForm p) {
		super.fromProps(p);
		// 清空标题
		// XXX: 切记添加子元素时此处也必须增加
		// while (numChildren > 3) {
		// removeChildAt(3);
		// }
		drawShape();
	}

	public void toProps(FieldsForm p) {

		super.toProps(p);
	}

	public void setModel(MetaMap obj) {
		super.setModel(obj);
		// model.fillColor = 0xFEFEC8;

		// if (!model.img) {
		// model.img = "img/class.gif";
		// }
	}
	
	public void draw(Graphics g) {
		this.setLineColor(g);
		MetaMap m = model;

		Rectangle rect = shape.getBounds();
		//rect.x = 0;
		//rect.y = 0;
		rect.width = 100;
		GC gc = new GC(Display.getCurrent());
		// 画标题
		String label = (m.label() + "(" + m.name() + ")");
		int labelWidth = Util.strlen(gc, label);
		if (rect.width < labelWidth + 10) {
			m.put("width", rect.width = labelWidth + 10);
		}

		rect.height = 40;
		// m.height = this.height = height;
		//
		// int i = 0;
		// Object field=null;
		// // XXX: 切记添加子元素时此处也必须增加
		// if (numChildren == 3) // 0是类的标题,1是引用图片
		// {
		List<MetaMap> fields = m.listmap("fields");
		MetaMap field = null;
		List<String> fieldLabels = new ArrayList<String>();
		for (int i = 0; i < fields.size(); i++) {
			field = fields.get(i);
			String fieldLabel = field.label() + "(" + field.name() + ") : ";
					//+ Types.get(field.str("type")) + "|" + field.str("uitype");
			fieldLabels.add(fieldLabel);
			// 计算文本宽度,flex bug需要加10.
			int fieldLabelWidth = Util.strlen(gc, fieldLabel);
			if (rect.width < fieldLabelWidth + 10) {
				m.put("width", rect.width = fieldLabelWidth + 10);
			}
			rect.height += 30;
		}
		gc.dispose();
		m.put("height", rect.height);
		
//		System.out.println("width = " + rect.width);
		shape.setSize(rect.width, rect.height);
		
		// 先画背景
		
		g.setBackgroundColor(Display.getCurrent()
				.getSystemColor(SWT.COLOR_YELLOW));
		g.fillRectangle(rect.x + 1, rect.y + 1, rect.x + rect.width - 2, rect.x + rect.height - 2);

		// 画图标
//		g.drawImage(iconImg, rect.x + 1, rect.y + 1);

		g.setForegroundColor(Display.getCurrent()
				.getSystemColor(SWT.COLOR_BLACK));
		g.drawText(label, rect.x + (rect.width - labelWidth) / 2, rect.y + 10);

		g.setForegroundColor(Display.getCurrent()
				.getSystemColor(selected ? SWT.COLOR_BLUE : SWT.COLOR_BLACK));
		g.drawLine(rect.x, rect.y + 40, rect.x + rect.width - 1, rect.y + 40);

		g.setForegroundColor(Display.getCurrent()
				.getSystemColor(SWT.COLOR_BLACK));
		int labelLinePos = 40;
		for (String fieldLabel : fieldLabels) {
			g.drawText(fieldLabel, rect.x + 2, rect.y + labelLinePos + 4);
			labelLinePos += 30;
			g.drawLine(rect.x, rect.y + labelLinePos, rect.x + rect.width - 1, rect.y + labelLinePos);
		}
		
		g.setLineWidth(selected ? 2 : 1);
		g.setForegroundColor(Display.getCurrent()
				.getSystemColor(selected ? SWT.COLOR_BLUE : SWT.COLOR_BLACK));
		g.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
//		g.drawLine(rect.x + 1, rect.y + 1, rect.x + rect.width - 1, rect.y);
//		g.drawLine(rect.x + 1, rect.y, rect.x, rect.y + rect.height - 1);
//		g.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);
//		g.drawLine(rect.x + 1, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
		//
		if (m.isTrue("outside")) {
		}
		//
		// m.width = this.width;

		// TODO 还得绘制方法
		this.board.drawLines(this);
	}
}
