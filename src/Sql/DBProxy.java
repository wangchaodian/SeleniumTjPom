/**  
 *
 * @Title: DBProxy.java
 * @Porject: A_TestDemo
 * @Package: com.proxy
 * @Description: TODO
 * @author: Administrator  
 * @date: 2017骞�3鏈�6鏃� 涓嬪�?1:44:13
 * @version: V1.0  
 */
package Sql;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: DBProxy
 * @Description: TODO
 * @author: <a href="mailto:Administrator@sunsharp.cn">Administrator</a>
 * @date: 2017骞�3鏈�6鏃� 涓嬪�?1:44:13
 */
public class DBProxy implements Proxy {

	private GenericDaoHelper mysqlDaoHelper = null;

	public DBProxy(String url, String username, String password) {
		this.mysqlDaoHelper = new GenericDaoHelper(url, username, password, "");
	}

	public DBProxy(InputStream inputStream) {
		Properties p = new Properties();
		try {
			p.load(inputStream);
			inputStream.close();
			this.mysqlDaoHelper = new GenericDaoHelper(p.getProperty("url"),
					p.getProperty("user"), p.getProperty("password"), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: insert
	 * @Description: TODO
	 * @param obj
	 * @param tableName
	 * @see com.proxy.Proxy#insert(java.lang.Object, java.lang.String)
	 */
	@Override
	public void insert(Object obj, String tableName) {
		Connection connection = mysqlDaoHelper.getConnection();
		PreparedStatement pst = null;
		if (obj instanceof List) {
			try {
				connection.setAutoCommit(false);
				pst = connection.prepareStatement("");
				for (Object o : (List<?>) obj) {
					String sql = getSQLOfInsert(o, tableName);
					pst.addBatch(sql);
				}
				pst.executeBatch();
				connection.commit();
				pst.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				String sql = getSQLOfInsert(obj, tableName);
				pst = connection.prepareStatement(sql);// 鍑嗗鎵ц璇彞
				pst.execute();
			} catch (Exception e) {

			} finally {
				mysqlDaoHelper.release(connection, pst, null);
			}
		}
	}

	/**
	 * @Title: insertWithId
	 * @Description: TODO
	 * @param obj
	 * @param tableName
	 * @see com.proxy.Proxy#insertWithId(java.lang.Object, java.lang.String)
	 */
	@Override
	public void insertWithId(Object obj, String tableName) {
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: insert
	 * @Description: TODO
	 * @param sql
	 * @see com.proxy.Proxy#insert(java.lang.String)
	 */
	@Override
	public void insert(String sql) {
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: batchInsert
	 * @Description: TODO
	 * @param sqls
	 * @see com.proxy.Proxy#batchInsert(java.util.List)
	 */
	@Override
	public void batchInsert(List<String> sqls) {
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: save
	 * @Description: TODO
	 * @param obj
	 * @param tableName
	 * @see com.proxy.Proxy#save(java.lang.Object, java.lang.String)
	 */
	@Override
	public void save(Object obj, String tableName) {
		Connection connection = null;
		PreparedStatement pst = null;
		try {
			String sql = getSQLOfInsert(obj, tableName);
			if (connection == null) {
				connection = mysqlDaoHelper.getConnection();
			}
			pst = connection.prepareStatement(sql);// 鍑嗗鎵ц璇彞
			pst.execute();
		} catch (Exception e) {

		} finally {
			mysqlDaoHelper.release(connection, pst, null);
		}
	}

	/**
	 * @Title: update
	 * @Description: TODO
	 * @param obj
	 * @param tableName
	 * @throws SQLException
	 * @see com.proxy.Proxy#update(java.lang.Object, java.lang.String)
	 */
	@Override
	public void update(Object clz, String tableName) {
		final Class<?> clazz = clz.getClass();
		// 鎷兼帴sql
		final StringBuilder sql = new StringBuilder("");
		sql.append("update ").append(tableName).append(" set ");
		// 鑾峰緱�?�楁�?
		String idFieldName = "";
		Object idFieldValue = "";
		final Field[] fields = clazz.getDeclaredFields();
		for (final Field field : fields) {
			try {
				final PropertyDescriptor pd = new PropertyDescriptor(
						field.getName(), clazz);
				if (field.getName().equals("id")) {
					idFieldName = field.getName();
					idFieldValue = pd.getReadMethod().invoke(clz);
				} else if (!field.getName().equals("id")) {
					String cols = field.getName();
					Object values = pd.getReadMethod().invoke(clz);
					if (cols.length() > 1 && values != null
							&& values.toString().length() > 1) {

						if (values instanceof Integer || values instanceof Long
								|| values instanceof Double) {
							values = values.toString() + ",";
						} else if (values instanceof String) {
							values = "'"
									+ ((String) values).trim()
											.replace("\'", "\\'")
											.replace("\"", "\\\"") + "',";
						} else if (values instanceof java.util.Date) {
							String datestr = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss")
									.format((java.util.Date) values);
							values = "'" + datestr.trim() + "',";
						} else {
							values = "'" + values.toString() + "',";
						}
						sql.append(cols + " = " + values);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sql.deleteCharAt(sql.length() - 1).append(" where ")
				.append(idFieldName).append(" = ").append(idFieldValue)
				.append(";");
		final Connection conn = mysqlDaoHelper.getConnection();
		// 璁剧疆SQL鍙傛暟鍗犱綅绗︾殑鍊�?
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			// 鎵цSQL
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mysqlDaoHelper.release(conn, ps, null);
	}

	/**
	 * @Title: update
	 * @Description: TODO
	 * @param sql
	 * @see com.proxy.Proxy#update(java.lang.String)
	 */
	@Override
	public void update(String sql) {
		// TODO Auto-generated method stub
		dealSql(sql);
	}

	/**
	 * @Title: batchUpdate
	 * @Description: TODO
	 * @param sqls
	 * @see com.proxy.Proxy#batchUpdate(java.util.List)
	 */
	@Override
	public void batchUpdate(List<String> sqls) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement statenent = null;
		conn = mysqlDaoHelper.getConnection();
		try {
			conn.setAutoCommit(false);
			statenent = conn.createStatement();
			for (String sql : sqls) {
				statenent.addBatch(sql);
			}
			statenent.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mysqlDaoHelper.release(conn, statenent, null);
		}

	}

	/**
	 * @Title: delete
	 * @Description: TODO
	 * @param obj
	 * @param tableName
	 * @see com.proxy.Proxy#delete(java.lang.Object, java.lang.String)
	 */
	@Override
	public void delete(Object obj, String tableName) {
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: dealSql
	 * @Description: TODO
	 * @param sql
	 * @see com.proxy.Proxy#dealSql(java.lang.String)
	 */
	@Override
	public boolean dealSql(String sql) {
		Connection conn = mysqlDaoHelper.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.execute();
			conn.commit();
			ps.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			//System.out.println(sql);
			e.printStackTrace();
		} finally {
			mysqlDaoHelper.release(conn, ps, null);
		}
		return false;
	}

	/**
	 * @Title: getSingle
	 * @Description: TODO
	 * @param sql
	 * @return
	 * @see com.proxy.Proxy#getSingle(java.lang.String)
	 */
	@Override
	public Object getSingle(String sql) {
		Object result = null;
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = mysqlDaoHelper.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getObject(1);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mysqlDaoHelper.release(conn, ps, rs);
		}
		return result;
	}

	/**
	 * @Title: getListString
	 * @Description: TODO
	 * @param sql
	 * @return
	 * @see com.proxy.Proxy#getListString(java.lang.String)
	 */
	@Override
	public List<String> getListString(String sql) {
		List<String> list = null;
		Connection con = mysqlDaoHelper.getConnection();
		PreparedStatement pst = null;
		ResultSet res = null;
		try {
			pst = con.prepareStatement(sql);
			res = pst.executeQuery();
			list = convertList2(res);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mysqlDaoHelper.release(con, pst, res);
		}
		return list;
	}

	/**
	 * @Title: getEntity
	 * @Description: TODO
	 * @param sql
	 * @param clazz
	 * @return
	 * @see com.proxy.Proxy#getEntity(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T getEntity(String sql, Class<T> clz) {
		PreparedStatement ps = null;
		Connection connection = null;
		ResultSet rs = null;
		if (connection == null) {
			connection = mysqlDaoHelper.getConnection();
		}
		try {
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery(sql);
			Field[] fields = clz.getDeclaredFields();
			while (rs.next()) {
				T obj = clz.newInstance();
				initObject(obj, fields, rs);
				return obj;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			// 閲婃斁璧勬簮
			mysqlDaoHelper.release(connection, ps, rs);
		}
		return null;
	}

	/**
	 * 閫氳繃bean瀵硅薄鑾峰彇鎻掑叆鏃剁殑SQL
	 * 
	 * @param obj
	 *            java瀵硅�?
	 * @param tableName
	 *            琛ㄥ�?
	 * @return SQL
	 */
	private static String getSQLOfInsert(Object obj, String tableName) {
		String strSQL = new String("INSERT INTO " + tableName
				+ "(#COLS) VALUES (#VALS)");
		Class<? extends Object> objClass = obj.getClass();
		Field fields[] = objClass.getDeclaredFields();
		StringBuffer cols = new StringBuffer("");
		StringBuffer values = new StringBuffer("");

		for (Field field : fields) {
			String methodName = "get"
					+ field.getName().substring(0, 1).toUpperCase()
					+ field.getName().substring(1);
			try {
				Method method = objClass.getMethod(methodName);
				Object o = method.invoke(obj);
				if (null != o) {
					cols.append(field.getName() + ",");
					// cols.append(replaceName(field.getName()) + ",");
					if (o instanceof Integer || o instanceof Long
							|| o instanceof Double) {
						values.append(o.toString() + ",");
					} else if (o instanceof String) {
						values.append("'"
								+ ((String) o).trim().replace("\'", "\\'")
										.replace("\"", "\\\"") + "',");
					} else if (o instanceof java.util.Date) {
						String datestr = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss")
								.format((java.util.Date) o);
						values.append("'" + datestr.trim() + "',");
					} else {
						values.append("'" + o.toString() + "',");
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
				continue;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				continue;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		if (cols.length() > 1 && values.length() > 1) {
			cols.delete(cols.length() - 1, cols.length());
			values.delete(values.length() - 1, values.length());
			strSQL = strSQL.replace("#COLS", cols).replace("#VALS", values);
		} else {
			System.out.println("绌哄璞℃棤娉曞畬鎴愭搷浣�,璇蜂负灞炴�ц祴鍊�?");
			// LOG.warning("绌哄璞℃棤娉曞畬鎴愭搷浣�,璇蜂负灞炴�ц祴鍊�?");
			return null;
		}
		// LOG.info(strSQL);

		return strSQL;
	}

	/**
	 * @Title: findAllBySql
	 * @Description: TODO
	 * @param sql
	 * @param clazz
	 * @return
	 * @see com.proxy.Proxy#findAllBySql(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> List<T> findAllBySql(String sql, Class<T> clz) {
		List<T> list = new ArrayList<T>();
		PreparedStatement ps = null;
		Connection connection = null;
		ResultSet rs = null;
		if (connection == null) {
			connection = mysqlDaoHelper.getConnection();
		}
		try {
			ps = connection.prepareStatement(sql);
			rs = ps.executeQuery(sql);
			Field[] fields = clz.getDeclaredFields();
			while (rs.next()) {
				T obj = clz.newInstance();
				initObject(obj, fields, rs);
				list.add(obj);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 閲婃斁璧勬簮
		mysqlDaoHelper.release(connection, ps, rs);
		return list;
	}

	/**
	 * 鏍规嵁缁撴灉闆嗗垵濮嬪寲瀵硅�?
	 * 
	 * @throws SQLException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void initObject(Object object, Field[] fields, ResultSet rs)
			throws SQLException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		for (Field field : fields) {
			// String propertyName = replaceName(field.getName());
			String propertyName = field.getName();
			Object paramVal = null;
			Class<?> clazzField = field.getType();
			if (clazzField == String.class) {
				paramVal = rs.getString(propertyName);
			} else if (clazzField == short.class || clazzField == Short.class) {
				paramVal = rs.getShort(propertyName);
			} else if (clazzField == int.class || clazzField == Integer.class) {
				paramVal = rs.getInt(propertyName);
			} else if (clazzField == long.class || clazzField == Long.class) {
				paramVal = rs.getLong(propertyName);
			} else if (clazzField == float.class || clazzField == Float.class) {
				paramVal = rs.getFloat(propertyName);
			} else if (clazzField == double.class || clazzField == Double.class) {
				paramVal = rs.getDouble(propertyName);
			} else if (clazzField == boolean.class
					|| clazzField == Boolean.class) {
				paramVal = rs.getBoolean(propertyName);
			} else if (clazzField == byte.class || clazzField == Byte.class) {
				paramVal = rs.getByte(propertyName);
			} else if (clazzField == char.class
					|| clazzField == Character.class) {
				paramVal = rs.getCharacterStream(propertyName);
			} else if (clazzField == Date.class || clazzField == Calendar.class) {
				paramVal = rs.getTimestamp(propertyName);
				Timestamp tt = (Timestamp) paramVal;
				if (tt != null) {
					Date d = new Date(tt.getTime());
					paramVal = d;
				} else {
					paramVal = null;
				}
			} else if (clazzField.isArray()) {
				paramVal = rs.getString(propertyName).split(","); // 浠ラ�楀彿鍒嗛殧鐨勫瓧绗︿覆
			}
			if (paramVal != null) {
				try {
					String methodName = "set"
							+ field.getName().substring(0, 1).toUpperCase()
							+ field.getName().substring(1);
					Method method = object.getClass().getMethod(methodName,
							clazzField);
					method.invoke(object, paramVal);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				// PropertyDescriptor pd = new
				// PropertyDescriptor(propertyName,object.getClass());
				// pd.getWriteMethod().invoke(field.getName(), paramVal);
			}
		}
	}

	/**
	 * @Title: getListMap
	 * @Description: TODO
	 * @param sql
	 * @return
	 * @see com.proxy.Proxy#getListMap(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getListMap(String sql) {
		List<Map<String, Object>> list = null;
		Connection con = mysqlDaoHelper.getConnection();
		PreparedStatement pst = null;
		ResultSet res = null;
		try {
			pst = con.prepareStatement(sql);
			res = pst.executeQuery();
			list = convertList(res);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mysqlDaoHelper.release(con, pst, res);
		}

		return list;
	}

	/**
	 * @Title: getListLinkedMap
	 * @Description: TODO
	 * @param sql
	 * @return
	 * @see com.proxy.Proxy#getListLinkedMap(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getListLinkedMap(String sql) {
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
		    conn = mysqlDaoHelper.getConnection();
		    ps = conn.prepareStatement(sql);
		    rs = ps.executeQuery();
		    return convertList(rs);
		} catch (SQLException e) {
		    e.printStackTrace();
		} finally {
			mysqlDaoHelper.release(conn, ps, rs);
		}
		return null;
	}

	/**
	 * 鎶婃暟鎹簱杩斿洖鐨勭粨鏋滈�?
	 * 
	 * @param rs
	 * @return List<Map<String,Object>>
	 * @throws SQLException
	 */
	private static List<Map<String, Object>> convertList(ResultSet rs) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount(); // Map rowData;
		while (rs.next()) { // rowData = new HashMap(columnCount);
			Map<String, Object> rowData = new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}

	private static List<String> convertList2(ResultSet rs) throws SQLException {
		List<String> list = new ArrayList<String>();
//		ResultSetMetaData md = rs.getMetaData();
		while (rs.next()) { // rowData = new HashMap(columnCount);
			list.add(rs.getString(1));
		}
		return list;
	}

	/**
	 * 
	 * @param clazz
	 *            瀹炰綋绫荤殑瀹屾暣璺緞
	 * @param tableName
	 *            琛ㄥ�?
	 * @return
	 * @throws ClassNotFoundException
	 */
	public String creatTable(Class<?> clz, String tableName) {
		if (checkTableIsExists(tableName)) {
			System.out.println(tableName + " 琛ㄥ凡�?�樺湪锛�?");
			return null;
		}
		if (tableName != null) {
			StringBuffer sql = new StringBuffer("");
			sql.append("CREATE TABLE " + tableName + " (");
			sql.append(" id bigint(12) NOT NULL AUTO_INCREMENT,");
			Field[] fields = clz.getDeclaredFields();
			Field.setAccessible(fields, true);
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName();
				if (name.equals("id"))
					continue;
				String type = fields[i].getType().getSimpleName();
				fields[i].setAccessible(true);// 璁块棶绉佹湁
				if (type.equals("String")) {// Str
					sql.append("" + name + " varchar(255) DEFAULT NULL,");
				} else if (type.equals("Integer")
						|| type.equals("class java.lang.Integer")
						|| type.equals("int")) {// int
					sql.append("" + name + " int(11) DEFAULT NULL,");
				} else if (type.equals("Double")
						|| type.equals("class java.lang.Double")) {// double
					sql.append("" + name + " decimal(16,4) DEFAULT NULL,");
				} else if (type.equals("Long")
						|| type.equals("class java.lang.Long")) {// long
					sql.append("" + name + " bigint(12) DEFAULT NULL,");
				} else if (type.equals("Date")
						|| type.equals("class java.lang.Date")) {// date
					sql.append("" + name + " datetime DEFAULT NULL,");
				} else {
					System.out.println("杩樻湁娌℃湁鍐欏埌鐨勫瓧娈碉�?" + name);
				}
			}
			sql.append("  PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='鏁版嵁淇℃伅琛�';");
			System.out.println(sql);
			String s = sql + "";
			dealSql(s);
			return tableName;
		} else {
			System.out.println("琛ㄥ悕鍙傛暟閿欒锛屽垱寤鸿〃澶辫触锛�");
			return null;
		}
	}

	/**
	 * 妫�鏌ヨ〃鏄惁瀛樺�?
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean checkTableIsExists(String tableName) {
		Connection conn = null;
		if (conn == null) {
			conn = mysqlDaoHelper.getConnection();
		}
		PreparedStatement pst = null;
		boolean flag = false;
		try {
			String sql = "select table_name FROM information_schema.TABLES where table_name = "
					+ "'" + tableName + "'";
			pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {// 琛ㄧず琛ㄥ瓨鍦�
				flag = true;
				return flag;
			} else {
				flag = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mysqlDaoHelper.release(conn, pst, null);
		}
		return flag;

	}
	public void nativeSql(String sql,List<Object> params) throws Exception {
		Connection conn=null;
		PreparedStatement ps= null;
		try{
			List<Object> fieldValues = new ArrayList<Object>(); //字段�?  
			if(null!=params&&params.size()>0){
				for(Object obj:params){
					fieldValues.add(obj);
				}
			}
			conn = mysqlDaoHelper.getConnection();

			if (fieldValues != null&&fieldValues.size()>0) {  
				ps = conn.prepareStatement(sql);  
				setParameter(fieldValues, ps, false);  
			} else {  
				ps = conn.prepareStatement(sql);  
			}  
			//执行SQL  
			ps.execute();  
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("sql="+sql);
			System.out.println("执行错误!!!");
			conn.rollback();
		}finally{
			mysqlDaoHelper.release(conn,ps, null);  
		}
	}
	/** 
	 * 设置SQL参数占位符的�? 
	 */  
	private void setParameter(List<Object> values, PreparedStatement ps, boolean isSearch)  
			throws SQLException {  
		for (int i = 1; i <= values.size(); i++) {  
			Object fieldValue = values.get(i-1); 
			if(null==fieldValue){
				ps.setNull(i, Types.NUMERIC);
				continue;
			}
			Class<?> clazzValue = fieldValue.getClass();  
			if (clazzValue == String.class) {  
				if (isSearch)   
					ps.setString(i, "%" + (String)fieldValue + "%");  
				else  
					ps.setString(i,(String)fieldValue);  

			} else if (clazzValue == boolean.class || clazzValue == Boolean.class) {  
				ps.setBoolean(i, (Boolean)fieldValue);  
			} else if (clazzValue == byte.class || clazzValue == Byte.class) {  
				ps.setByte(i, (Byte)fieldValue);  
			} else if (clazzValue == char.class || clazzValue == Character.class) {  
				ps.setObject(i, fieldValue,Types.CHAR);  
			} else if (clazzValue == Date.class) {  
				ps.setTimestamp(i, new Timestamp(((Date) fieldValue).getTime()));  
			}else if(clazzValue == Calendar.class||clazzValue==GregorianCalendar.class){
				ps.setTimestamp(i, new Timestamp(((Calendar) fieldValue).getTimeInMillis()));  
			} else if (clazzValue.isArray()) {  
				Object[] arrayValue = (Object[]) fieldValue;  
				StringBuffer sb = new StringBuffer();  
				for (int j = 0; j < arrayValue.length; j++) {  
					sb.append(arrayValue[j]).append("�?");  
				}  
				ps.setString(i, sb.deleteCharAt(sb.length()-1).toString());  
			} else if(clazzValue ==Double.class){
				ps.setObject(i, fieldValue, Types.DOUBLE);
			}else{  
				ps.setObject(i, fieldValue, Types.NUMERIC);  
			}  
		}  
	}

}
