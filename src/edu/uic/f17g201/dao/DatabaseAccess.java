package edu.uic.f17g201.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.uic.f17g201.bean.LoginBean;
import edu.uic.f17g201.utils.AppConstants;

public class DatabaseAccess {
	private Connection connection;
	private Statement statement;
	private DatabaseMetaData databaseMetaData;
	private ResultSet resultSet;
	private int numOfCols;
	private int numOfRows;
	private ResultSetMetaData resultSetMetaData;
	private List<String> selectedColumns;
	private List<String> selectedColumnNames;
	private List<String> tableList;
	private static final String[] TABLE_TYPES = {"TABLE", "VIEW"};
	
	public DatabaseAccess() {//Default constructor
	}
	
	public String connect(LoginBean bean) {
		String error = "";
		String dbms = bean.getDmbsType().toUpperCase();
		String jdbcDriver= "";
		String dbConnectionURL = "";
		switch (dbms) {
		case "MYSQL":
			bean.setPort(AppConstants.DBPorts.MYSQL);
			jdbcDriver = AppConstants.DBDrivers.MYSQL;
			dbConnectionURL = AppConstants.DBURL.MYSQL + bean.getHost() + ":" + bean.getPort() + "/" + bean.getSchema() + "?&useSSL=false";
			break;
		case "DB2":
			bean.setPort(AppConstants.DBPorts.DB2);
			jdbcDriver = AppConstants.DBDrivers.DB2;
			dbConnectionURL = AppConstants.DBURL.DB2 + bean.getHost() + ":" + bean.getPort()+ "/" + bean.getSchema();
			break;
		case "ORACLE":
			bean.setPort(AppConstants.DBPorts.ORACLE);
			jdbcDriver = AppConstants.DBDrivers.ORACLE;
			dbConnectionURL = AppConstants.DBURL.ORACLE + bean.getHost() + ":" + bean.getPort()+ "/" + bean.getSchema();
			break;
		}
		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(dbConnectionURL, bean.getUsername(), bean.getPassword());
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			databaseMetaData = connection.getMetaData();
			return "success";
		} catch (ClassNotFoundException ce) {
			error = "Database: " + bean.getDmbsType() + " not supported.";
			return error;

		} catch (SQLException se) {
			if (se.getSQLState().equals("28000")) {
				error = "Either username or password does not match";
			} else if (se.getSQLState().equals("42000")) {
				error = "Schema does not exists";
			} else {
				error = "SQL Exception occurred!\n" + "Error Code: " + se.getErrorCode() + "\n" + "SQL State: "
						+ se.getSQLState() + "\n" + "Message :" + se.getMessage() + "\n\n";
			}
			return error;
		} catch (Exception e) {
			error = "Exception occurred: " + e.getMessage();
			close();
			return error;
		}
	}
	
	public List<String> listTables(LoginBean bean) {
		String tableName = "TABLE_NAME";
		try {
			resultSet = databaseMetaData.getTables(bean.getSchema(), bean.getSchema(), "%", TABLE_TYPES);
			if (resultSet != null) {
				resultSet.last();
				numOfRows = resultSet.getRow();
				tableList = new ArrayList<String>(numOfRows);
				resultSet.beforeFirst();
				String eachTableName="";
				while(resultSet.next()) {
					eachTableName = resultSet.getString(tableName);
					if(!bean.getDmbsType().equalsIgnoreCase("ORACLE")|| eachTableName.length() < 4)
						tableList.add(eachTableName);
					else if(!eachTableName.substring(0,4).equalsIgnoreCase("BIN$"))
						tableList.add(eachTableName);
				}
			}
			return tableList;
		}
		catch (SQLException e) {
			return tableList;
		}
	}
	
	public void processQuery(String query){
		String queryType = query.split(" ")[0];
		if (queryType.equalsIgnoreCase("select")) {
			try {
				resultSet = statement.executeQuery(query);
				if(resultSet != null){
					resultSetMetaData = resultSet.getMetaData();
					numOfCols = resultSetMetaData.getColumnCount();
					resultSet.last();
					numOfRows = resultSet.getRow();
					resultSet.beforeFirst();
					selectedColumnNames = new ArrayList<String>(numOfCols);
					for (int i = 0; i < numOfCols; i++) {
						selectedColumnNames.add(resultSetMetaData.getColumnName(i + 1));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.statement.executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<String> getColumnNames(String query) {
		List<String> columnNames = null;
		try {
			resultSet = statement.executeQuery(query);
			if(resultSet != null){
				resultSetMetaData = resultSet.getMetaData();
				numOfCols = resultSetMetaData.getColumnCount();
				resultSet.last();
				numOfRows = resultSet.getRow();
				resultSet.beforeFirst();
				columnNames = new ArrayList<String>(numOfCols);
				for (int i = 0; i < numOfCols; i++) {
					columnNames.add(resultSetMetaData.getColumnName(i + 1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnNames;
	}
	public void close() {
		try {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException excp) {
			System.out.println("SQL Exception" + "Error Code: " + excp.getErrorCode() + "\n" + "SQL State: "
					+ excp.getSQLState() + "\n" + " SQL Message" + excp.getMessage() + "\n");
		}
	}

	public DatabaseMetaData getDatabaseMetaData() {
		return databaseMetaData;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public int getNumOfCols() {
		return numOfCols;
	}

	public int getNumOfRows() {
		return numOfRows;
	}

	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}
	
	public List<String> getSelectedColumnNames() {
		return selectedColumnNames;
	}

	public List<String> getSelectedColumns() {
		return selectedColumns;
	}

	public List<String> getTableList() {
		return tableList;
	}
}