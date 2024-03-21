package com.user.expense;

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

/**
 * Servlet implementation class UserCategory
 */
@WebServlet("/app/UserCategory")
public class UserCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		int userId = -1;
		Connection connector = DBconnector.getInstance().getConnection();
		PreparedStatement stmt = null;
		try {
			for(int index = 0; index < cookies.length; index++) {
				Cookie currCookie = cookies[index];
				if(currCookie.getName().equals("userId")) {
					userId = Integer.parseInt(currCookie.getValue());
				}					
			}
			stmt = connector.prepareStatement("select categoryName from PersonalCategories as pc join Categories as c on  c.categoryId = pc.categoryId where userId = ?;");
			stmt.setInt(1, userId);
			ResultSet output = stmt.executeQuery();
			int index = 1;
			StringBuilder jsonText = new StringBuilder();
			jsonText.append("{");
			while(output.next()) {
				jsonText.append(index +":"+ output.getString(1)+",");
				index+=1;
			}
			jsonText.replace(jsonText.length() - 1, jsonText.length(), "}");
			JSONObject details = new JSONObject(jsonText.toString());
			response.getWriter().write(details.toString());
			
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
