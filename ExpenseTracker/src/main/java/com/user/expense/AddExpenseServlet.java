package com.user.expense;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/app/AddExpenseServlet")
public class AddExpenseServlet extends HttpServlet {
   
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject error = null;
		int userId = -1;
		Cookie[] cookies = request.getCookies();
		try {
			for(int index = 0; index < cookies.length; index++) {
				Cookie currCookie = cookies[index];
				if(currCookie.getName().equals("userId")) {
					userId = Integer.parseInt(currCookie.getValue());
				}					
			}
			BufferedReader reader = request.getReader();
			StringBuilder json = new StringBuilder();
			String line = "";
			while((line = reader.readLine()) != null) {
				json.append(line);
			}
			String jsonGot = json.toString();
			JSONObject expenseDetails = new JSONObject(jsonGot);
			ExpenseTracker expenseManager = ExpenseTracker.getInstance();
			error = expenseManager.addExpense(expenseDetails, userId);
			
		} catch (Exception e) {
			try {
				error.put("ERROR", e.getMessage());
			} catch (JSONException e1) {
				System.out.println(e.getMessage());
			}
		} finally {
			response.getWriter().write(error.toString());
		}
	}

}
