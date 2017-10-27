package orm;

/**
 * 持久化类元数据.
 * @author chenmin
 *
 */
public class ClassMetadata {
	public String getIdentifierPropertyName() {
		return "id";
	}
	
	public Class getIdentifierType() {
		return Long.class;
	}
}
