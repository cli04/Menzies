package menzies.database;

import java.sql.*;

public class Database {
	private Connection con = null;
	
	public Database(){
		try{
			Connection connect = (Connection) DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/simpletest",
					"root", "2543120");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQuery(String sql){
		try{
			Statement stmt = this.con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
