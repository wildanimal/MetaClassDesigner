package orm.transformer;

import java.io.Serializable;

import util.MetaMap;

public class MetaMapTransformer extends BasicTransformerAdapter implements Serializable {
	public static final long serialVersionUID = -1L;
	public static final MetaMapTransformer instance = new MetaMapTransformer();

	private MetaMapTransformer() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object transformTuple(Object[] tuple, String[] aliases) {
		MetaMap result = new MetaMap(tuple.length);
		for ( int i=0; i<tuple.length; i++ ) {
			String alias = aliases[i];
			if ( alias!=null ) {
				result.put( alias, tuple[i] );
			}
		}
		return result;
	}
	private Object readResolve() {
		return instance;
	}
}
