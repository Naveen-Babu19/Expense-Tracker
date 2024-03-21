package com.user.login;

import java.io.BufferedReader;
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

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.mysql.connector.DBconnector;


@WebServlet("/Signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		BufferedReader reader = request.getReader();
		StringBuilder jsonText = new StringBuilder();
		String line = "";
		while((line = reader.readLine()) != null) {
			
			jsonText.append(line);
		}
		try {
			JSONObject json = new JSONObject(jsonText.toString());
			String hashPass = BCrypt.hashpw(json.getString("password"), BCrypt.gensalt());
			conn = DBconnector.getInstance().getConnection();
			PreparedStatement stmt = conn.prepareStatement("select emailAddress, password from UserDetails where emailAddress like ?");
			stmt.setString(1, json.getString("email"));
			ResultSet rs = stmt.executeQuery();
			int userId = -1;	
			String sessionId = UUID.randomUUID().toString();
			if(!rs.next()) {
				stmt = conn.prepareStatement("insert into UserDetails (emailAddress, userName, password, income) values ('"+json.getString("email")+"','"+json.getString("username")+"','"+hashPass+"',"+json.getString("salary")+");");
				stmt.executeUpdate();
				stmt = conn.prepareStatement("select userId from UserDetails where emailAddress like ? and userName like ?");
				stmt.setString(1, json.getString("email"));
				stmt.setString(2, json.getString("username"));
				rs = stmt.executeQuery();
				if(rs.next()) {
					userId = rs.getInt(1);
				}
			}
			else {
				response.getWriter().write("User Already exist");
			}
			stmt = conn.prepareStatement("INSERT INTO SessionDetails values (?,?)");
			stmt.setInt(1, userId);
			stmt.setString(2, sessionId);
			if(stmt.executeUpdate() == 1) {
				json.put("login", "success");
			}else {
				throw new Exception("");
			}
			Cookie cookie = new Cookie("userId", userId+"");
			Cookie sessionCookie = new Cookie("sessionId", sessionId);
			response.addCookie(cookie);
			response.addCookie(sessionCookie);
			System.out.println(userId);
			stmt = conn.prepareStatement("insert into PersonalCategories (userId, categoryId) values (?,1),(?,2),(?,3),(?,4),(?,5),(?,6)");
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			stmt.setInt(3, userId);
			stmt.setInt(4, userId);
			stmt.setInt(5, userId);
			stmt.setInt(6, userId);
			stmt.executeUpdate();
			response.getWriter().write("success");
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}	

}
