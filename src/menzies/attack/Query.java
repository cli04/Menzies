package menzies.attack;

import java.util.*;

public class Query {
	private ArrayList<SubRange> attributes = null;
	private String sql = null;
	
	public Query(SubRange[] attrs, String sql){
		this.attributes = new ArrayList<SubRange>();
		for(int i=0; i<attrs.length; i++){
			this.attributes.add(attrs[i]);
		}
		this.sql = sql;
	}
	
	public String getSQL(){
		return this.sql;
	}
	
	public SubRange[] getAttr(){
		SubRange[] result = new SubRange[this.attributes.size()];
		for(int i=0; i<this.attributes.size(); i++){
			result[i] = this.attributes.get(i);
		}
		return result;
	}
}
