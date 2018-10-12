package orm;

import java.util.List;

import orm.transformer.BasicTransformerAdapter;
import orm.work.QueryWork;

/**
 * 查询.
 * @author chenmin
 *
 */
@SuppressWarnings("unchecked")
public class Query {
	
	public QueryWork work = null;
	public Session session = null;
	
	int firstResult = 0;
	int maxResults = 0;
	
	public Query setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public Query setMaxResults(int maxResult) {
		this.maxResults = maxResult;
		return this;
	}

	public Query() {
		
	}
	
	public Query(Session session, String sql) {
		this.session = session;
		work = new QueryWork(session, sql);
	}
	
	public Query setParameter(int idx, Object val) {
		work.setParameter(idx, val);
		return this;
	}
	
	public Query setParameter(String idx, Object val) {
		work.setParameter(idx, val);
		return this;
	}
	
	public Query setParameterList(String key, Object val) {
		work.setParameterList(key, val);
		return this;
	}
	
	public Query setResultTransformer(
		BasicTransformerAdapter transformer) {
		return this;
	}
	
	public List list() throws Exception {
		if (maxResults > 0) {
			work.sql = this.session.sf.dialect.limit(
				work.sql, firstResult, maxResults);
		}
		this.session.doWork(work);
		return work.result;
	}
	
	public Object uniqueResult() throws Exception {
		this.session.doWork(work);
		if (work.result.size() > 0)
			return work.result.get(0);
		
		return null;
	}
	
	public int executeUpdate() throws Exception {
		work.query = false;
		this.session.doWork(work);
		return work.count;
	}
}
