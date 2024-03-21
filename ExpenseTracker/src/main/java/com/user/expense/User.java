package com.user.expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.mysql.connector.DBconnector;

public class User {
	private String username;
	private String email;
	private String password;
	private double salary;
	private List<IndividualExpense> individualExpense;
	private ReceiptManager receiptManager;
	private SharedExpenseManager sharedExpenseManger;
    private Map<Category, Double> categoryAndBudgets;
	private Statistics stats;
	
	public User(String username) {
		this.username = username;
	}
	
	public String getUserName() {
		return username;
	}		
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void removeExpense(IndividualExpense individualExpense) {
		this.individualExpense.remove(individualExpense);
	}
	
	public Statistics getStats() {
		return stats;
	}
	
	public long totalSpentThisMonth() throws Exception{
		long totalAmount = 0;
		Connection connector = DBconnector.getInstance().getConnection();
		try {
			int day = 1;
			LocalDate date = LocalDate.of(LocalDate.now().getYear(),LocalDate.now().getMonthValue(),day);
			long epochMilliseconds = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
			PreparedStatement stmt = connector.prepareStatement("select onDate, amountSpent from Expense where userId = (select userId from UserDetails where userName like ?)");
			stmt.setString(1,username);
			ResultSet output = stmt.executeQuery();
			while(output.next()) {
				if(Long.parseLong(output.getString(1)) > epochMilliseconds) {
					totalAmount += output.getInt(2);
				}
			}
		} catch (SQLException e) {
			throw new Exception("Something went wrong in getting total money spent this month");
		} 
		return totalAmount;
	}
	
	public static synchronized JSONObject updateProfile(JSONObject userDetails, int userId) throws Exception {
		JSONObject details = null;
		PreparedStatement stmt = null;
		Connection conn = DBconnector.getInstance().getConnection();
		try {
			stmt = conn.prepareStatement("Update UserDetails set userName = ?, income = ? where userId = ?");
			stmt.setString(1, userDetails.getString("username"));
			stmt.setInt(2, userDetails.getInt("salary"));
			stmt.setInt(3, userId);
			int changedRows = stmt.executeUpdate();
			if(changedRows == 1) {
				details = new JSONObject("{\"status\":\"success\"}");
			}
			else {
				throw new SQLException();
			}
		}catch(SQLException se) {
			se.printStackTrace();
			throw new Exception("Something went wrong");
		}
		return details;
	}
	
	public static int login(String email, String password) throws Exception{
		Connection conn = null;
		try {
			conn = DBconnector.getInstance().getConnection();	
			PreparedStatement stmt = conn.prepareStatement("select userId, emailAddress, password from UserDetails where emailAddress = ?");
			stmt.setString(1, email);
			System.out.println(email);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				if(BCrypt.checkpw(password, rs.getString(3))) {
					return rs.getInt(1);
				}
				else {
					throw new SQLException("password problem");
				}
			}
			else {
				throw new SQLException("email problem");
			}
		}
		catch(SQLException se) {
			throw new Exception(se.getMessage());
		}
	}
}
