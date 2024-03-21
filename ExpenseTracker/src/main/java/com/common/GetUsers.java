package com.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/app/GetUsers")
public class GetUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		try {		
			int userId = -1;
			for(int index = 0; index < cookies.length; index++) {
				Cookie currCookie = cookies[index];
				if(currCookie.getName().equals("userId")) {
					userId = Integer.parseInt(currCookie.getValue());
				}					
			}
			Util manager = Util.getInstance();
			JSONObject users = manager.getUsers(userId);
			response.getWriter().write(users.toString());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
