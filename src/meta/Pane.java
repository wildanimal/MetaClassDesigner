package meta;

import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.FreeformLayout;

public class Pane extends FreeformLayeredPane {

	public Pane() {
		setLayoutManager(new FreeformLayout());
		//setBackgroundColor(ColorConstants.lightGray);
		setOpaque(true);
	}

}
