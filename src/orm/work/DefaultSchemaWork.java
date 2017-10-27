package orm.work;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import orm.ORMUtil;
import orm.Work;
import orm.dialect.Dialect;
import util.Exp;
import util.MetaMap;
/**
 * 数据库库表更新工作.
 * <p>根据模型定义更新库表结构</p>
 * @author chenmin
 *
 */
@SuppressWarnings("unchecked")
public class DefaultSchemaWork implements Work {
	public static final Log log = LogFactory.getLog(DefaultSchemaWork.class);
	
	public MetaMap pkg = null;
	
	public int count = 0;
	
	Dialect dialect = null;
	
	public String sql = "";
	
	@Override
	public String getSql() {
		return sql;
	}

	public DefaultSchemaWork(Dialect dialect, MetaMap pkg) {
		this.dialect = dialect;
		this.pkg = pkg;
	}

	public void execute(Connection conn) throws SQLException {
		if (Exp.isTrue(pkg.get("nottable"))) {
			return;
		}
		
		log.info("updating " + pkg.get("name") + " schema... ");
		
		DatabaseMetaData dbmd = conn.getMetaData(); 
		PreparedStatement ps = null;
		ResultSet colRet = null;
		ResultSet tableRet = null;
		
		List<String> indexSqls = new ArrayList<String>();
		ORMUtil.arrange(pkg);
		
		List<MetaMap> classes = (List<MetaMap>)pkg.get("classes");
		for (MetaMap oclass : classes) {
			if (Exp.isTrue(oclass.get("nottable"))
				|| Exp.isTrue(oclass.get("outside")) ) {
				continue;
			}
			
			log.info("	checking " + oclass.get("tableName") + " schema");
			
			boolean hasTable = false;
			try {
				tableRet = dbmd.getTables(null, "%", oclass.str("tableName"), null);
				hasTable = tableRet.next();
				if (!hasTable) {
					tableRet.close();
					tableRet = dbmd.getTables(null, "%", oclass.str("tableName").toUpperCase(), null);
					hasTable = tableRet.next();
				}
				
				if (hasTable) {
					updateTable(conn, dbmd, oclass, ps, colRet, tableRet, indexSqls);
				} else {
					createTable(conn, oclass, ps, indexSqls);
				}
			} finally {
				if (tableRet != null)
					tableRet.close();
				
				if (colRet != null)
					colRet.close();
				
				if (ps != null)
					ps.close();
			}
		}
		
		for (String sql : indexSqls) {
			try {
				ps = conn.prepareStatement(sql);log.info(sql);
				this.sql += sql += "\n\n/*----------*/\n\n";
				
				count += ps.executeUpdate();
			} catch(SQLException e) {
				log.warn("execute sql " + sql + " fail:" + e.getMessage() );
			} finally {
				if (ps != null)
					ps.close();
			}
		}
	}

	public void updateTable(Connection conn, DatabaseMetaData dbmd
		, MetaMap oclass, PreparedStatement ps
		, ResultSet colRet, ResultSet tableRet, List<String> indexSqls) throws SQLException {
		MetaMap toClass = null;
		
		String colType;
		String fieldType;
		Integer datasize = 0;
		Integer digits = 0; 
		boolean nullable = false; 
		boolean required = false;
		Integer length;
		Integer scale;
		

		boolean lengthType = false;
		
		String tableName = (oclass.str("tableName"));
		String columnName = "";
		
		String sql = "";

		boolean hasColumn = false;
		colRet = dbmd.getColumns(null,"%", tableName, "id");
		hasColumn = colRet.next();
		if (!hasColumn) {
			colRet.close();
			colRet = dbmd.getColumns(null,"%", tableName.toUpperCase(), "ID");
			hasColumn = colRet.next();
		}
		if (hasColumn) {
			fieldType = dialect.dbtypes.get( oclass.str("idtype") );
			colType = colRet.getString("TYPE_NAME");
			boolean b1 = !(colType.contains(fieldType) || fieldType.contains(colType));
			if (b1) {
				sql = "alter table " + tableName + dialect.alterKeyword 
					+ " id " + fieldType
					+ (oclass.get("idtype").equals("String") ? "(255)" : "") 
					+ dialect.getIdCreateClause(oclass);
				try {
					ps = conn.prepareStatement(sql);log.info(sql);
					
					count += ps.executeUpdate();
				} catch (SQLException e) {
					log.warn("update " + tableName + ".id fail:" + e.getLocalizedMessage() );
				} finally {
					if (ps != null)
						ps.close();
				}
			}
		}
		
		List<MetaMap> fields = (List<MetaMap>)oclass.get("fields");
		for (MetaMap field : fields) {
			if (Exp.isTrue(field.get("notcolumn")) ) {
				continue;
			}
			columnName = (String)field.get("columnName");
			fieldType = dialect.dbtypes.get( field.str("type") );
			required = Exp.isTrue(field.get("required"));

			length = Exp.isNull(field.get("length")) ? 0 : field.num("length");
			scale = Exp.isNull(field.get("scale")) ? 0 : field.num("scale");
			
			hasColumn = false;
			colRet = dbmd.getColumns(null,"%", tableName, columnName);
			hasColumn = colRet.next();
			if (!hasColumn) {
				colRet.close();
				colRet = dbmd.getColumns(null,"%", tableName.toUpperCase(), columnName.toUpperCase());
				hasColumn = colRet.next();
			}
			if (hasColumn) { // 修改字段
				colType = colRet.getString("TYPE_NAME");
				datasize = colRet.getInt("COLUMN_SIZE");
				digits = colRet.getInt("DECIMAL_DIGITS"); 
				nullable = colRet.getBoolean("NULLABLE");
				lengthType = dialect.lengthTypes.contains(field.get("type"));
				boolean b1 = !(colType.contains(fieldType) || fieldType.contains(colType));
				boolean b2 = datasize.intValue() != length.intValue() && lengthType;
				boolean b3 = digits.intValue() != scale.intValue() && lengthType;
				boolean b4 = nullable == required;
				if ( b1 || b2 || b3 || b4 ) {
					sql = "alter table " + tableName + dialect.alterKeyword + columnName;
					sql += genColumnInfo(field, fieldType, required, length, scale);
					
					try {
						ps = conn.prepareStatement(sql);log.info(sql);
						
						count += ps.executeUpdate();
					} catch (SQLException e) {
						log.warn("update " + tableName + "." + columnName + " fail:" + e.getLocalizedMessage() );
					} finally {
						if (ps != null)
							ps.close();
					}
				}
			} else { // 创建字段
				sql = "alter table "+tableName + " add " + columnName;
				sql += genColumnInfo(field, fieldType, required, length, scale);
				
				try {
					ps = conn.prepareStatement(sql);log.info(sql);
					
					count += ps.executeUpdate();
				} catch (SQLException e) {
					log.warn("update " + tableName + "." + columnName + " fail:" + e.getLocalizedMessage() );
				} finally {
					if (ps != null)
						ps.close();
				}
					
			}
			colRet.close();
		}
		
		String toTableName = null;
		String fkName = null;
		
		fields = oclass.listmap("many2ones");
		for (MetaMap field : fields) {
			if (Exp.isTrue(field.get("notcolumn")) ) {
				continue;
			}
			
			toClass = field.map("toClass");
			//System.out.println("类" + oclass.name() + "寻找关联类" + field.str("toid"));
			columnName = field.str("columnName");
			fieldType = dialect.dbtypes.get( field.str("type") );
			length = Exp.isNull(field.get("length")) ? 0 : field.num("length");
			scale = Exp.isNull(field.get("scale")) ? 0 : field.num("scale");
			required = Exp.isTrue(field.get("required"));
			
			colRet = dbmd.getColumns(null,"%", tableName, columnName);
			hasColumn = colRet.next();
			if (!hasColumn) {
				colRet.close();
				colRet = dbmd.getColumns(null,"%", tableName.toUpperCase(), columnName.toUpperCase());
				hasColumn = colRet.next();
			}
			
			if (!hasColumn) { // 创建字段
				
				sql = "alter table "+ tableName + " add " + columnName;
				sql += " " + dialect.dbtypes.get( "Long" );
				try {
					ps = conn.prepareStatement(sql);log.info(sql);

					this.sql += sql += "\n\n/*----------*/\n\n";
					
					count += ps.executeUpdate();
				} catch (SQLException e) {
					log.warn("update " + tableName + "." + columnName + " fail:" + e.getLocalizedMessage() );
				} finally {
					if (ps != null)
						ps.close();
				}
				
			}

			toTableName = toClass.str("tableName");
			fkName = tableName + "_fk_" + field.get("name");
			
			colRet.close();
			colRet = conn.createStatement().executeQuery(
				"SELECT * FROM `information_schema`.`KEY_COLUMN_USAGE` where constraint_name='"+fkName+"'");
			if (!colRet.next()) {
				indexSqls.add(" alter table "+tableName +" add index "+ fkName +" ("+field.get("columnName")+")"
					+ ", add constraint "+ fkName +" foreign key ("+field.get("columnName")+") references "+toTableName+" ("+toClass.get("idname")+")");
			}
			
			colRet.close();
		}
	}

	public String genColumnInfo(MetaMap field, String fieldType, boolean required, Integer length, Integer scale) {
		boolean noLengthType = !dialect.lengthTypes.contains(field.get("type"));
		
		String result = " " + fieldType
			+ (Exp.isNull(length) || noLengthType ? "" : " (" + length 
					+ (Exp.isNull(scale) ? "" : " , " + scale) + ")")
			+ (Exp.isTrue(required) ? " not null " : "");
		
		return result;
	}

	public void createTable(Connection conn, MetaMap oclass, PreparedStatement ps, List<String> indexSqls) throws SQLException {
		String sql = "create table " + oclass.get("tableName") + "( " + oclass.get("idname") + " " 
			+ dialect.dbtypes.get(oclass.str("idtype") ) 
			+ (oclass.get("idtype").equals("String") ? "(255)" : "") 
			+ dialect.getIdCreateClause(oclass);
		List<MetaMap> fields = oclass.listmap("fields");
		MetaMap toClass = null;

		String fieldType;
		boolean required = false;
		Integer length;
		Integer scale;
		
		String tableName = oclass.str("tableName");
		for (MetaMap field : fields) {
			if (Exp.isTrue(field.get("notcolumn")) ) {
				continue;
			}

			fieldType = dialect.dbtypes.get( field.str("type") );
			length = Exp.isNull(field.get("length")) ? 0 : field.num("length");
			scale = Exp.isNull(field.get("scale")) ? 0 : field.num("scale");
			required = Exp.isTrue(field.get("required"));
			
			
			sql += ", " + field.get("columnName") + genColumnInfo(field, fieldType, required, length, scale);
		}
		
		fields = oclass.listmap("many2ones");
		String toTableName = null;
		String fkName = null;
		for (MetaMap field : fields) {
			if (Exp.isTrue(field.get("notcolumn")) ) {
				continue;
			}
			toClass = field.map("toClass");
			toTableName = toClass.str("tableName");
			fkName = tableName + "_fk_" + field.get("name");
			
			sql += ", " + field.get("columnName") + " " + dialect.dbtypes.get( "Long" );
			
			ResultSet colRet = conn.createStatement().executeQuery(
				"SELECT * FROM `information_schema`.`KEY_COLUMN_USAGE` where constraint_name='"+fkName+"'");
			if (!colRet.next()) {
			indexSqls.add(" alter table "+tableName+" add index "+fkName+" ("+field.get("columnName")+")"
				+ ", add constraint "+fkName+" foreign key ("+field.get("columnName")+") references "+toTableName+" ("+toClass.get("idname")+")");
			}
			colRet.close();
		}
		sql += ", " + dialect.getPrimaryKeyClause(oclass) + " ) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		
		try {
			ps = conn.prepareStatement(sql);log.info(sql);
			this.sql += sql += "\n\n/*----------*/\n\n";
			
			count += ps.executeUpdate();
		} catch (SQLException e) {
			log.warn("create " + tableName + " fail:" + e.getErrorCode() + " | " + e.getSQLState() + " | " + e.getLocalizedMessage() );
		} finally {
			if (ps != null)
				ps.close();
		}
	}
}
