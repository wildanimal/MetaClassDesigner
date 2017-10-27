package meta;
	import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;

import util.MetaMap;

import command.ActRoute;
import command.Move;
import command.Select;
	
	public class MShape extends MFigure
	{
		public MShape(DrawBoard board)
		{
			super(board);
//			textLabel.setStyle("textAlign","center");
		}
		
		@Override
		public void onClick(MouseEvent event){
			/*trace("shape click status = " + board.status);
			if (board.status=="select") {
				SingleSelect.exec(this);
				board.showSelectEffect(this);
			}*/
		}

		@Override
		public void onDoubleClick(MouseEvent event){
			// 如果区域在图片内,则修改图片
			// 如果区域在标题内,则修改标题
			// 如果区域在时间部分，则修改时间
		}

		@Override
		public void onMouseDown(MouseEvent e){
			if (Select.isme(board.cmd)) {
				Move.start(this, e);
			} else if (ActRoute.isme(board.cmd)) {
				ActRoute.start(this, e);
			} 
			//event.stopPropagation();
		}

		@Override
		public void onMouseOver(MouseEvent event){
			if ( ActRoute.isme(board.cmd) ) {
				board.overShape = this;
				board.showDocks();
			}
		}

		@Override
		public void onMouseOut(MouseEvent event){
			if ( ActRoute.isme(board.cmd) ) {// && ! board.overDock
				//board.overShape = null;
				//board.hideDocks();
			}
		}
		
		public void destroy() {
			for (MLine line : lines) {
				if (line.toid.lines.contains(line))
					line.toid.lines.remove(line);
				
				if (line.fromid.lines.contains(line))
					line.fromid.lines.remove(line);
			}
		}
		
		public void setModel(MetaMap m) {
//			setBounds(m.num("x"), m.num("y"), 
//				m.num("width"), m.num("height"));
			super.setModel(m);
		}

		@Override
		public MFigure drawShape() {
			return this;
		}

		@Override
		public MFigure createShape() {
			if (shape != null)
				return this;

			shape = new Figure();

			bindEvents();

			board.addFigure(this);
			
			return this;
		}
	}
