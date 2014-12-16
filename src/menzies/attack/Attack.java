package menzies.attack;

import java.util.*;
import java.sql.*;

import menzies.database.*;

public class Attack {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//args contains two tables' name, query size and sensitive attributes
		//args[0] is the origin table's name
		//args[1] is the privatized table's name
		//args[2] is the query size
		//args[3] is the sensitive attributes, which are separated by ";"
		if(args.length != 4){
			System.out.println("usage: origin_name privatized_name query_size arribute1;arribute2;...");
		}
		Query[] queries = queryGenerator(args[0], Integer.parseInt(args[1]));
		
		//subrange generator
		String[] attrs = args[3].split(";");
		ArrayList<SubRanges> subs = new ArrayList<SubRanges>();
		for(String attr:attrs){
			subs.add(subrangesGenerator(attr, args[0]));
		}
		
		int sumBreach = 0;
		Database database = new Database();
		for(Query query:queries){
			//G1 and G2
			ResultSet rs1 = database.executeQuery(query.getSQL());
			ResultSet rs2 = database.executeQuery(query.getSQL());
			
			try {
				for(SubRanges sub:subs) {
					// smax(G1)
					rs1.beforeFirst();
					while(rs1.next()) {
						sub.putNumber(rs1.getDouble(sub.getAttr()));
					}
					int maxG1 = sub.getMax();
					sub.clear();
					// smax(G2)
					rs2.beforeFirst();
					while(rs2.next()){
						sub.putNumber(rs2.getDouble(sub.getAttr()));
					}
					int maxG2 = sub.getMax();
					if (maxG1 == maxG2){
						sumBreach++;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		//final result
		System.out.println(sumBreach/Integer.parseInt(args[1]));
		
	}

	public static Query[] queryGenerator(String tableName, int n){
		return null;
	}
	
	public static SubRanges subrangesGenerator(String attr, String tableName){
		return null;
	}
}
