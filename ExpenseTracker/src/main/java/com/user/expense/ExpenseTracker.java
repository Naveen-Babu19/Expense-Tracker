package com.user.expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Statement;
import com.mysql.connector.DBconnector;

public class ExpenseTracker {
	private List<IndividualExpense> individualExpense;
	private ReceiptManager receiptManager;
	private SharedExpenseManager sharedExpenseManager;
    private Map<Category, Double> categoryAndBudgets;
	private Statistics stats;
	private static ExpenseTracker expenseManager;
	
	private ExpenseTracker() {
		sharedExpenseManager = SharedExpenseManager.getInstance();
		receiptManager = new ReceiptManager();
	}
	
	public static synchronized ExpenseTracker getInstance() {
		if(expenseManager == null) {
			expenseManager = new ExpenseTracker();
		}
		return expenseManager;
	}
	
	public ReceiptManager getReceiptManager() {
		return receiptManager;
	}
	
	public SharedExpenseManager getSharedExpenseManager() {
		return sharedExpenseManager;
	}

	public Statistics getStats() {
		return stats;
	}
	
	public JSONObject getExpenses(int userId) throws Exception{
		System.out.println(userId);
		StringBuilder outer = new StringBuilder();
		Connection conn = DBconnector.getInstance().getConnection();;
		PreparedStatement stmt = null;
		JSONObject Result = null;
		try {
			stmt = conn.prepareStatement("select expenseId, categoryName, description, amountSpent, onDate from Expense as e join Categories as c on c.categoryId = e.categoryId where userId = ?");
			stmt.setInt(1, userId); 
			ResultSet output = stmt.executeQuery();
			int index = 1;
			outer.append("{");
			while(output.next()) {
				String date = output.getString(5);
		        long epochMilliseconds = Long.parseLong(date);
		        Instant instant = Instant.ofEpochMilli(epochMilliseconds);
		        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
	
				StringBuilder inner = new StringBuilder();
				inner.append("{ Expenseid:"+output.getInt(1)+", Category: "+output.getString(2)+", Description: "+output.getString(3)+", Amount: "+output.getInt(4)+", Date: "+localDate.toString()+"}");
				System.out.println(inner.toString());
				outer.append(index+": "+inner.toString()+", ");
				index+=1;
				
			}
			if(outer.length() > 1) {
				outer.replace(outer.length() - 2, outer.length(), "}");
			}
			else {
				outer.append("expense: 0 }");
			}
			System.out.println(outer);
			Result = new JSONObject(new JSONObject(outer.toString()));
		}catch(SQLException | JSONException e) {
			throw new Exception(e.getMessage());
		}
		return Result;
	}
	
	public JSONObject addCategory(int userId, Category categoryDetails) throws Exception {
		JSONObject details = new JSONObject();
		Connection connector = DBconnector.getInstance().getConnection();
		PreparedStatement stmt = null;
		try {
			String categoryName = categoryDetails.getCategoryName();
			int categoryBudget = categoryDetails.getBudget();
			stmt = connector.prepareStatement("Select * from Categories where categoryName like ? ;");
			stmt.setString(1, categoryName);
			ResultSet output = stmt.executeQuery();
			int categoryId = -1;
			if(output.next()) {
				categoryId = output.getInt(1);
			}
			else {
				stmt = connector.prepareStatement("insert into Categories (categoryName) values (?) ;");
				stmt.setString(1, categoryName);
				stmt.executeUpdate();
				stmt = connector.prepareStatement("select * from Categories where categoryName like ? ;");
				stmt.setString(1, categoryName);
				output = stmt.executeQuery();
				output.next();
				categoryId = output.getInt(1);
			}
			stmt = connector.prepareStatement("select userId, categoryId from PersonalCategories where categoryId = ? and userId = ?");
			stmt.setInt(1,categoryId);
			stmt.setInt(2, userId);
			output = stmt.executeQuery();
			if(output.next()) {
				details.put("status", "Already Exist");
			}
			else {
				stmt = connector.prepareStatement("insert into PersonalCategories (userId, categoryId, budget) values (?, ?, ?)");
				stmt.setInt(1, userId);
				stmt.setInt(2, categoryId);
				stmt.setInt(3, categoryBudget);
				stmt.executeUpdate();
				details.put("status", "done");
			}
		}catch(SQLException se) {
			throw new Exception(se.getMessage());
		}catch(JSONException je) {
			throw new JSONException(je.getMessage());
		}
		return details;
	}
	
	public JSONObject addExpense(JSONObject expenseDetails, int userId) throws Exception {
		PreparedStatement statement = null;
		ResultSet output = null;
		Connection connection = DBconnector.getInstance().getConnection();
		JSONObject details = null;
		try {
			Category category = new Category(expenseDetails.getString("category"));
			String description = expenseDetails.getString("description");
			int amount = expenseDetails.getInt("amount");
			String expenseName = expenseDetails.getString("expenseName");
			IndividualExpense individualExpense = new IndividualExpense(description, amount, category,LocalDate.ofEpochDay( System.currentTimeMillis() / (24 * 60 * 60 * 1000)), expenseName);
			int categoryId = -1;
			statement = connection.prepareStatement("select categoryId from Categories where categoryName like ?;");
			statement.setString(1, individualExpense.getCategory().getCategoryName());
			output = statement.executeQuery();
			if(output.next()) {
				categoryId = output.getInt(1);
			}
			else{
				throw new Exception("No such category found");
			}
			statement = connection.prepareStatement("insert into Expense (userId, categoryId, onDate, amountSpent, description, expenseName) values (?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, userId);
			statement.setInt(2, categoryId);
			statement.setString(3, System.currentTimeMillis()+"");
			statement.setInt(4, (int)individualExpense.getAmount());
			statement.setString(5, individualExpense.getDescription());
			statement.setString(6, individualExpense.getExpenseName());
			int affectedLines = statement.executeUpdate();
			if(affectedLines != 1) {
				throw new SQLException("");
			}
			int expenseId = 0;
			ResultSet generatedKeys = statement.getGeneratedKeys();
			if(generatedKeys.next()) {
				expenseId = generatedKeys.getInt(1);
			}
			details = new JSONObject("{ expense_id:"+expenseId+" }");
		}
		catch(SQLException se) {
			throw new Exception(se.getMessage());
		}
		catch(JSONException je) {
			throw new Exception(je.getMessage());
		}
		return details;
	}
	
	public JSONObject createTagToUser(Tag tag, int userId) throws Exception{
		Connection connection = DBconnector.getInstance().getConnection();
		PreparedStatement statement = null;
		JSONObject details = null;
		try {
			statement = connection.prepareStatement("insert into Tag (tagName) values (?)");
			statement.setString(1,tag.getTag());
			int affectedLines = statement.executeUpdate();
			if(affectedLines != 1) {
				
				throw new SQLException();
			}
			statement = connection.prepareStatement("select tagId from Tag where tagName = ?");
			statement.setString(1,tag.getTag());
			int tagId = -1;
			ResultSet output = statement.executeQuery();
			if(output.next()) {
				tagId = output.getInt(1);
			}
			statement = connection.prepareStatement("select * from PersonalTags where userId = ? and tagId = ?");
			statement.setInt(1, tagId);
			statement.setInt(2, userId);
			output = statement.executeQuery();
			if(!output.next()) {
				statement = connection.prepareStatement("insert into PersonalTags (tagId, userId) values (?,?)");
				statement.setInt(1, tagId);
				statement.setInt(2, userId);
				affectedLines = statement.executeUpdate();
				if(affectedLines == 1) {
					details = new JSONObject("{ status: success }");
				}
				else {
					throw new SQLException();
				}
			}else {
				throw new SQLException();
			}
		}catch (SQLException se){
			se.printStackTrace();
			throw new Exception("Something went wrong");
		}
		return details;
	}
	
	public JSONObject editBudget(JSONObject budgetDetails, int userId) throws Exception{
		JSONObject details = null;
		PreparedStatement stmt = null;
		Connection connector = DBconnector.getInstance().getConnection();		
		try {
			int categoryId = -1;
			stmt = connector.prepareStatement("select categoryId from Categories where categoryName = ?");
			stmt.setString(1, budgetDetails.getString("categoryname"));
			ResultSet output = stmt.executeQuery();
			if(output.next()) {
				categoryId = output.getInt(1);
			}
			stmt = connector.prepareStatement("Update PersonalCategories set budget = ? where userId = ? and categoryId = ?");
			stmt.setInt(1, Integer.parseInt(budgetDetails.getString("budget")));
			stmt.setInt(2, userId);
			stmt.setInt(3, categoryId);
			int affectedLines = stmt.executeUpdate();
			if(affectedLines != 1) {
				throw new SQLException("Something went wrong");
			}
			details = new JSONObject("{\"status\":\"success\"}");
			
		}catch(SQLException se) {
			throw new Exception(se.getMessage()+"\n"+se.getStackTrace().toString());
		}
		return details;
	}
	
	public JSONObject addTagToExpense(JSONObject tagDetails, int userId) throws Exception{
		JSONObject details = null;
		String tagName = tagDetails.getString("tagname");
		Tag tag = new Tag(tagName);
		int expenseId = tagDetails.getInt("ExpenseId");
		Connection connection = DBconnector.getInstance().getConnection();
		try {
			PreparedStatement statement = connection.prepareStatement("Select tagId from Tag where tagName like ?");
			statement.setString(1, tag.getTag());
			ResultSet output = statement.executeQuery();
			int tagId = -1;
			if(output.next()) {
				tagId = output.getInt(1);
			}
			else {
				throw new JSONException("Tag not found");
			}
			statement = connection.prepareStatement("select * from TagsForExpense where tagId = ? and expenseId = ?");
			statement.setInt(1, tagId);
			statement.setInt(2, expenseId);
			output = statement.executeQuery();
			if(output.next()) {
				throw new JSONException("Already available");
			}
			statement = connection.prepareStatement("insert into TagsForExpense (tagId, expenseId,userId) values (?,?,?)");
			statement.setInt(1, tagId);
			statement.setInt(2, expenseId);
			statement.setInt(3, userId);
			int affectedLines = statement.executeUpdate();
			if(affectedLines != 1) {
				throw new SQLException("");
			}
			details = new JSONObject("{\"status\": \"success\"}");
		}catch(SQLException se) {
			throw new Exception("Something went wrong");
		}
		catch(JSONException je) {
			throw new Exception(je.getMessage());
		}
		return details;
	}
	
	public JSONObject removeCategory(JSONObject categoryDetails, int userId) throws Exception{
		Connection connector = DBconnector.getInstance().getConnection();
		JSONObject details = null;
		try {
			PreparedStatement statement = connector.prepareStatement("Select * from Categories where categoryName like ?");
			statement.setString(1, categoryDetails.getString("category"));
			ResultSet output = statement.executeQuery();
			int categoryId = -1;
			if(output.next()) {
				categoryId = output.getInt(1);	
			}else {
				throw new Exception("Category is not found");
			}
			statement = connector.prepareStatement("Delete from PersonalCategories where userId = ? and categoryId = ?");
			statement.setInt(1, userId);
			statement.setInt(2, categoryId);
			if(!statement.execute()) {
				throw new Exception("No such relation found");
			}
			details = new JSONObject("{status: success}");
			
		}catch(SQLException e) {
			throw new Exception("Something went wrong ");
		}
		return details;
	}
	
	public JSONObject updateExpense(JSONObject expenseDetails, int userId) throws Exception{
		JSONObject details = null;
		Connection connector = DBconnector.getInstance().getConnection();
		try {
			PreparedStatement stmt = connector.prepareStatement("Update Expense set description = ? and expenseName = ? where expenseId = ? and userId = ?");
			stmt.setString(1, expenseDetails.getString("description"));
			stmt.setString(2, expenseDetails.getString("expensename"));
			stmt.setInt(3, expenseDetails.getInt("expenseId"));
			stmt.setInt(4, userId);
			int affectedLine = stmt.executeUpdate();
			if(affectedLine != 1) {
				throw new SQLException("");
			}
			details = new JSONObject("{\"status\":\"success\"}");
		}catch(SQLException se) {
			throw new Exception("Something went wrong");
		}
		return details;
	}
	
	public JSONObject removeExpense(JSONObject expenseDetails, int userId) throws Exception{
		JSONObject details = null;
		Connection connector = DBconnector.getInstance().getConnection();
		try {
			PreparedStatement stmt = connector.prepareStatement("Delete from Expense where expenseId = ? and userId = ?");
			stmt.setInt(1, expenseDetails.getInt("expenseId"));
			stmt.setInt(2, userId);
			int affectedLine = stmt.executeUpdate();
			if(affectedLine != 1) {
				throw new SQLException("");
			}
			details = new JSONObject("{\"status\":\"success\"}");
		}catch(SQLException se) {
			throw new Exception("Something went wrong");
		}
		return details;
	}
	
	public JSONObject removeTagFromExpense(JSONObject tagDetails, int userId) throws Exception{
		JSONObject details = null;
		Connection connector = DBconnector.getInstance().getConnection();
		try {
			PreparedStatement stmt = connector.prepareStatement("Delete from TagsForExpense where expenseId = ? and userId = ? and tagId = ?");
			stmt.setInt(1, tagDetails.getInt("expenseId"));
			stmt.setInt(2, userId);
			stmt.setInt(3, tagDetails.getInt("tagId"));
			int affectedLine = stmt.executeUpdate();
			if(affectedLine != 1) {
				throw new SQLException("");
			}
			details = new JSONObject("{\"status\":\"success\"}");
		}catch(SQLException se) {
			throw new Exception("Something went wrong");
		}
		return details;
	}
	
	public JSONObject removeTagFromUser(JSONObject tagDetails, int userId) throws Exception{
		JSONObject details = null;
		Connection connector = DBconnector.getInstance().getConnection();
		try {
			PreparedStatement stmt = connector.prepareStatement("Delete from PersonalTags where userId = ? and tagId = ?");
			stmt.setInt(1, userId);
			stmt.setInt(2, tagDetails.getInt("tagId"));
			int affectedLine = stmt.executeUpdate();
			if(affectedLine != 1) {
				throw new SQLException("");
			}
			details = new JSONObject("{\"status\":\"success\"}");
		}catch(SQLException se) {
			throw new Exception("Something went wrong");
		}
		return details;
	}
}
