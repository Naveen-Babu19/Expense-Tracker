package com.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import com.mysql.connector.DBconnector;

public class Util {
	
	private static Util manager = null;
	
	private Util() {
		
	}
	
	public static Util getInstance() {
		if(manager == null) {
			manager = new Util();
		}
		return manager;
	}
	
	public JSONObject getUsers(int userId) throws Exception {
		JSONObject userDetails = new JSONObject();
		Connection connector = DBconnector.getInstance().getConnection();
		PreparedStatement query = null;
		try {
			query = connector.prepareStatement(SQLQueries.GET_TOTAL_USERS);
			query.setInt(1, userId);
			ResultSet output = query.executeQuery();
			while(output.next()) {
				userDetails.put(output.getInt(1)+"", output.getString(2));
			}
		}catch(SQLException se) {
			throw new Exception("Something went wrong while getting user names");
		}
		return userDetails;
	}
	
}
