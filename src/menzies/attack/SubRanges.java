package menzies.attack;

import java.util.*;

public class SubRanges {
	//suppose dividePoints contains {4, 9, 14}
	//it means the divided ranges are (-infinity, 4] (4, 9] (9,14] (14, infinity)
	private String attr = null;
	private ArrayList<Double> dividePoint = null;
	private ArrayList<Integer> bin = null;
	
	public SubRanges(double[] dividePoint, String attr){
		this.attr = attr;
		this.dividePoint = new ArrayList<Double>();
		for(double e:dividePoint){
			this.dividePoint.add(e);
		}
		Collections.sort(this.dividePoint);
		this.bin = new ArrayList<Integer>();
		for(int i=0; i<this.dividePoint.size()+1; i++)
			this.bin.add(0);
	}
	
	public String getAttr(){
		return this.attr;
	}
	
	public int numberOf(int i){
		return this.bin.indexOf(i);
	}
	
	public int[] numberAll(){
		int[] result = new int[this.bin.size()];
		for(int i=0; i<this.bin.size(); i++){
			result[i] = this.bin.get(i);
		}
		return result;
	}
	
	//find the index of certain number it belongs to
	public int findIndex(double num){
		for(int i=0; i<this.dividePoint.size(); i++){
			if(this.dividePoint.get(i) >= num){
				return i;
			}
		}
		return this.dividePoint.size();
	}
	
	public void putNumber(double num){
		int index = this.findIndex(num);
		int element = this.bin.get(index);
		this.bin.set(index, element+1);
	}
	
	public int getMax(){
		int max = 0;
		for(int e:this.bin){
			if(max < e){
				max = e;
			}
		}
		return this.bin.indexOf(max);
	}
	
	public void clear(){
		for(int i=0; i<this.bin.size(); i++)
			this.bin.set(i, 0);
	}
	
	public static void main(String[] args){
		double[] dividePoints = new double[4];
		dividePoints[0] = 10;
		dividePoints[1] = 20;
		dividePoints[2] = 30;
		dividePoints[3] = 40;
		SubRanges sub = new SubRanges(dividePoints, "test");
		
		for(int i=0; i<10; i++){
			sub.putNumber(7*i);
		}
		int[] result = sub.numberAll();
		for(int i=0; i<result.length; i++){
			System.out.println(result[i]);
		}
	}
}
