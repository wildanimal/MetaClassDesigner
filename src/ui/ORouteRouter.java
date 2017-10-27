package ui;


import meta.MLine;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

public class ORouteRouter extends BendpointConnectionRouter {
	MLine line;
	public ORouteRouter(MLine line) {
		super();
		this.line = line;
	}

	@Override
	public void route(Connection conn) {
		// 清空连线的所有点
		PointList points = conn.getPoints();
		points.removeAllPoints();
		
		Point A_POINT = new Point(0, 0);
		
		// 得到目标和源参考点
		Point sourceRef = conn.getSourceAnchor().getReferencePoint();
		Point targetRef = conn.getTargetAnchor().getReferencePoint();
		A_POINT.setLocation(sourceRef.x, targetRef.y);
		
		// 得到起始点和结束点
		Point startPoint = conn.getSourceAnchor().getLocation(A_POINT);
		Point endPoint = conn.getTargetAnchor().getLocation(A_POINT);
		
		// 添加起始点
		A_POINT.setLocation(startPoint);
		conn.translateToRelative(A_POINT);
		points.addPoint(A_POINT);
		
		// 添加转折点
		A_POINT.setLocation(sourceRef.x, targetRef.y);
		conn.translateToRelative(A_POINT);
		points.addPoint(A_POINT);
		
		// 添加结束点
		A_POINT.setLocation(endPoint);
		conn.translateToRelative(A_POINT);
		points.addPoint(A_POINT);
		
		// 设置连线经过的所有点
		conn.setPoints(points);

		// 得到目标和源参考点
//		Rectangle startRect = line.fromid.getBounds();
//		Rectangle toRect = line.toid.getBounds();
//
//		// 得到起始点和结束点
//		Point loc1 = new Point(startRect.x + startRect.width /2, startRect.y + startRect.height / 2);//conn.getSourceAnchor().getLocation(A_POINT);
//		Point loc2 = new Point(toRect.x + toRect.width /2, toRect.y + toRect.height / 2);//conn.getTargetAnchor().getLocation(A_POINT);
//
//		// 添加起始点
//		A_POINT.setLocation(loc1);
//		conn.translateToRelative(A_POINT);
//		points.addPoint(A_POINT);
//
////		A_POINT.setLocation(sourceRef.x, targetRef.y);
////		 添加转折点
//		int offset = line.model.num("offset");
//		switch (line.model.str("shape")) {
//		case "HorzRect":
//			A_POINT.setLocation(loc1.x + offset, loc1.y);
//			//conn.translateToRelative(A_POINT);
//			points.addPoint(A_POINT);
//			
//			A_POINT.setLocation(loc1.x + offset, loc2.y);
//			//conn.translateToRelative(A_POINT);
//			points.addPoint(A_POINT);
//			break;
//		case "VertRect":
//			A_POINT.setLocation(loc1.x, loc1.y + offset);
//			//conn.translateToRelative(A_POINT);
//			points.addPoint(A_POINT);
//			
//			A_POINT.setLocation(loc2.x, loc1.y + offset);
//			//conn.translateToRelative(A_POINT);
//			points.addPoint(A_POINT);
//			
//			break;
//		}
//		
//
//		// 添加结束点
//		A_POINT.setLocation(loc2);
//		conn.translateToRelative(A_POINT);
//		points.addPoint(A_POINT);
//
//		// 设置连线经过的所有点
//		conn.setPoints(points);
	}

}
