package com.qatrend.pomegranate.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

import com.qatrend.pomegranate.logging.PLogger;

/**
 * Database utility class
 * 
 * <ul>
 * <li>Oracle</li>
 * <li>Driver - "oracle.jdbc.driver.OracleDriver"</li>
 * <li>JDBC Url - jdbc:oracle:thin:@//[HOST][:PORT]/SERVICE e.g. "jdbc:oracle:thin:@//myhost:1521/orcl"</li>
 * </ul>
 * <ul>
 * <li>MySql</li>
 * <li>Driver - "com.mysql.jdbc.Driver"</li>
 * <li>JDBC Url - jdbc:mysql://host:port/database_name OR jdbc:mysql://host:port/database_name?property1=value1&amp;property2=value2. e.g. jdbc:mysql://host:port/database_name?user=root&amp;password=root&amp;database=mydb</li>
 * </ul>
 * <ul>
 * <li>SqlServer</li>
 * <li>Driver - "com.microsoft.sqlserver.jdbc.SQLServerDriver"</li>
 * <li>JDBC Url - jdbc:jtds:sqlserver://host:port/database_name e.g. jdbc:jtds:sqlserver://neptune.acme.com:1433/test</li>
 * </ul>
 * <ul>
 * <li>MSAccess</li>
 * <li>Driver - "sun.jdbc.odbc.JdbcOdbcDriver"</li>
 * <li>JDBC Url - "jdbc:odbc:dsn" e.g. "jdbc:odbc:myodbcdsn"</li>
 * </ul>
 * 
 * @author <a href="http://visitamaresh.com" target=_blank>Amaresh Pattanaik (amaresh@visitamaresh.com)</a>
 *
 */
public class DbUtil {
	public static enum DbType {
		ORACLE, MYSQL, SQLSERVER, MSACCESS
	}
	private static String sJdbcOraDriver = "oracle.jdbc.driver.OracleDriver";
	private static String sJdbcMySqlDriver = "com.mysql.jdbc.Driver";
	private static String sJdbcSqlServerDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static String sJdbcMsAccessDriver = "sun.jdbc.odbc.JdbcOdbcDriver";
	
	public static int getRowCount(DbType dbType, String jdbcUrl, String dbUser, String dbPassword, String sqlQuery){
		int rowCount = 0;
		String jdbcDriver = null;
		
		try{
			switch(dbType) {
			case ORACLE:
				jdbcDriver = sJdbcOraDriver;
				break;
			case MYSQL:
				jdbcDriver = sJdbcMySqlDriver;
				break;
			case SQLSERVER:
				jdbcDriver = sJdbcSqlServerDriver;
				break;
			case MSACCESS:
				jdbcDriver = sJdbcMsAccessDriver;
				break;
			default:
				jdbcDriver = sJdbcOraDriver;
				break;
			}
			
			Class.forName( jdbcDriver );
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
			PreparedStatement stmnt = conn.prepareStatement(sqlQuery);
			ResultSet rs = stmnt.executeQuery();
			while(rs.next()){
				rowCount++;
			}
			conn.close();
			
		} catch (Exception ex) {
			PLogger.getLogger().error(ex);
		}
		
		return rowCount;
	}

	public static ArrayList<HashMap<String, String>> getQueryResults(DbType dbType, String jdbcUrl, String dbUser, String dbPassword, String sqlQuery){
		ArrayList<HashMap<String, String>> qResults = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rowMap = new HashMap<String, String>();
		String jdbcDriver = null;
		String colLabel = null;
		String colVal = null;
		int numberOfColumns = 0;
		
		try{
			switch(dbType) {
			case ORACLE:
				jdbcDriver = sJdbcOraDriver;
				break;
			case MYSQL:
				jdbcDriver = sJdbcMySqlDriver;
				break;
			case SQLSERVER:
				jdbcDriver = sJdbcSqlServerDriver;
				break;
			case MSACCESS:
				jdbcDriver = sJdbcMsAccessDriver;
				break;
			default:
				jdbcDriver = sJdbcOraDriver;
				break;
			}
			
			Class.forName( jdbcDriver );
			Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
			PreparedStatement stmnt = conn.prepareStatement(sqlQuery);
			ResultSet rs = stmnt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
		    numberOfColumns = rsmd.getColumnCount();
			while(rs.next()){
			    for(int i=1; i<=numberOfColumns; i++){
			    	colLabel = rsmd.getColumnLabel(i);
			    	colVal = rs.getString(colLabel);
			    	rowMap.put(colLabel, colVal);
			    }
			    qResults.add(rowMap);
			}
			conn.close();
			stmnt.close();
			
		} catch (Exception ex) {
			PLogger.getLogger().error(ex);
		}
		
		return qResults;
	}
	
}
