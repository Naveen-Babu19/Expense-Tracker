package com.mysql.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnector {
	private static DBconnector dbConnector = null;
	private Connection dbConnection = null;
	
	private DBconnector() {
		
	}
	
	public static DBconnector getInstance() {
		if(dbConnector == null) {
			dbConnector = new DBconnector();
		}
		return dbConnector;
	}
	
	public Connection getConnection() {
		if(dbConnection == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ExpenseTracker","NaveenBabu","naveen");
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
		return dbConnection;
	}
}
