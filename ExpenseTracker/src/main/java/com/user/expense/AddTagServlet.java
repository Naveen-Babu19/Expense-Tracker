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


@WebServlet("/app/AddTagServlet")
public class AddTagServlet extends HttpServlet {
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
			ExpenseTracker expenseManager = ExpenseTracker.getInstance();
			JSONObject details = expenseManager.addTagToExpense(categoryDetails, userId);
			response.getWriter().write(details.toString());
		}catch(Exception e) {
			JSONObject error;
			try {
				error = new JSONObject("{status:"+ e.getMessage()+", class"+ e.getStackTrace()+"}");
				response.getWriter().write(error.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

}
