package com.user.expense;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


@WebServlet("/app/AddCategoryServlet")
public class AddCategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		try {
			BufferedReader reader = request.getReader();
			StringBuilder json = new StringBuilder();
			String line = "";
			while((line = reader.readLine()) != null) {
				json.append(line);
			}
			int userId = -1;
			for(int index = 0; index < cookies.length; index++) {
				Cookie currCookie = cookies[index];
				if(currCookie.getName().equals("userId")) {
					userId = Integer.parseInt(currCookie.getValue());
				}					
			}
			JSONObject categoryDetails = new JSONObject(json.toString());
			Category category = new Category(categoryDetails.getString("category_name"));
			ExpenseTracker expenseManager = ExpenseTracker.getInstance();
			JSONObject details = expenseManager.addCategory(userId, category);
			response.getWriter().write(details.toString());
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
