package br.com.rpires.dao.generic.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

	private static final String URL = "jdbc:h2:mem:modulo30;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
	private static final String USER = "sa";
	private static final String PASS = "";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}

	public static Connection initConnection() throws SQLException {
		return getConnection();
	}
}
