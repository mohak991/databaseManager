package edu.uic.f17g201.utils;

public interface AppConstants {
	
	interface DBURL {
		String MYSQL = "jdbc:mysql://";
		String DB2 = "jdbc:db2://";
		String ORACLE = "jdbc:oracle:thin:@";
	}
	
	interface DBPorts {
		String MYSQL = "3306";
		String DB2 = "50000";
		String ORACLE = "1521";
	}
	
	interface DBDrivers {
		String MYSQL = "com.mysql.jdbc.Driver";
		String DB2 = "com.ibm.db2.jcc.DB2Driver";
		String ORACLE = "oracle.jdbc.driver.OracleDriver";
	}
}
