package menzies.database;

import java.sql.*;
import java.util.*;

public class Database {
	private Connection con = null;
	
	public Database(){
		try{
			this.con = (Connection) DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/minecraft",
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
	
	public ArrayList<String> getColumnName(String tableName){
		try{
			Statement stmt = this.con.createStatement();
			String sql = "select * from " + tableName;
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			ArrayList<String> result = new ArrayList<String>();
			for(int i=0; i<md.getColumnCount(); i++){
				result.add(md.getColumnName(i+1));
			}
			return result;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
