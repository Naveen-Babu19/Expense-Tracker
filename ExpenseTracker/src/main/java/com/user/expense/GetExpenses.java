package com.user.expense;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


@WebServlet("/app/GetExpenses")
public class GetExpenses extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int userId = -1;
		Cookie[] cookies = request.getCookies();
		System.out.println("Vandachu");
		try {
			for(int index = 0; index < cookies.length; index++) {
				Cookie currCookie = cookies[index];
				if(currCookie.getName().equals("userId")) {
					userId = Integer.parseInt(currCookie.getValue());
				}					
			}
			System.out.println("loop mudunchutu");
			ExpenseTracker expenseManager = ExpenseTracker.getInstance();
			JSONObject result = expenseManager.getExpenses(userId);

			System.out.println("Object");
			response.getWriter().write(result.toString());
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
