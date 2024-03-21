package com.common;

public class SQLQueries {
	public static final String GET_TOTAL_USERS = "SELECT userId, username FROM UserDetails WHERE userId <> ?";
}
