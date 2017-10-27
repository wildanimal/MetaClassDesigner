package command;

import meta.MFigure;

public class SingleSelect extends Command {

	public static void exec(MFigure figure, boolean unselect) {
		if (unselect) {
			figure.board.unSelect();
		}
		figure.onSelect();
		figure.board.addSelect(figure);

		figure.setVisible(true);
		if (figure.board.propBoard != null) {
			figure.toProps(null);
		}
	}
}
