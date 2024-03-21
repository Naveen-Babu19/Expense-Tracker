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


@WebServlet("/app/CreateTagServlet")
public class CreateTagServlet extends HttpServlet {
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
			Tag tag = new Tag(categoryDetails.getString("tagname"));
			JSONObject details = expenseManager.createTagToUser(tag, userId);
			response.getWriter().write(details.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
