package com.user.expense;

public class Category {
	private String category;
	private int budget;
	
	public Category(String category){
		this.category = category;
	}

	public String getCategoryName() {
		return category;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public void setCategoryName(String category) {
		this.category = category;
	}
	
}
