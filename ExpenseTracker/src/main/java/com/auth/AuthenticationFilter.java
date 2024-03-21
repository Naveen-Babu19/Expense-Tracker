	package com.auth;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.mysql.connector.DBconnector;


@WebFilter("/app/*")
public class AuthenticationFilter extends HttpFilter implements Filter {
	
	private static final long serialVersionUID = 1L;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Connection dbConnector = DBconnector.getInstance().getConnection();
		JSONObject outputJSON =  new JSONObject();
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			Cookie[] existingCookies = req.getCookies();
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
			
			PreparedStatement stmt = dbConnector.prepareStatement("select sessionId, userId from SessionDetails where userId = ? and sessionId = ?");
			stmt.setString(2, sessionId);
			stmt.setInt(1, userId);
			System.out.println(sessionId+" - "+userId);
			ResultSet output = stmt.executeQuery();
			if(output.next()) {
				chain.doFilter(request, response);
			}
			else {
				outputJSON.put("status", "LoggedOut");
				response.getWriter().write(outputJSON.toString());
			}
		}catch(SQLException se) {
			System.out.println(se.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
