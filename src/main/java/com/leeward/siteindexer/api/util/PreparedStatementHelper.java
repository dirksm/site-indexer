package com.leeward.siteindexer.api.util;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class PreparedStatementHelper implements PreparedStatement {

	private PreparedStatement prepStmt;
	private String[] values;

	public PreparedStatementHelper(PreparedStatement prepStmt) throws SQLException {
		this.prepStmt = prepStmt;
		this.values = new String[this.prepStmt.getParameterMetaData().getParameterCount()];
	}

	public String getParameter(int index) {
		String value = this.values[index - 1];
		return String.valueOf(value);
	}

	private void setParameter(int index, Object value) {
		String valueStr = "";
		if (value instanceof String) {
			valueStr = "'" + String.valueOf(value).replaceAll("'", "''") + "'";
		} else if (value instanceof Integer) {
			valueStr = String.valueOf(value);
		} else if (value instanceof Date || value instanceof Time || value instanceof Timestamp) {
			valueStr = "'" + String.valueOf(value) + "'";
		} else {
			valueStr = String.valueOf(value);
		}
		this.values[index - 1] = valueStr;
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		return this.prepStmt.executeQuery(sql);
	}

	public int executeUpdate(String sql) throws SQLException {
		return this.prepStmt.executeUpdate(sql);
	}

	public void close() throws SQLException {
		this.prepStmt.close();

	}

	public int getMaxFieldSize() throws SQLException {
		return this.prepStmt.getMaxFieldSize();
	}

	public void setMaxFieldSize(int max) throws SQLException {
		this.prepStmt.setMaxFieldSize(max);
	}

	public int getMaxRows() throws SQLException {
		return this.prepStmt.getMaxRows();
	}

	public void setMaxRows(int max) throws SQLException {
		this.prepStmt.setMaxRows(max);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		this.prepStmt.setEscapeProcessing(enable);
	}

	public int getQueryTimeout() throws SQLException {
		return this.prepStmt.getQueryTimeout();
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		this.prepStmt.setQueryTimeout(seconds);
	}

	public void cancel() throws SQLException {
		this.prepStmt.cancel();
	}

	public SQLWarning getWarnings() throws SQLException {
		return this.prepStmt.getWarnings();
	}

	public void clearWarnings() throws SQLException {
		this.prepStmt.clearWarnings();
	}

	public void setCursorName(String name) throws SQLException {
		this.prepStmt.setCursorName(name);
	}

	public boolean execute(String sql) throws SQLException {
		return this.prepStmt.execute(sql);
	}

	public ResultSet getResultSet() throws SQLException {
		return this.prepStmt.getResultSet();
	}

	public int getUpdateCount() throws SQLException {
		return this.prepStmt.getUpdateCount();
	}

	public boolean getMoreResults() throws SQLException {
		return this.prepStmt.getMoreResults();
	}

	public void setFetchDirection(int direction) throws SQLException {
		this.prepStmt.setFetchDirection(direction);
	}

	public int getFetchDirection() throws SQLException {
		return this.prepStmt.getFetchDirection();
	}

	public void setFetchSize(int rows) throws SQLException {
		this.prepStmt.setFetchSize(rows);
	}

	public int getFetchSize() throws SQLException {
		return this.prepStmt.getFetchSize();
	}

	public int getResultSetConcurrency() throws SQLException {
		return this.prepStmt.getResultSetConcurrency();
	}

	public int getResultSetType() throws SQLException {
		return this.prepStmt.getResultSetType();
	}

	public void addBatch(String sql) throws SQLException {
		this.prepStmt.addBatch(sql);
	}

	public void clearBatch() throws SQLException {
		this.prepStmt.clearBatch();
	}

	public int[] executeBatch() throws SQLException {
		return this.prepStmt.executeBatch();
	}

	public Connection getConnection() throws SQLException {
		return this.prepStmt.getConnection();
	}

	public boolean getMoreResults(int current) throws SQLException {
		return this.prepStmt.getMoreResults(current);
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		return this.prepStmt.getGeneratedKeys();
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return this.prepStmt.executeUpdate(sql, autoGeneratedKeys);
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return this.prepStmt.executeUpdate(sql, columnIndexes);
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return this.prepStmt.executeUpdate(sql, columnNames);
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return this.prepStmt.execute(sql, autoGeneratedKeys);
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return this.prepStmt.execute(sql, columnIndexes);
	}

	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return this.prepStmt.execute(sql, columnNames);
	}

	public int getResultSetHoldability() throws SQLException {
		return this.prepStmt.getResultSetHoldability();
	}

	public boolean isClosed() throws SQLException {
		return this.prepStmt.isClosed();
	}

	public void setPoolable(boolean poolable) throws SQLException {
		this.prepStmt.setPoolable(poolable);
	}

	public boolean isPoolable() throws SQLException {
		return this.prepStmt.isPoolable();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.prepStmt.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.prepStmt.isWrapperFor(iface);
	}

	public ResultSet executeQuery() throws SQLException {
		return this.prepStmt.executeQuery();
	}

	public int executeUpdate() throws SQLException {
		return this.prepStmt.executeUpdate();
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.prepStmt.setNull(parameterIndex, sqlType);
		setParameter(parameterIndex, null);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.prepStmt.setBoolean(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.prepStmt.setByte(parameterIndex, x);
		// TODO Add to tree set
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		this.prepStmt.setShort(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		this.prepStmt.setInt(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		this.prepStmt.setLong(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.prepStmt.setFloat(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.prepStmt.setDouble(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		this.prepStmt.setBigDecimal(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		this.prepStmt.setString(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.prepStmt.setBytes(parameterIndex, x);
		// TODO Add to tree set
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.prepStmt.setDate(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.prepStmt.setTime(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.prepStmt.setTimestamp(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.prepStmt.setAsciiStream(parameterIndex, x, length);
	}

	@SuppressWarnings("deprecation")

	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.prepStmt.setUnicodeStream(parameterIndex, x, length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.prepStmt.setBinaryStream(parameterIndex, x, length);
	}

	public void clearParameters() throws SQLException {
		this.prepStmt.clearParameters();
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.prepStmt.setObject(parameterIndex, x, targetSqlType);
		setParameter(parameterIndex, x);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.prepStmt.setObject(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public boolean execute() throws SQLException {
		return this.prepStmt.execute();
	}

	public void addBatch() throws SQLException {
		this.prepStmt.addBatch();
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		this.prepStmt.setCharacterStream(parameterIndex, reader, length);
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException {
		this.prepStmt.setRef(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		this.prepStmt.setBlob(parameterIndex, x);
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		this.prepStmt.setClob(parameterIndex, x);
	}

	public void setArray(int parameterIndex, Array x) throws SQLException {
		this.prepStmt.setArray(parameterIndex, x);
		// TODO Add to tree set
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return this.prepStmt.getMetaData();
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		this.prepStmt.setDate(parameterIndex, x, cal);
		setParameter(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		this.prepStmt.setTime(parameterIndex, x, cal);
		setParameter(parameterIndex, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		this.prepStmt.setTimestamp(parameterIndex, x, cal);
		setParameter(parameterIndex, x);
	}

	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.prepStmt.setNull(parameterIndex, sqlType, typeName);
		setParameter(parameterIndex, null);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		this.prepStmt.setURL(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this.prepStmt.getParameterMetaData();
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		this.prepStmt.setRowId(parameterIndex, x);
		setParameter(parameterIndex, x);
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		this.prepStmt.setNString(parameterIndex, value);
		setParameter(parameterIndex, value);
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		this.prepStmt.setNCharacterStream(parameterIndex, value, length);
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		this.prepStmt.setNClob(parameterIndex, value);
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.prepStmt.setClob(parameterIndex, reader, length);
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		this.prepStmt.setBlob(parameterIndex, inputStream, length);
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.prepStmt.setNClob(parameterIndex, reader, length);
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		this.prepStmt.setSQLXML(parameterIndex, xmlObject);
		setParameter(parameterIndex, xmlObject);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		this.prepStmt.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
		setParameter(parameterIndex, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.prepStmt.setAsciiStream(parameterIndex, x, length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.prepStmt.setBinaryStream(parameterIndex, x, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		this.prepStmt.setCharacterStream(parameterIndex, reader, length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		this.prepStmt.setAsciiStream(parameterIndex, x);
		// TODO Add to tree set
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		this.prepStmt.setBinaryStream(parameterIndex, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		this.prepStmt.setCharacterStream(parameterIndex, reader);
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		this.prepStmt.setNCharacterStream(parameterIndex, value);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		this.prepStmt.setClob(parameterIndex, reader);
		// TODO Add to tree set
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		this.prepStmt.setBlob(parameterIndex, inputStream);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		this.prepStmt.setNClob(parameterIndex, reader);
	}

	public void closeOnCompletion() throws SQLException {
		this.prepStmt.closeOnCompletion();

	}

	public boolean isCloseOnCompletion() throws SQLException {
		return this.prepStmt.isCloseOnCompletion();
	}

}