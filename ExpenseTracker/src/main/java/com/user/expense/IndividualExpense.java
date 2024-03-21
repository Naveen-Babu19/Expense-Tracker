package com.user.expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.mysql.connector.DBconnector;

public class IndividualExpense implements Expense{
	private int expenseId;
	private String expenseName;
	private String Description;
	private double amount;
	private Category category;
	private LocalDate date;
	private List<Tag> tags;
	
	public IndividualExpense(String description, double amount, Category category, LocalDate date, String expenseName) {
		Description = description;
		this.amount = amount;
		this.category = category;
		this.date = date;
		this.expenseName = expenseName;
	}
	
	public IndividualExpense(String description, double amount, Category category, LocalDate date,int expenseId) {
		Description = description;
		this.amount = amount;
		this.category = category;
		this.date = date;
		this.expenseId = expenseId;
	}
	
	public String getDetails() {
		return ""+expenseId+". "+category.getCategoryName()+" - "+amount+" \nDescription:"+Description+" \nDate:"+date.toString();
	}
	
	public int getExpenseId() {
		return expenseId;
	}

	public String getDescription() {
		return Description;
	}

	public double getAmount() {
		return amount;
	}
	
	public String getExpenseName() {
		return expenseName;
	}

	public void setExpenseName(String expenseName) {
		this.expenseName = expenseName;
	}

	public Category getCategory() {
		return category;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<Tag> getTags() {
		return tags;
	}

	
	
	public void removeTag(Tag tag) {
		this.tags.remove(tag);
	}
	
	public String getTagDetails() {
		String tags = "";
		for(int index = 0; index < this.tags.size(); index++) {
			if(index == (this.tags.size() - 1))
				tags += this.tags.get(index).getTag();
			else
				tags += this.tags.get(index).getTag() + ",";
		}
		return tags;
	}
	
	public void updateDescription(String Description) {
		this.Description = Description;
	}
}