package com.user.expense;

import java.time.LocalDate;

public class Receipt {
	private byte[] image;
	private String Description;
	private LocalDate date;
	
	public Receipt(byte[] image, String description, LocalDate date) {
		super();
		this.image = image;
		Description = description;
		this.date = date;
	}
	
	public byte[] getImage() {
		return image;
	}
	
	public String getDescription() {
		return Description;
	}

	public String getDate() {
		return date.toString();
	}
	
	public void setDescription(String Description) {
		this.Description = Description;
	}
}

