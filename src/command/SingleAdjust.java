package command;
	import handler.Handler;
import meta.DrawBoard;
import meta.MFigure;

import org.eclipse.draw2d.MouseEvent;
	public class SingleAdjust extends Command
	{
		public static Boolean isme(String cmd)  {
			return cmd == "SingleAdjust";
		}
		
		public static String tome()  {
			return "SingleAdjust";
		}
		
		public static void start (Handler hdl, MouseEvent e)   {
			hdl.board.cmd=SingleAdjust.tome();
			hdl.board.selectHandler = hdl;
		}

		public static void exec (DrawBoard board, MouseEvent e) {
			board.selectHandler.adjust(e);
		}

		public static void end (DrawBoard board, MouseEvent e) {
			board.cmd=Select.tome();
			MFigure figure  = board.selects.get(0);
			figure.copyBoxRect(board.selBox);
			SingleSelect.exec(figure, false);
			board.showSelectEffect(figure);
			board.addUndo();
			if (board.propBoard != null) {
				figure.toProps(null);
			}
		}
	}
