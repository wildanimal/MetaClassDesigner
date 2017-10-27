package command;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import meta.DrawBoard;
import meta.MLine;
import util.MetaMap;
	
public class ActRouteLabel extends Command
	{
		public static int startX = 0;
		
		public static int startY = 0;
		
		public static Boolean move = false;
		
		public static String tome() {
			return "RouteLabel";
		}
		
		public static Boolean isme(String cmd) {
			return cmd.indexOf("RouteLabel") != -1;
		}
		
		public static void start (MLine line, MouseEvent e) {
			line.board.cmd = "RouteLabel";
			line.board.line=line;
			startX = e.x;
			startY = e.y;
		}

	public static void exec (DrawBoard board, MouseEvent e) {
			MLine route = board.line;
			int changeX = e.x - startX;
			int changeY = e.y - startY;
			
			MetaMap m = route.model;
			if (route.label != null) {
				Rectangle r = route.label.getBounds();
				Point p = e.getLocation();
				p.x = changeX + r.x;
				p.y = changeY + r.y;
				
				m.put("labelx", p.x + r.width / 2);
				m.put("labely", p.y + r.height / 2);
				route.label.setLocation(p);
			}
//			switch (m.str("shape")) {
//			case "HorzRect":
//				m.put("offset", changeX);
//				break;
//			case "VertRect":
//				m.put("offset", changeY);
//				break;
//			} /*else if (move && 
//				(Math.abs(changeX) > Math.abs(changeY))) {
//				m.type = "HorzRect";
//				m.offset = changeX;
//			} else if (move) {
//				m.type = "VertRect";
//				m.offset = changeY;
//			}*/
			
			move = true;
			route.invalidate();
		}

		public static void end (DrawBoard board) {
			move = false;
			board.addUndo();
			SingleSelect.exec(board.line, true);
			board.line = null;
			board.cmd = Select.tome();
			board.hideDocks();
		}
	}
