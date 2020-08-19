/**  
 * Copyright �? 2011 鎴愰兘鏄犳疆绉戞妧鏈夐檺鍏�?. All rights reserved.
 *
 * @Title: PGProxy.java
 * @Porject: A_TestDemo
 * @Package: com.proxy
 * @Description: TODO
 * @author: Administrator  
 * @date: 2017骞�3鏈�6鏃� 涓嬪�?1:44:43
 * @version: V1.0  
 */
package Sql;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: PGProxy
 * @Description: TODO
 * @author: <a href="mailto:Administrator@sunsharp.cn">Administrator</a>
 * @date: 2017骞�3鏈�6鏃� 涓嬪�?1:44:43
 */
public class PGProxy implements Proxy {

    private GenericDaoHelper greenplumDaoHelper = null;
    private String schema = null;

    public PGProxy(String url, String username, String password, String schema) {
	this.schema = schema;
	this.greenplumDaoHelper = new GenericDaoHelper(url, username, password);
    }

    public PGProxy(InputStream inputStream) {
	Properties p = new Properties();
	try {
	    p.load(inputStream);
	    inputStream.close();
	    this.greenplumDaoHelper = new GenericDaoHelper(
		    p.getProperty("url"), p.getProperty("name"),
		    p.getProperty("password"), "");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public PGProxy(String propertiesName) {
		// TODO Auto-generated constructor stub
		File file = new File("/home/hexuejian/"+propertiesName);
		if(!file.exists()){
			file = new File("/home/huanglei/"+propertiesName);
			if(!file.exists()){
				file = new File("/home/wangdonglan/"+propertiesName);
			}
		}
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			Properties p = new Properties();
		    try {
			p.load(fileInputStream);
			fileInputStream.close();
			this.greenplumDaoHelper = new GenericDaoHelper(p.getProperty("url"), p.getProperty("name"), p.getProperty("password"));
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
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
    public void insertWithId(Object obj, String tableName) {
	// TODO Auto-generated method stub
	Connection conn = greenplumDaoHelper.getConnection();
	if (obj instanceof List) {
	    try {
		conn.setAutoCommit(false);
		List lists = (List) obj;
		if (lists == null || lists.size() == 0)
		    return;
		String insertSql = getInsertSql(lists.get(0), tableName, false);
		PreparedStatement ps = conn.prepareStatement(insertSql);
		for (Object o : lists) {
		    addPreparedStatement(ps, o);
		    ps.addBatch();
		}
		ps.executeBatch();
		conn.commit();
		ps.close();
		conn.close();
		return;
	    } catch (Exception e) {
		e.printStackTrace();
		return;
	    } finally {
		greenplumDaoHelper.release(conn, null, null);
	    }
	} else {
	    String sql = getSQLOfInsert(obj, tableName);
	    PreparedStatement ps = null;
	    try {
		ps = conn.prepareStatement(sql);
		ps.execute();
		ps.close();
		conn.close();
		return;
	    } catch (SQLException e) {
		e.printStackTrace();
		return;
	    } finally {
		greenplumDaoHelper.release(conn, null, null);
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
    public void insert(Object obj, String tableName) {
	// TODO Auto-generated method stub
	Connection conn = greenplumDaoHelper.getConnection();
	if (obj instanceof List) {
	    try {
		conn.setAutoCommit(false);
		List lists = (List) obj;
		String insertSql = getInsertSql(lists.get(0), tableName, true);
		System.out.println(insertSql);
		PreparedStatement ps = conn.prepareStatement(insertSql);
		for (Object o : lists) {
		    Field[] declaredFields = o.getClass().getDeclaredFields();
		    for (int i = 1; i < declaredFields.length; i++) {
			Field field = declaredFields[i];
			String name = field.getType().getSimpleName();
			String methodName = "get"
				+ field.getName().substring(0, 1).toUpperCase()
				+ field.getName().substring(1);
			Method method = o.getClass().getMethod(methodName);
			if (name.equals("int") || name.equals("Integer")) {
			    int num = (Integer) method.invoke(o);
			    ps.setInt(i, num);
			} else if (name.equals("Double")
				|| name.equals("double")) {
			    double dnum = (Double) method.invoke(o);
			    ps.setDouble(i, dnum);
			} else if (name.equals("String")) {
			    String string = (String) method.invoke(o);
			    ps.setString(i, string);

			} else if (name.equals("Date")) {
			    Date date = (Date) method.invoke(o);
			    java.sql.Date d = new java.sql.Date(date.getTime());
			    ps.setDate(i, d);
			} else if (name.equals("long") || name.equals("Long")) {
			    Long lg = (Long) method.invoke(o);
			    ps.setLong(i, lg);
			} else {
			    System.out.println("鏁版嵁绫诲�?�寮傚父!");
			}
		    }
		    ps.addBatch();
		}
		ps.executeBatch();
		conn.commit();
		ps.close();
		conn.close();
		return;
	    } catch (SQLException e) {
		e.printStackTrace();
	    } catch (NoSuchMethodException e) {
		e.printStackTrace();
	    } catch (SecurityException e) {
		e.printStackTrace();
	    } catch (IllegalAccessException e) {
		e.printStackTrace();
	    } catch (IllegalArgumentException e) {
		e.printStackTrace();
	    } catch (InvocationTargetException e) {
		e.printStackTrace();
	    } finally {
		greenplumDaoHelper.release(conn, null, null);
	    }
	} else {
	    String sql = getSQLOfInsert(obj, tableName);
	    PreparedStatement ps = null;
	    try {
		ps = conn.prepareStatement(sql);
		ps.execute();
		ps.close();
		conn.close();
		return;
	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		greenplumDaoHelper.release(conn, null, null);
	    }
	}
	return;
    }

    /**
     * @Title: insert
     * @Description: TODO
     * @param sql
     * @see com.proxy.Proxy#insert(java.lang.String)
     */
    @Override
    public void insert(String sql) {
	dealSql(sql);
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
	Connection conn = greenplumDaoHelper.getConnection();
	Statement st = null;
	try {
	    conn.setAutoCommit(false);
	    st = conn.createStatement();
	    for (String sql : sqls) {
		st.addBatch(sql);
	    }
	    st.executeBatch();
	    conn.commit();
	    st.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, st, null);
	}
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
	// TODO Auto-generated method stub
	String sql = getSQLOfInsert(obj, tableName);
	dealSql(sql);
    }

    /**
     * @Title: update
     * @Description: TODO
     * @param obj
     * @param tableName
     * @throws IntrospectionException
     * @see com.proxy.Proxy#update(java.lang.Object, java.lang.String)
     */
    @Override
    public void update(Object obj, String tableName) {
	// TODO Auto-generated method stub
	final Class<?> clazz = obj.getClass();
	// 鎷兼帴sql
	final StringBuilder sql = new StringBuilder("");
	sql.append("update ").append(schema).append(".").append(tableName)
		.append(" set ");
	// 鑾峰緱�?�楁�?
	String idFieldName = "";
	Object idFieldValue = "";
	final Field[] fields = clazz.getDeclaredFields();
	for (final Field field : fields) {
	    PropertyDescriptor pd = null;
	    try {
		pd = new PropertyDescriptor(field.getName(), clazz);
		if (field.getName().equals("id")) {
		    // idFieldName = replaceName(field.getName());
		    idFieldName = field.getName();
		    idFieldValue = pd.getReadMethod().invoke(obj);
		} else if (!field.getName().equals("id")) {
		    String cols = field.getName();
		    Object values = pd.getReadMethod().invoke(obj);
		    if (cols.length() > 0 && values != null
			    && values.toString().length() > 0) {

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
			sql.append("\"" + cols + "\"" + " = " + values);
		    }
		}
	    } catch (IntrospectionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}
	sql.deleteCharAt(sql.length() - 1).append(" where ")
		.append(idFieldName).append(" = ").append(idFieldValue)
		.append(";");
	dealSql(sql + "");
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
	Connection conn = null;
	Statement st = null;
	try {
	    conn = greenplumDaoHelper.getConnection();
	    conn.setAutoCommit(false);
	    st = conn.createStatement();
	    st.addBatch(sql);
	    st.executeBatch();
	    conn.commit();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, st, null);
	}
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
	Statement st = null;
	try {
	    conn = greenplumDaoHelper.getConnection();
	    conn.setAutoCommit(false);
	    st = conn.createStatement();
	    for (String sql : sqls) {
		st.addBatch(sql);
	    }
	    st.executeBatch();
	    conn.commit();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, st, null);
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
	// TODO Auto-generated method stub
	Connection conn = greenplumDaoHelper.getConnection();
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
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, ps, null);
	}
	return false;
    }
    
    public void nativeSql(String sql,List<Object> params) throws Exception {
		Connection conn=null;
		PreparedStatement ps= null;
		try{
			List<Object> fieldValues = new ArrayList<Object>(); //瀛楁鍊�?  
			if(null!=params&&params.size()>0){
				for(Object obj:params){
					fieldValues.add(obj);
				}
			}
			conn = greenplumDaoHelper.getConnection();

			if (fieldValues != null&&fieldValues.size()>0) {  
				ps = conn.prepareStatement(sql);  
				setParameter(fieldValues, ps, false);  
			} else {  
				ps = conn.prepareStatement(sql);  
			}  
			//鎵цSQL  
			ps.execute();  
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("sql="+sql);
			System.out.println("鎵ц閿欒�?!!!");
			conn.rollback();
		}finally{
			greenplumDaoHelper.release(conn,ps, null);  
		}
	}

    /** 
	 * 璁剧疆SQL鍙傛暟鍗犱綅绗︾殑鍊�? 
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
					sb.append(arrayValue[j]).append("銆�");  
				}  
				ps.setString(i, sb.deleteCharAt(sb.length()-1).toString());  
			} else if(clazzValue ==Double.class){
				ps.setObject(i, fieldValue, Types.DOUBLE);
			}else{  
				ps.setObject(i, fieldValue, Types.NUMERIC);  
			}  
		}  
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
	    conn = greenplumDaoHelper.getConnection();
	    ps = conn.prepareStatement(sql);
	    rs = ps.executeQuery();
	    if (rs.next()) {
		result = rs.getObject(1);
	    }
	    return result;
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, ps, rs);
	}
	return null;
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

	return null;
    }

    /**
     * @Title: getListEntity
     * @Description: TODO
     * @param sql
     * @param clazz
     * @return
     * @see com.proxy.Proxy#getListEntity(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> List<T> findAllBySql(String sql, Class<T> clz) {
	// TODO Auto-generated method stub
	List<T> list = new ArrayList<T>();
	Connection conn = greenplumDaoHelper.getConnection();
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
	    ps = conn.prepareStatement(sql);
	    rs = ps.executeQuery();
	    Field[] fields = clz.getDeclaredFields();
	    while (rs.next()) {
		T obj = clz.newInstance();
		initObject(obj, fields, rs);
		list.add(obj);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	}
	// 閲婃斁璧勬簮
	greenplumDaoHelper.release(conn, ps, rs);
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
	Connection conn = greenplumDaoHelper.getConnection();
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
	    ps = conn.prepareStatement(sql);
	    rs = ps.executeQuery();
	    Field[] fields = clz.getDeclaredFields();
	    while (rs.next()) {
		T obj = clz.newInstance();
		initObject(obj, fields, rs);
		return obj;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, ps, rs);
	}
	// 閲婃斁璧勬簮
	return null;
    }

    /**
     * @Title: getListMap
     * @Description: TODO
     * @return
     * @see com.proxy.Proxy#getListMap()
     */
    @Override
    public List<Map<String, Object>> getListMap(String sql) {
	PreparedStatement ps = null;
	Connection conn = null;
	ResultSet rs = null;
	try {
	    conn = greenplumDaoHelper.getConnection();
	    ps = conn.prepareStatement(sql);
	    rs = ps.executeQuery();
	    return ResultToListMap(rs);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, ps, rs);
	}
	return null;
    }

    /**
     * @Title: getListLinkedMap
     * @Description: TODO
     * @return
     * @see com.proxy.Proxy#getListLinkedMap()
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String, Object>> getListLinkedMap(String sql) {
	PreparedStatement ps = null;
	Connection conn = null;
	ResultSet rs = null;
	try {
	    conn = greenplumDaoHelper.getConnection();
	    ps = conn.prepareStatement(sql);
	    rs = ps.executeQuery();
	    return ResultToListMap(rs);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    greenplumDaoHelper.release(conn, ps, rs);
	}
	return null;
    }

    private String getInsertSql(Object obj, String tableName, boolean flag) {
	Class<? extends Object> objClass = obj.getClass();
	Field[] fields = objClass.getDeclaredFields();
	StringBuffer sb = new StringBuffer();
	StringBuffer sb2 = new StringBuffer();
	sb.append("insert into " + schema + "." + tableName + "(");
	sb2.append(") values (");
	for (Field field : fields) {
	    sb.append("\"" + field.getName() + "\",");
	    sb2.append("?,");
	}
	sb2.append(");");
	String sql = sb.toString() + sb2.toString();
	if (!flag) {
	    sql = sql.replace("(\"id\",", "(").replace("(?,", "(");
	}
	sql = sql.replace(",)", ")");
	return sql;
    }

    public static void addPreparedStatement(PreparedStatement ps, Object obj) {
	Field[] declaredFields = obj.getClass().getDeclaredFields();
	for (int i = 1; i < declaredFields.length; i++) {
	    Field field = declaredFields[i];
	    String name = field.getType().getSimpleName();
	    String methodName = "get"
		    + field.getName().substring(0, 1).toUpperCase()
		    + field.getName().substring(1);
	    try {
		Method method = obj.getClass().getMethod(methodName);
		if (name.equals("String")) {
		    String string = (String) method.invoke(obj);
		    ps.setString(i, string);
		} else if (name.equals("Integer") || name.equals("int")) {
		    Integer num = (Integer) method.invoke(obj);
		    if (num != null) {
			ps.setInt(i, num);
		    } else {
			ps.setObject(i, null);
		    }
		} else if (name.equals("Long") || name.equals("long")) {
		    Long num = (Long) method.invoke(obj);
		    if (num != null) {
			ps.setLong(i, num);
		    } else {
			ps.setObject(i, null);
		    }
		} else if (name.equals("Double") || name.equals("double")) {
		    Double num = (Double) method.invoke(obj);
		    if (num != null) {
			ps.setDouble(i, num);
		    } else {
			ps.setObject(i, null);
		    }
		} else if (name.equals("Date")) {
		    try {
			Date date = (Date) method.invoke(obj);
			Timestamp t = new Timestamp(date.getTime());
			ps.setTimestamp(i, t);
		    } catch (Exception e) {
			ps.setDate(i, null);
		    }
		} else if (name.equals("Calendar")) {
		    try {
			Calendar invoke = (Calendar) method.invoke(obj);
			Timestamp datetime = new Timestamp(invoke.getTime()
				.getTime());
			ps.setTimestamp(i, datetime);
		    } catch (Exception e) {
			ps.setDate(i, null);
		    }
		} else {
		    System.out.println("瀛楁绫诲�?�鏈坊鍔狅�?");
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private String getSQLOfInsert(Object obj, String tableName) {
	String strSQL = new String("INSERT INTO " + schema + "." + tableName
		+ "(#COLS) VALUES (#VALS)");
	Class<? extends Object> objClass = obj.getClass();
	Field fields[] = objClass.getDeclaredFields();
	StringBuffer cols = new StringBuffer("");
	StringBuffer values = new StringBuffer("");

	for (Field field : fields) {
	    if (field.getName().toLowerCase().equals("id")) {
		continue;
	    }
	    String methodName = "get"
		    + field.getName().substring(0, 1).toUpperCase()
		    + field.getName().substring(1);
	    try {
		Method method = objClass.getMethod(methodName);
		Object o = method.invoke(obj);
		if (null != o) {
		    cols.append("\"" + field.getName() + "\"" + ",");
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
		    } else if (o instanceof java.util.GregorianCalendar) {
			Calendar c = (Calendar) o;
			Date date = new Date(c.getTime().getTime());
			values.append("'" + date + "',");
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
	    return null;
	}
	return strSQL;
    }

    private void initObject(Object object, Field[] fields, ResultSet rs)
	    throws SQLException, IllegalArgumentException,
	    IllegalAccessException, InvocationTargetException {
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
		if (paramVal != null) {
		    Timestamp tt = (Timestamp) paramVal;
		    Date d = new Date(tt.getTime());
		    paramVal = d;
		} else {
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
	    }
	}
    }

    private List<Map<String, Object>> ResultToListMap(ResultSet rs) throws SQLException {
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	while (rs.next()) {
	    ResultSetMetaData md = rs.getMetaData();
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (int i = 1; i <= md.getColumnCount(); i++) {
		map.put(md.getColumnLabel(i), rs.getObject(i));
	    }
	    list.add(map);
	}
	return list;
    }

    private static List ResultToListMap2Order(ResultSet rs) throws SQLException {
	List list = new ArrayList();
	while (rs.next()) {
	    ResultSetMetaData md = rs.getMetaData();
	    Map map = new LinkedHashMap();
	    for (int i = 1; i <= md.getColumnCount(); i++) {
		map.put(md.getColumnLabel(i), rs.getObject(i));
	    }
	    list.add(map);
	}
	return list;
    }
}
