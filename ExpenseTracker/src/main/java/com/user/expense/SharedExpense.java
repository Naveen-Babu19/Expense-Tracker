package com.user.expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SharedExpense implements Expense {
	private int sharedExpenseId;
	private String Description;
	private double amount;
	private Category category;
	private LocalDate date;
	private List<Tag> tag;
	private Map<User, Double> participantsAndTheirContribution;

	public SharedExpense(String description, double amount, Category category, LocalDate date, List<Tag> tag, Map<User, Double> participantsContribution) {
		this.Description = description;
		this.amount = amount;
		this.category = category;
		this.date = date;
		this.tag = tag;
		this.participantsAndTheirContribution = participantsContribution;
	}
	
	public void addParticipant (User user, double contribution) {
		this.participantsAndTheirContribution.put(user,contribution);
	}
	
	public void removeParticipant (User user) {
		this.participantsAndTheirContribution.remove(user);
	}
	
	public Map<User, Double> getParticipants(){
		return this.participantsAndTheirContribution;
	}

	public void addTag(Tag tag) {
		this.tag.add(tag);
	}
	
	public void removeTag(Tag tag) {
		this.tag.remove(tag);
	}
	
	public String getTagDetails() {
		String tags = "";
		for(int index = 0; index < this.tag.size(); index++) {
			if(index == (this.tag.size() - 1))
				tags += this.tag.get(index).getTag();
			else
				tags += this.tag.get(index).getTag() + ",";
				
		}
		return tags;
	}
	
	public void updateDescription(String Description) {
		this.Description = Description;
	}

	public String getDetails() {
		return ""+sharedExpenseId+". "+category.getCategoryName()+" - "+amount+" \nDescription:"+Description+" \nDate:"+date.toString();
	}
	
}
