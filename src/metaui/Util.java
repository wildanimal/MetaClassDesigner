package metaui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import util.Exp;

public class Util {
	
	public static Image transparentImage = null;
	
	static {
		Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	    Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	    PaletteData palette = new PaletteData(new RGB[] { white.getRGB(), black.getRGB() });
	    ImageData imageData = new ImageData(1, 1, 1, palette);
	    imageData.transparentPixel = 0;
	    
	    transparentImage = new Image(Display.getCurrent()
	    	, imageData);
	    //Util.class.getResourceAsStream("/img/transparent.png")
	}
	
	public static int strlen(GC gc, String string) {
		if (Exp.isNull(string))
			return 0;
	    
	    int width = 0;     
	    for (int i = 0; i < string.length(); i++) {     
	        char c = string.charAt(i);     
	        width += gc.getAdvanceWidth(c);     
	    }     
	    
	    return width;     
	}    
}
