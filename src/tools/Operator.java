package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class Operator {
	/**
	 * 将两个矩阵取平均操作
	 * @param idMatrix
	 * @param labelMatrix
	 * @return
	 */
	
	public static SparseDoubleMatrix2D averageMatrix_1(SparseDoubleMatrix2D idMatrix,SparseDoubleMatrix2D labelMatrix){
		int size=idMatrix.rows();
		//SparseDoubleMatrix2D resultmatrix=new SparseDoubleMatrix2D(size, size);
		double id_weight = 0.2;
		double label_weight = 0.8;
//		if(){
//		    id_weight = ...
///		    label_weight = ..
//		}else if(){
//			
//		}
		for(int i=0;i<size;i++){
			for(int j=i+1;j<size;j++){	
				double sim=(idMatrix.getQuick(i, j)*id_weight+labelMatrix.getQuick(i, j)*label_weight);
				//if(sim>Info.FILTER)
				idMatrix.setQuick(i, j, sim);
			}
		}
		
		return idMatrix;	
	}
	
	public static SparseDoubleMatrix2D averageMatrix_2(SparseDoubleMatrix2D idMatrix,SparseDoubleMatrix2D labelMatrix){
		

		double id_weight = 0.2;
		double label_weight = 0.8;
//		if(){
//		  id_weight = ...
//		  label_weight = ...
//			
//		}else if(){
//			
//		}
		int m = idMatrix.rows();
		int n = idMatrix.columns();
		
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){	
				double sim=(idMatrix.getQuick(i, j)*id_weight+labelMatrix.getQuick(i, j)*label_weight);
				idMatrix.setQuick(i, j, sim);
			}
		}
		
		return idMatrix;	
	}

	
	
	/*
	 * 将字典转换为hashmap形式，key为词名，value为这个词在空间向量中处于第几个维度，该值从0开始；
	*/
	public static HashMap<String,Integer> dictToMap(HashSet<String> dictSet){
		
		HashMap<String,Integer> dictHashMap = new HashMap<String,Integer>();
		int indexNum = 0;
		for(String temp : dictSet){
			dictHashMap.put(temp, indexNum++);
		}
		return dictHashMap;		
	}
	
	
	
	public static void writeInfo2file(ArrayList<String> outputList) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter bufferedWriter = null;
		String path = "C:\\Users\\Ethan\\Desktop\\N.txt";
		File file = new File(path);
		bufferedWriter = new BufferedWriter(new FileWriter(file));
		for(String temp:outputList){
			bufferedWriter.write(temp + "\r\n");
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	
}
