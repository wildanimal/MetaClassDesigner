package command;
	import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import meta.DrawBoard;
import meta.MFigure;
import meta.SelectBox;

	public class Remove extends Command
	{
		public static void exec(DrawBoard board) {
			board.hideSelectEffect();
			
			if (board.selects.size() == board.children.size()) {
				 MessageBox dialog = new MessageBox(board.propBoard.getShell(),SWT.OK|SWT.CANCEL|SWT.ICON_WARNING);
			        dialog.setText("警告");
			        dialog.setMessage("确定要删除画板所有物件？");
			        
			     if (dialog.open() == SWT.CANCEL) {
			    	 return;
			     }
			        
			    
			}
			
			for (MFigure figure : board.selects) {

				figure.destroy();
				
				if (figure instanceof SelectBox ) {
					continue;
				}
				
				board.removeFigure(figure);
			}
			
			//System.out.println(board.model.toJson());

			board.refreshOutline();
			
			board.selects.clear();
			board.addUndo();
		}
	}
