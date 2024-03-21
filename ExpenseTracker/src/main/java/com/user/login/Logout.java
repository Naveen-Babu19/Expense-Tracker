package com.user.login;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.connector.DBconnector;


@WebServlet("/app/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection dbConnector = DBconnector.getInstance().getConnection();
		Cookie[] existingCookies = request.getCookies();
		int userId = -1;
		String sessionId = "";
		for(int index = 0; index < existingCookies.length; index++) {
			Cookie currCookie = existingCookies[index];
			if(currCookie.getName().equals("userId")) {
				userId = Integer.parseInt(currCookie.getValue());
			}
			if(currCookie.getName().equals("sessionId")) {
				sessionId = currCookie.getValue();
			}
		}
		try {
			PreparedStatement stmt = dbConnector.prepareStatement("select * from Session where userId = ? and sessionId like ?");
			stmt.setInt(1, userId);
			stmt.setString(2, sessionId);
			ResultSet output = stmt.executeQuery();
			if(output.next()) {
				stmt = dbConnector.prepareStatement("delete from Session where userId = ? and sessionId like ? ");
				stmt.setInt(1, userId);
				stmt.setString(2, sessionId);
				int deletedLines = stmt.executeUpdate();
				if(deletedLines != 1) {
					throw new Exception("error in deleting session");
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
