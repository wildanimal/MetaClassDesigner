package orm;

import java.util.ArrayList;
import java.util.List;

import meta.DrawBoard;
import meta.MFigure;
import metaui.FieldsForm;
import util.MetaMap;

public class OPackage extends DrawBoard {
	public OPackage() {

		reg.put("orm.OClass", OClass.class);
		reg.put("orm.ORoute", ORoute.class);

		model.createListMap("classes");
		model.createListMap("routes");

		model.put("children", new ArrayList<MetaMap>());
		model.put("label", "包");

		model.put("img", "img/Package.gif");
		this.type = "OPackage";
	}

	@Override
	public void fromProps(FieldsForm p) {
		super.fromProps(p);
		// p.schema.text = this.schema;
	}

	/**
	 * 将自身属性赋值到参数面板.
	 */
	@Override
	public void toProps(FieldsForm p) {
		if (p == null && (p = getProps(type)) == null)
			return;

		super.toProps(p);
		// p.schema.text = this.schema;
	}

	@Override
	public void setModel(MetaMap obj) {
		super.setModel(obj);

		if (!obj.containsKey("img")) {
			obj.put("img", "img/Package.gif");
		}

		for (MetaMap clazz : obj.listmap("classes")) {
			OClass oclass = new OClass(this);
			oclass.setModel(clazz);
		}

		for (MetaMap route : obj.listmap("routes")) {
			try {
				ORoute oroute = new ORoute(this);
				oroute.setModel(route);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		refreshOutline();
		
		// TODO zIndex
//		selBox.moveBelow(null);
	}
	
	@Override
	public String toJson() {
		
		return getModel().toJson();
		
	}

	@Override
	public MetaMap getModel() {
		//model.listmap("classes");
		//model.listmap("routes");
		model.put("classes", null);
		model.put("routes", null);
		MFigure mf = null;
		for (Object f : children) {
			if (f instanceof ORoute) {
				mf = clearCircleRef(f);
				model.listmap("routes").add(mf.model);
			} else if (f instanceof OClass) {
				mf = clearCircleRef(f);
				model.listmap("classes").add(((MFigure) f).model);
			}
		}

		return model;
	}

	/**
	 * 去除循环引用.
	 * 循环引用会导致无法输出JSON.
	 * 循环引用的额外对象，是在{@link ORMUtil#arrange}里设定的.
	 * @param f
	 * @return
	 */
	private MFigure clearCircleRef(Object f) {
		MFigure mf;
		mf = ((MFigure) f);
		mf.model.put("toClass", null);
		mf.model.put("opackage", null);
		mf.model.put("superClass", null);
		return mf;
	}
}
