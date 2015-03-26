package com.qatrend.pomegranate.db;

import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Database utility class
 * <b>Oracle</b>
 * <pre>
 * Driver - "oracle.jdbc.driver.OracleDriver"
 * JDBC Url - jdbc:oracle:thin:@//[HOST][:PORT]/SERVICE e.g. "jdbc:oracle:thin:@//myhost:1521/orcl"
 * </pre>
 * 
 * <b> MySql</b>
 * <pre>
 * Driver - com.mysql.jdbc.Driver
 * JDBC Url - jdbc:mysql://host:port/database_name OR 
 * jdbc:mysql://host:port/database_name?property1=value1&amp;property2=value2. e.g. 
 * jdbc:mysql://host:port/database_name?user=root&amp;password=root&amp;database=mydb
 * </pre>
 * 
 * <b> SqlServer</b>
 * <pre>
 * Driver - com.microsoft.sqlserver.jdbc.SQLServerDriver
 * JDBC Url - jdbc:jtds:sqlserver://host:port/database_name e.g. jdbc:jtds:sqlserver://neptune.acme.com:1433/test
 * </pre>
 * 
 * <b> MSAccess</b>
 * <pre>
 * Driver - sun.jdbc.odbc.JdbcOdbcDriver
 * JDBC Url - jdbc:odbc:dsn e.g. jdbc:odbc:myodbcdsn
 * </pre>
 * 
 * @author <a href="http://walmart.com" target=_blank>Amaresh Pattanaik
 *         (apattanaik@walmartlabs.com)</a>
 *
 */
public class DbUtil {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());
    
    private static DbType sDbType = DbType.ORACLE;
    private static String sJdbcDriver = null;
    private static String sJdbcUrl = null;
    private static String sJdbcOraDriver = "oracle.jdbc.driver.OracleDriver";
    private static String sJdbcMySqlDriver = "com.mysql.jdbc.Driver";
    private static String sJdbcSqlServerDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String sJdbcMsAccessDriver = "sun.jdbc.odbc.JdbcOdbcDriver";
    private static String sDbUser = null;
    private static String sDbPwd = null;
    private static Connection conn = null;
    private static PreparedStatement stmnt = null;
    private static ResultSet rs = null;

    private static void createConnection(DbType dbType, String jdbcUrl) {
        sDbType = dbType;
        sJdbcUrl = jdbcUrl;
        try {
            // also sets the database type, user and pwd.
            switch (sDbType) {
            case ORACLE:
                sJdbcDriver = sJdbcOraDriver;
                break;
            case MYSQL:
                sJdbcDriver = sJdbcMySqlDriver;
                break;
            case SQLSERVER:
                sJdbcDriver = sJdbcSqlServerDriver;
                break;
            case MSACCESS:
                sJdbcDriver = sJdbcMsAccessDriver;
                break;
            default:
                sJdbcDriver = sJdbcOraDriver;
                break;
            }
            Class.forName(sJdbcDriver);
            conn = DriverManager.getConnection(sJdbcUrl);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        conn = null;
        stmnt = null;
        rs = null;
    }

    public static enum DbType {
        ORACLE, MYSQL, SQLSERVER, MSACCESS
    }

    public static int getRowCount(DbType dbType, String jdbcUrl, String sqlQuery) {
        int rowCount = 0;
        try {
            createConnection(dbType, jdbcUrl);
            stmnt = conn.prepareStatement(sqlQuery);
            rs = stmnt.executeQuery();
            if(rs == null) {
                return rowCount;
            }
            while (rs.next()) {
                rowCount++;
            }
            closeConnection();

        } catch (Exception ex) {
            logger.error(ex);
        }

        return rowCount;
    }
    
    public static int getRowCount(String jdbcUrl, String sqlQuery) {
        return getRowCount(DbType.ORACLE, jdbcUrl, sqlQuery);
    }

    public static ArrayList<HashMap<String, String>> getQueryResults(String jdbcUrl, String sqlQuery) {
        return getQueryResults(DbType.ORACLE, jdbcUrl, sqlQuery);
    }
    
    public static ArrayList<HashMap<String, String>> getQueryResults(DbType dbType, String jdbcUrl, String sqlQuery) {
        logger.info("jdbc url: " + jdbcUrl);
        logger.info("query: " + sqlQuery);
        ArrayList<HashMap<String, String>> qResults = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> rowMap = null;
        String colLabel = null;
        String colVal = null;
        int numberOfColumns = 0;

        try {
            createConnection(dbType, jdbcUrl);
            stmnt = conn.prepareStatement(sqlQuery);
            rs = stmnt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
            while (rs.next()) {
                rowMap = new HashMap<String, String>();
                for (int i = 1; i <= numberOfColumns; i++) {
                    colLabel = rsmd.getColumnLabel(i);
                    colVal = getValue(rs, colLabel);
                    rowMap.put(colLabel, colVal);
                }
                qResults.add(rowMap);
            }
            stmnt.close();
            closeConnection();

        } catch (Exception ex) {
            logger.error(ex);
        }

        return qResults;
    }
    
    public static String clobToString(java.sql.Clob clobObject) {
        String clobAsString = null;
        try {
            InputStream in = clobObject.getAsciiStream();
            StringWriter w = new StringWriter();
            IOUtils.copy(in, w);
            clobAsString = w.toString();
        } catch(Exception ex) {
            logger.error(ex);
        }
        return clobAsString;
    }
    
    public static String getValue(ResultSet rs1, String columnLabel) {
        Object value = null;
        try {
            value = rs1.getObject(columnLabel);
            if(value == null) {
                return "null";
            }
            if(value instanceof java.lang.String) {
                return value.toString();
            }
            if(value instanceof java.sql.Clob) {
                return clobToString( (java.sql.Clob) value);
            }
            return value.toString();
        } catch(Exception ex) {
            logger.error(ex);
        }
        
        return "";
    }

    private static void setJdbcUrl(String jdbcUrl) {
        sJdbcUrl = jdbcUrl;
        logger.info("JDBC url: " + sJdbcUrl);
    }

}