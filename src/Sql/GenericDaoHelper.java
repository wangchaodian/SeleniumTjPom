package Sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 
 * @author sureKing
 *
 */
public class GenericDaoHelper {

    private GenericDaoHelper factory;

    private DruidDataSource dataSource = new DruidDataSource();

    private static final String GPDRIVER = "org.postgresql.Driver";
    private static final String MYSQLDRIVER = "com.mysql.jdbc.Driver";


    public GenericDaoHelper(String url, String name, String password) {
	initDruidDataSource(url, name, password, dataSource, GPDRIVER);
    }

    public GenericDaoHelper(String url, String name, String password,
	    String driver) {
	initDruidDataSource(url, name, password, dataSource, MYSQLDRIVER);
    }

    private void initDruidDataSource(String url, String name, String password,
	    DruidDataSource dataSource, String driver) {
	// dataSource = new DruidDataSource();
	dataSource.setDriverClassName(driver);
	dataSource.setUrl(url);
	dataSource.setUsername(name);
	dataSource.setPassword(password);
	dataSource.setInitialSize(1);
	dataSource.setMinIdle(1);
	dataSource.setMaxActive(240);
	dataSource.setMaxOpenPreparedStatements(120);
	dataSource.setTestWhileIdle(true);
	dataSource.setTimeBetweenEvictionRunsMillis(500000);
	dataSource.setValidationQuery("select 'x'");
	dataSource.setPoolPreparedStatements(true);
    }



    public Connection getConnection() {
	Connection conn = null;
	try {
	    conn = dataSource.getConnection();
	} catch (SQLException e) {
	    e.printStackTrace();
	    System.out.println("建立连接失败!");
	}
	return conn;
    }

    public void release(Connection conn, PreparedStatement ps, ResultSet rs) {
	try {
	    if (conn != null) {
		conn.close();
		conn = null;
	    }
	    if (ps != null) {
		ps.close();
		ps = null;
	    }
	    if (rs != null) {
		rs.close();
		rs = null;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void release(Connection conn, Statement ps, ResultSet rs) {
	try {
	    if (conn != null) {
		conn.close();
		conn = null;
	    }
	    if (ps != null) {
		ps.close();
		ps = null;
	    }
	    if (rs != null) {
		rs.close();
		rs = null;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}