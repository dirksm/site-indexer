package com.leeward.siteindexer.api.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leeward.siteindexer.api.models.ConnectionInfoModel;

public class DBUtil {
	
	private static String DB_URL = "";
	private static String DB_USER = "";
	private static String DB_PASSWORD = "";
	private static String DB_DRIVER = "";
	
	private static Logger log = LoggerFactory.getLogger(DBUtil.class);

	private static GenericObjectPool defaultConnectionPool = null;
	private static DataSource defDs = null;
	private static HashMap<String, GenericObjectPool> pools = new HashMap<String, GenericObjectPool>();
	
	public static void init(ConnectionInfoModel params) {
		DB_URL = params.getUrl();
		DB_USER = params.getUser();
		DB_PASSWORD = params.getPassword();
		DB_DRIVER = params.getDriverClassName();
		
	}
	
	private static ConnectionInfoModel getConnectionInfo() {
		String username = PropertyConfigManager.getString("db.username", "");
		String password = PropertyConfigManager.getString("db.password", "");
		String url = PropertyConfigManager.getString("db.url", "");
		String driver = PropertyConfigManager.getString("db.driver", "");
		return new ConnectionInfoModel(url, username, password, driver);
	}
	
	
	/**
	 * Sets up and retrieves the data source.
	 * @return {@link DataSource} object
	 * @throws Exception
	 */
	public static DataSource getDataSource() throws Exception {
		if (defaultConnectionPool == null) {
			init();
		}
		return defDs;
	}
	
	/**
	 * Sets up the connectionPool, ConnectionFactory, and PoolableConnectionFactory as well as the DataSource.
	 * @throws Exception
	 */
	private static void init()  throws Exception {
		
		init(getConnectionInfo());
		Class.forName(DB_DRIVER).newInstance();

		defaultConnectionPool = new GenericObjectPool();
		defaultConnectionPool.setMaxActive(10);
		
		ConnectionFactory cf = new DriverManagerConnectionFactory(
				DB_URL,
				DB_USER,
				DB_PASSWORD);

		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, defaultConnectionPool, null, "select 1", false, true);
		defDs = new PoolingDataSource(defaultConnectionPool);
		pools.put("default", defaultConnectionPool);
	}

	
	
	
	/**
	 * Sets up the datasource and retrieves the connection from the datasource.
	 * @return {@link Connection} object
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception{
		return getDataSource().getConnection();
	}
	
	/**
	 * Iterates through the list of connection pools and closes each one.
	 * @throws Exception
	 */
	public static void closeConnectionPools() throws Exception {
		Set<String> keys = pools.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			GenericObjectPool connectionPool = pools.get(key);
			connectionPool.clear();
			connectionPool.close();
		}
	}
	
	
	/**
	 * Closes and returns connection object back to pool
	 * @param stmt Statement
	 * @param rs Result Set
	 * @param conn Connection
	 */
	public static void returnObjects(Statement stmt, ResultSet rs, Connection conn) {
		try {
			stmt.close();
		} catch (Exception e) {
		}
		
		try {
			rs.close();
		} catch (Exception e) {
		}
		
		try {
			conn.close();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Closes and returns connection object back to pool
	 * @param pstmt Prepared Statement
	 * @param rs Result Set
	 * @param conn Connection
	 */
	public static void returnObjects(PreparedStatement pstmt, ResultSet rs, Connection conn) {
		try {
			pstmt.close();
		} catch (Exception e) {
		}
		
		try {
			rs.close();
		} catch (Exception e) {
		}
		
		try {
			conn.close();
		} catch (Exception e) {
		}
	}
}
