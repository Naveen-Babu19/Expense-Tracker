package com.user.expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.connector.DBconnector;

public class SharedExpenseManager {
	private List<SharedExpense> sharedExpenses;

	public void addSharedExpense(SharedExpense sharedExpense){
		this.sharedExpenses.add(sharedExpense);
	}
	
	public void removeSharedExpense(SharedExpense sharedExpense) {
		this.sharedExpenses.remove(sharedExpense);
	}
	
	public List<SharedExpense> getSharedExpense(){
		return sharedExpenses;
	}
	
	private static SharedExpenseManager manager = null;
	
	private SharedExpenseManager() {
		
	}
	
	public static SharedExpenseManager getInstance() {
		if(manager == null) {
			manager = new SharedExpenseManager();
		}
		return manager;
	}
	
	public void setSharedExpense(JSONObject expenseDetails) throws Exception {
		PreparedStatement statement = null;
		Connection connection = DBconnector.getInstance().getConnection();
		try {
			int userId = expenseDetails.getInt("user_id");
			int amount = expenseDetails.getInt("amount");
			int expenseId = expenseDetails.getInt("expenseId");
			statement = connection.prepareStatement("insert into SharedExpense (amountSpent, userId, expenseId) values (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			statement.setInt(2, userId);
			statement.setInt(3, expenseId);
			statement.setInt(1, amount);
			int affectedLines = statement.executeUpdate();
			if(affectedLines != 1) {
				throw new SQLException("Error while inserting ");
			}
		}
		catch(SQLException se) {
			throw new Exception(se.getMessage());
		}
		catch(JSONException je) {
			throw new Exception(je.getMessage());
		}
	}
	
}