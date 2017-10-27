package metaui;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

import util.Exp;
import util.MetaMap;

public class SimpleRouter extends AbstractRouter {
	
	public Point A_POINT = new Point();
	
	public MetaMap model = null;

	public SimpleRouter(MetaMap model) {
		this.model = model;
	}
	
	@Override
	public void route(Connection conn) {  
	    // 清空连线的所有点  
	    PointList points = conn.getPoints();  
	    points.removeAllPoints();  
	      
	    // 得到目标和源参考点  
	    Point sourceRef = conn.getSourceAnchor().getReferencePoint();  
	    Point targetRef = conn.getTargetAnchor().getReferencePoint();  
	    A_POINT.setLocation(sourceRef.x, targetRef.y);  
	      
	    // 得到起始点和结束点  
	    Point startPoint = conn.getSourceAnchor().getLocation(A_POINT);  
	    Point endPoint = conn.getTargetAnchor().getLocation(A_POINT);  
	      
	    // 添加起始点  
	    A_POINT.setLocation(conn.getSourceAnchor().getLocation(sourceRef));  
	    conn.translateToRelative(A_POINT);  
	    points.addPoint(A_POINT);  
	      
	    // 添加转折点  
	    Integer x = model.num("labelx");
	    Integer y = model.num("labely");
	    if (!Exp.isNull(x) && !Exp.isNull(y)) {
		    A_POINT.setLocation(x, y);  
		    conn.translateToRelative(A_POINT);  
		    points.addPoint(A_POINT);  
	    }
	      
	    // 添加结束点  
	    A_POINT.setLocation(conn.getTargetAnchor().getLocation(targetRef));  
	    conn.translateToRelative(A_POINT);  
	    points.addPoint(A_POINT);  
	      
	    // 设置连线经过的所有点  
	    conn.setPoints(points);  
	} 
}
