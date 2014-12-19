package menzies.attack;

import java.util.*;
import java.io.*;
import java.sql.*;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import menzies.database.*;

public class Attack {
	
	static private int totalBin = 400;
	static final private String user = "root";
	static final private String password = "2543120";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//how many bins we divided
		
		//args contains two tables' name, query size and sensitive attributes
		//args[0] is the origin table's name
		//args[1] is the privatized table's name
		//args[2] is the query size
		//args[3] is the sensitive attributes, which are separated by ";"
		/*if(args.length != 4){
			System.out.println("usage: origin_name privatized_name query_size arribute1;arribute2;...");
		}*/
		try{
			File file = new File("config.txt");
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = input.readLine();
			args = line.split(" ");
			input.close();
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		
		String[] attrs = args[3].split(";");
		
		Query[] queries = queryGenerator(args[0], Integer.parseInt(args[2]), attrs);
		
		//subrange generator
		
		ArrayList<SubRanges> subs = new ArrayList<SubRanges>();
		for(String attr:attrs){
			subs.add(subrangesGenerator(attr, args[0]));
		}
		
		int sumBreach = 0;
		Database database = new Database();
		for(Query query:queries){
			//G1 and G2
			if(query == null)
				System.out.println("nani!");
			String sql = query.getSQL();
			System.out.println(sql);
			String[] tokens = sql.split(" ");
			tokens[3] = args[1];
			sql = "";
			for (int index = 0; index < tokens.length; index++)
				sql += tokens[index] + " ";
			ResultSet rs1 = database.executeQuery(query.getSQL());
			ResultSet rs2 = database.executeQuery(sql);
			
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
						System.out.println("same: " + maxG1 + " " + maxG2);
						sumBreach++;
						break;
					}else{
						System.out.println("not same: " + maxG1 + " " + maxG2);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		//final result
		System.out.println((double)sumBreach/Integer.parseInt(args[2]));
		
	}

	public static Query[] queryGenerator(String tableName, int n, String[] sAttr){
		//get non sensitive attribute
		Database database = new Database();
		ArrayList<String> nsAttr = database.getColumnName(tableName);
		for(int i=0; i<sAttr.length; i++){
			nsAttr.remove(sAttr[i]);
		}
		
		//generate random number
		Random random = new Random();
		
		Query[] result = new Query[n];
		
		for (int j = 0; j < n; j++) {
			// generate one query
			try {
				InstanceQuery query;
				query = new InstanceQuery();
				query.setUsername(Attack.user);
				query.setPassword(Attack.password);

				int arity = random.nextInt(3)+1;
				SubRange[] subs = new SubRange[arity];
				for (int i = 0; i < arity; i++) {
					// get attribute
					int index = random.nextInt(nsAttr.size());
					String attr = nsAttr.get(index);

					query.setQuery("select " + attr + " from " + tableName);
					Instances data = query.retrieveInstances();

					Discretize filter = new Discretize();
					filter.setBins(Attack.totalBin);
					filter.setInputFormat(data);
					filter.setUseEqualFrequency(true);
					Instances data_new = Filter.useFilter(data, filter);

					double[] cutPoints = filter.getCutPoints(data_new
							.attribute(attr).index());
					//ignore string type attr
					if (cutPoints == null){
						i--;
						continue;
					}
					///////////////////////////
					//System.out.println("cutPoints: " + cutPoints.length);
					
					double low, high;
					index = random.nextInt(cutPoints.length + 1);
					if (index == 0) {
						low = Double.NEGATIVE_INFINITY;
						high = cutPoints[index];
					} else if (index == cutPoints.length) {
						low = cutPoints[cutPoints.length - 1];
						high = Double.POSITIVE_INFINITY;
					} else {
						low = cutPoints[index - 1];
						high = cutPoints[index];
					}
					subs[i] = new SubRange(low, high, attr);
				}
				// generate sql
				String sql = "select ";
				/*for (int i = 0; i < arity - 1; i++) {
					sql = sql + subs[i].attr + ",";
				}
				sql = sql + subs[subs.length - 1].attr + " from " + tableName
						+ " where ";*/
				sql = sql + "*" + " from " + tableName
						+ " where ";
				for (int i = 0; i < arity - 1; i++) {
					sql = sql + Attack.range2str(subs[i]) + " and ";
				}
				//System.out.println(subs.length);
				sql = sql + Attack.range2str(subs[subs.length - 1]) + " ;";
				//System.out.println(j + ": " + sql);
				
				//test whether this works
				ResultSet rs = database.executeQuery(sql);
				//ignore the query that has no result.
				if(!rs.first()){
					j--;
					continue;
				}
				//////////////////////
				result[j] = new Query(subs, sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//return result;
		}
		return result;
	}
	
	private static String range2str(SubRange sub){
		String result = "";
		if(sub.lower == Double.NEGATIVE_INFINITY){
			result = sub.attr + "<" + Double.toString(sub.higher);
		}else if(sub.higher == Double.POSITIVE_INFINITY){
			result = Double.toString(sub.lower) + "<" + sub.attr;
		}else{
			result = Double.toString(sub.lower) + "<" + sub.attr + " and " + sub.attr + "<" + Double.toString(sub.higher);
		}
		return result;
	}
	
	public static SubRanges subrangesGenerator(String attr, String tableName){
		//connect to the database
		try {
			InstanceQuery query;
			query = new InstanceQuery();
			query.setUsername(Attack.user);
			query.setPassword(Attack.password);
			query.setQuery("select "+ attr +" from " + tableName);
			Instances data = query.retrieveInstances();
			
			Discretize filter = new Discretize();
			filter.setBins(Attack.totalBin);
			filter.setInputFormat(data);
			filter.setUseEqualFrequency(true);
			Instances data_new = Filter.useFilter(data, filter);
			
			double[] cutPoints = filter.getCutPoints(data_new.attribute(attr).index());
			//System.out.println(data_new.attribute(0).name());
			/*for(int i=0; i<cutPoints.length; i++)
				System.out.println(cutPoints[i]);*/
			
			//this may exist potential problem.
			return new SubRanges(cutPoints, attr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
