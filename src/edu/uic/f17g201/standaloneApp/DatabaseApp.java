package edu.uic.f17g201.standaloneApp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import edu.uic.f17g201.bean.LoginBean;
import edu.uic.f17g201.dao.DatabaseAccess;

public class DatabaseApp {
	private static Scanner sc;
	private static LoginBean bean;
	private static DatabaseAccess dbOperations;

	enum DBOperations {
		SELECT, CREATE, INSERT, DROP
	}
	public static void main(String[] args) {
		sc = new Scanner(System.in);
		System.out.print("Enter Username : ");
		String username = sc.next();
		System.out.print("Enter Password : ");
		String password = sc.next();
		System.out.print("Enter DBMSType : ");
		String dbmsType = sc.next();
		System.out.print("Input 131.193.209.54 for velebitserver1 \nEnter Host:");
		String host = sc.next();
		System.out.print("For Read: world | For create, update and delete:  f17x321 \nEnter Schema: ");
		String schema = sc.next();
		bean = new LoginBean(username, password, host, dbmsType, schema);
		
		dbOperations = new DatabaseAccess();
		String connectionOutcome = dbOperations.connect(bean);
		if (connectionOutcome.equalsIgnoreCase("success")) {
			System.out.println("Connection Successfull to : " + bean.getHost());
			selectOperation();
		} else {
			dbOperations.close();
			System.out.println("Error Occured While Connection : " + connectionOutcome);
		}
	}

	private static void selectOperation() {
		System.out.println("\nSelect one of the options to perfrom operations");
		System.out.println("Enter Options : \\n\" For "  + "\n" + "Select: 1"+ 
					"\n" + "Create: 2"+  "\n" + "Insert: 3"+ "\n" + "Drop: 4");
		int selectedOption = Integer.parseInt(sc.next());
		if(selectedOption > 4) {
			System.out.println("Invalid Input: ReEnter");
			selectOperation();
		} else {
			String name = DBOperations.values()[selectedOption - 1].name().toLowerCase();
			switch (name) {
			case "select":
				System.out.println("Processing select...");
				processSelect();
				break;
			case "create":
				System.out.println("Processing Create...");
				processCreate();
				break;
			case "insert":
				System.out.println("Processing Insert...");
				processInsert();
				break;
			case "drop":
				System.out.println("Processing Drop...");
				processDrop();
				break;
			default:
				break;
			}
		}
	}

	private static void processDrop() {
		System.out.println("You do not have permission to drop in world schema. \n Switching to f17x321...");
		bean.setSchema("f17x321");
		List<String> tableList = dbOperations.listTables(bean);
		for(String s: tableList) {
			System.out.println("Table " +  (tableList.indexOf(s) + 1) + ": " + s);
		}
		System.out.print("Choose anyone out of ");
		for(String s: tableList) {
			System.out.print(s + " ");
		}
		System.out.println("to drop ");
		String tableSelection = sc.next();
		if(tableList.contains(tableSelection)) {
			String query = "drop table " + bean.getSchema()+"."+ tableSelection;
			try {
			    dbOperations.processQuery(query);
			    System.out.println("Drop Successfull..");
			    System.out.println(query);
			} catch(Exception e) {
				System.out.println("Exception Occured: " + e.getMessage());
				dbOperations.close();
			}
			selectOperation();
		} else {
			System.out.println("Invalid TableName ");
			processDrop();
		}
	}

	private static void processInsert() {
		System.out.println("You do not have permission to insert in world schema. \n Switching to f17x321...");
		bean.setSchema("f17x321");
		List<String> tableList = dbOperations.listTables(bean);
		System.out.print("Table List in : " + bean.getSchema());
		for(String s: tableList) {
			System.out.println("Table " +  (tableList.indexOf(s) + 1) + ": " + s);
		}
		System.out.print("Choose anyone out of ");
		for(String s: tableList) {
			System.out.print(s + " ");
		}
		System.out.print("tables \n");
		String tableSelection = sc.next().toLowerCase();
		if(tableList.contains(tableSelection)) {
			StringBuilder str = new StringBuilder();
			str.append("insert into " + bean.getSchema() + "." + tableSelection + " VALUES (");
			List<String> columnNames = dbOperations.getColumnNames("select * from " + bean.getSchema() +"." + tableSelection + " ;");
			for(String s: columnNames) {
				System.out.println("Insert : " + s + " from " + tableSelection);
				String value = sc.next();
				try {
				    Integer.parseInt(value);
				    if(columnNames.size() == columnNames.indexOf(s) + 1) {
				    	str.append(Integer.parseInt(value));
				    } else {
				    	str.append(Integer.parseInt(value) +", ");
				    }
				} catch (Exception e) {
					if(columnNames.size() == columnNames.indexOf(s) + 1) {
						str.append("\""+ String.valueOf(value) + "\"");
				    } else {
				    	str.append("\""+String.valueOf(value) +"\""+", ");
				    }
				}
			}
			str.append(");");
			System.out.println("My Query: \n" + str);
			try {
			    dbOperations.processQuery(str.toString());
			    System.out.println("Insert Successfull--");
			    System.out.println(str);
			} catch (Exception e) {
				System.out.println("Exception Occured: " + e.getMessage());
				dbOperations.close();
			}
			selectOperation();
		} else {
			System.out.println("incorrect table selected...");
			processInsert();
		}
		
	}

	private static void processCreate() {
		System.out.println("You do not have permission to create in world schema. \n Switching to f17x321...");
		System.out.println("Creating table employee, to test create");
		bean.setSchema("f17x321");
		String query = "create table IF NOT EXISTS f17x321.f17g201_672119300_employee (emp_id int, firstname varchar(30), lastname varchar(30), address varchar(30))";
		dbOperations.processQuery(query);
		System.out.println("Created Table: f17g201_672119300_employee");
		System.out.println(query);
		selectOperation();
	}

	private static void processSelect() {
		bean.setSchema("world");
		System.out.println("Logged into : " + bean.getSchema());
		//Showing Table List and number of records in each table.
		System.out.println("Do you want to switch schema ?");
		System.out.println("World and f17x321 available");
		System.out.println("Type Yes if you want to change to f17x321");
		String decision = sc.next();
		if(decision.equalsIgnoreCase("yes")) {
			bean.setSchema("f17x321");
		}
		List<String> tableList = dbOperations.listTables(bean);
		System.out.println("Tables in " +  bean.getSchema() + " are ");
		for(String s: tableList) {
			System.out.println("Table " +  (tableList.indexOf(s) + 1) + ": " + s);
		}
		System.out.print("Choose anyone out of ");
		for(String s: tableList) {
			System.out.print(s + " ");
		}
		System.out.print("tables \n");
		String tableSelection = sc.next().toLowerCase();
		if (tableList.contains(tableSelection)) {
			String query =  "select * from " + bean.getSchema() +"." + tableSelection + " ;";
			dbOperations.processQuery(query);
			System.out.println("Total Columns : " + dbOperations.getNumOfCols());
			System.out.println("Total Rows : " + dbOperations.getNumOfRows());
			System.out.print("Column Names in table : " + tableSelection +" are \n");
			if (dbOperations.getSelectedColumnNames() != null && !dbOperations.getSelectedColumnNames().isEmpty()) {
				for (String columnName : dbOperations.getSelectedColumnNames()) {
					System.out.print(columnName + " " + "\n");
				}
				System.out.println();
				ResultSet rs = dbOperations.getResultSet();
				if(rs != null) {
					System.out.println("Records :: ");
					try {
						for (int i = 0; i < dbOperations.getNumOfRows(); i++) {
							rs.next();
							for(int j = 0; j < dbOperations.getNumOfCols(); j++) {
								System.out.print(rs.getString(dbOperations.getSelectedColumnNames().get(j)) + "|");
							}
							System.out.println();
						}
						selectOperation();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("Invalid Table Name-- Reselect");
			processSelect();
		}
	}
}