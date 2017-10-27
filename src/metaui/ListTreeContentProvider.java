package metaui;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import meta.DrawBoard;
import util.MetaMap;

public class ListTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

	@Override
	public Object[] getChildren(Object obj) {
		//System.out.println("getChildren");
//		MetaMap map = (MetaMap)obj;
//		List<MetaMap> children = new ArrayList<>();
//		children.addAll(map.listmap("classes"));
//		children.addAll(map.listmap("routes"));
		DrawBoard board = (DrawBoard)obj;
		return board.children.toArray();
		//return children.toArray();
	}

	@Override
	public Object[] getElements(Object obj) {
		if (obj instanceof List) {
			List<MetaMap> list = (List<MetaMap>)obj;
			return list.toArray();
		} else if (obj instanceof DrawBoard) {
			return ((DrawBoard)obj).children.toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object obj) {
		return null;
	}

	@Override
	public boolean hasChildren(Object obj) {
		if (obj instanceof DrawBoard) {
			DrawBoard board = (DrawBoard)obj;
			return board.children.size() > 0;
		} else {
			return false;
		}
	}
	
}