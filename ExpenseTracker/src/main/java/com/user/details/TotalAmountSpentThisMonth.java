package com.user.details;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mysql.connector.DBconnector;
import com.user.expense.User;


@WebServlet("/app/TotalAmountSpentThisMonth")
public class TotalAmountSpentThisMonth extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		Connection connector = DBconnector.getInstance().getConnection();
		PreparedStatement stmt = null;
		JSONObject details = new JSONObject();
		int userId = -1;
		try {
			for(int index = 0; index < cookies.length; index++) {
				Cookie currCookie = cookies[index];
				if(currCookie.getName().equals("userId")) {
					userId = Integer.parseInt(currCookie.getValue());
				}					
			}
			stmt = connector.prepareStatement("select userName from UserDetails where userId = ?");
			stmt.setInt(1, userId);
			ResultSet output = stmt.executeQuery();
			String Username = "";
			if(output.next()) {
				Username = output.getString(1);
			}
			User currentUser = new User(Username);
			Long amount = currentUser.totalSpentThisMonth();
			details.put("totalAmount", amount);
			response.getWriter().write(details.toString());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}


}
