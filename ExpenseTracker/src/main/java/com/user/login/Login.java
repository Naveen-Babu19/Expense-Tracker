package com.user.login;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.connector.DBconnector;
import com.user.expense.User;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		try {
			conn = DBconnector.getInstance().getConnection();		
			String sessionId = UUID.randomUUID().toString();
			int userId = User.login(email,password);
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO SessionDetails values (?,?)");
			stmt.setInt(1, userId);
			stmt.setString(2, sessionId);
			int times = stmt.executeUpdate();
			if(times == 1) {
				json.put("login", "success");
			}else {
				throw new Exception("");
			}
			Cookie cookie = new Cookie("userId", userId+"");
			Cookie sessionCookie = new Cookie("sessionId", sessionId);
			response.addCookie(cookie);
			response.addCookie(sessionCookie);
			stmt = conn.prepareStatement("select userName from UserDetails where userId = ?");
			stmt.setInt(1, userId);
			ResultSet output = stmt.executeQuery();
			output.next();
			JSONObject details = new JSONObject();
			details.put("username", output.getString(1));
			response.getWriter().write(details.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				json.put("status", "500");
				json.put("exception",String.valueOf(e.getClass()));
			} catch (JSONException e1) {
				System.out.println(e.getMessage());
			}
		}finally {
			response.getWriter().write(json.toString());
		}
	}

}
