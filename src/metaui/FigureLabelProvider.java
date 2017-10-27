package metaui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import meta.MFigure;
import meta.MLine;
import meta.MShape;
import util.Exp;

public class FigureLabelProvider extends LabelProvider {
	Map<String, Image> images = new HashMap<String, Image>();
	
	@Override
	public Image getImage(Object element) {
		MFigure f = (MFigure)element;
		//MetaMap map = (MetaMap) element;
		String imgPath = f.model.str("img");
		if (Exp.isNull(imgPath)) {
			return null;
		}
		
		if (images.containsKey(imgPath)) {
			return images.get(imgPath);
		}
		
		// System.out.println("Figure Label Load Image:" + Consts.root + imgPath);
		// ClassLoader classLoader = this.getClass().getClassLoader();
		// new FileInputStream(new File(Consts.root + imgPath));
		Image img = null;
		try {
			InputStream stream = this.getClass().getResourceAsStream("/" + imgPath);
			//InputStream stream  = new FileInputStream(new File(Consts.root + imgPath));
			img = new Image(Display.getCurrent(), stream);
			images.put(imgPath, img);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

	@Override
	public String getText(Object element) {
		//MetaMap map = (MetaMap) element;
		MFigure f = (MFigure)element;
		String name = f.model.name();
		String quote_name = "";
		if (!Exp.isNull(name)) {
			quote_name = "(" + f.model.name() + ")";
		}
		return f.model.label() + quote_name;
	}
}
