package tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import matcher.DocMatcher;
import matcher.StrEDMatcher;
import tools.Info;
import tools.JiebaTokenizer;
import tools.OWLOntParse1;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import com.hp.hpl.jena.ontology.OntModel;
/**
 * 计算本体属性内部的相似度矩阵
 * @author Administrator
 */
public class PropertySimilarity1 {
	
	public static SparseDoubleMatrix2D getDPIdMatchingResult(OntModel ont){
		
		ArrayList<String> dpURIs = OWLOntParse1.getDatatypeProptyURIs(ont);
		int size=dpURIs.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(size,size);
		
		double similarity = 0.0;
		int i,j;
		String[] splitPro1 = null;
		String[] splitPro2 = null;
		
		for(i=0;i<size;i++){
			String a = ont.getDatatypeProperty(dpURIs.get(i)).getLocalName();
			if(a == null || a == ""){
				continue;
			}
			
			
			
			
			a = ont.getDatatypeProperty(dpURIs.get(i)).getDomain() + "__" + a + "_" + "datatype";
			
				
			
			
			
			
			
			splitPro1 = a.split("__");
			for(j=i+1;j<size;j++){	
				String b = ont.getDatatypeProperty(dpURIs.get(j)).getLocalName();
				if(b==null || b== ""){
					continue;
				}
				
				
				
				
				
				b = ont.getDatatypeProperty(dpURIs.get(j)).getDomain() + "__" + b + "_" + "datatype";
				
				
				
				
				splitPro2 = b.split("__");
				if(splitPro1[0].equals(splitPro2[0])){
					continue;
				}else{
					similarity = StrEDMatcher.getNormEDSim(splitPro1[1].split("_")[0],splitPro2[1].split("_")[0]);
					if(similarity>Info.FILTER)
						simMatrix.setQuick(i,j,similarity);
			}	
		}
			System.out.println("i is    " + i + "   computed similarity successfully");
		}	
		
		return simMatrix;
	}	
	
	public static SparseDoubleMatrix2D getOPIdMatchingResult(OntModel ont){
		
		ArrayList<String> opURIs = OWLOntParse1.getObjectProptyURIs(ont);
		int size=opURIs.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(size,size);
		
		double similarity = 0.0;
		int i,j;
		String[] splitPro1 = null;
		String[] splitPro2 = null;
		
		for(i=0;i<size;i++){
			String a = ont.getObjectProperty(opURIs.get(i)).getLocalName();
			if(a == null || a == ""){
				continue;
			}
			splitPro1 = a.split("__");
			for(j=i+1;j<size;j++){	
				String b = ont.getObjectProperty(opURIs.get(j)).getLocalName();
				if(b==null || b== ""){
					continue;
				}
				splitPro2 = b.split("__");
				if(splitPro1[0].equals(splitPro2[0])){
					continue;
				}else{
					similarity = StrEDMatcher.getNormEDSim(splitPro1[1].split("_")[0],splitPro2[1].split("_")[0]);
					if(similarity>Info.FILTER)
						simMatrix.setQuick(i,j,similarity);
			}	
		}
			System.out.println("i is    " + i + "   computed similarity successfully");
		}	
		
		return simMatrix;
	}	

	public static SparseDoubleMatrix2D getDPLabelMatchingResult(OntModel ont){
		
		ArrayList<String> dpURIs = OWLOntParse1.getDatatypeProptyURIs(ont);
		int size=dpURIs.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(size,size);
		
		HashSet<String> propertyDictSet = new HashSet<String>();
		HashMap<String, Integer> propertyDictIDFInfo= new HashMap<String, Integer>();
		ArrayList<HashSet<String>> listOfSets = new ArrayList<HashSet<String>>();
		
		try {		
			//这句话非常耗时(2s)
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
			//先遍历一遍得到对应的词典，与此同时存储对应的词的IDF所需要的信息，这个循环有两个目的。
			for(int i=0; i < size;i++){
				String label = ont.getDatatypeProperty(dpURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//对没有标注的不放入词典
					listOfSets.add(new HashSet<String>());
					continue;
				}
				//This step must have true value. 
				
				
				
				
				
//				label = ont.getDatatypeProperty(dpURIs.get(i)).getDomain() + "__" + label;
//				System.out.println(label);
				
				
				
				
				
				String[] splitLabel = label.split("__");
				
				List<String> chinesePropertyLabels = test_Tokenizer.Tokenizer(splitLabel[1]);
				propertyDictSet.addAll(chinesePropertyLabels);
				
				//在计算IDF之前对每个文档去重
				HashSet<String> set=new HashSet<String>(chinesePropertyLabels);
				listOfSets.add(set);
				
				
				for(String temp : set){
					if(propertyDictIDFInfo.containsKey(temp)){
						propertyDictIDFInfo.put(temp, propertyDictIDFInfo.get(temp) + 1);
					}else{
						propertyDictIDFInfo.put(temp,1);
					}
				}
			}	
			
			
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(propertyDictSet);
			System.out.println(dictHashMap);
			
			//事先构造所有文本的向量空间 (注意这里在28所实测下来，用double存储向量空间在内存中是没有问题的，效率比压缩数组稍快)
			int n = propertyDictSet.size();
			//构造所有文本的向量空间,这里还是用汪老师建议的压缩数组来操作，但是缺点是压缩数组取的时候需要遍历。
			SparseDoubleMatrix2D vecHouse=new SparseDoubleMatrix2D(size, n);
			
			for(int i=0;i<size;i++){
				
				String label = ont.getDatatypeProperty(dpURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//这里开始存0向量
					continue;
				}
				
				
				
				
				
//				label = ont.getDatatypeProperty(dpURIs.get(i)).getDomain() + "__" + label;
//				System.out.println(label);
				
				
				
				
				
				//This step must have value...Don't Worry...
				List<String> temp=test_Tokenizer.Tokenizer(label.split("__")[1]);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(temp, size, propertyDictIDFInfo, dictHashMap);
				//必须循环放入，缺点
				for(int j=0;j<n;j++){
					if(vec[j]==0){
						//不存直接跳过使得矩阵变得稀疏
						continue;
					}
					vecHouse.setQuick(i,j,vec[j]);
				}
			}
			
			//在算相似度之前清空一下内存中其实已经不再用到的数据。
			propertyDictSet.clear();
			propertyDictIDFInfo.clear();
			propertyDictSet=null;
			propertyDictIDFInfo=null;
			System.gc();
			
			
			
			HashSet<String> set1=null;
			HashSet<String> set2=null;
			String[] splitLabel1=null;
			String[] splitLabel2=null;
			double similarity=0.0;
			for(int i=0;i<size;i++){
				String label1 = ont.getDatatypeProperty(dpURIs.get(i)).getLabel(null);
				if(label1== "" || label1==null){
					continue;
				}
				
				
		//		label1 = ont.getDatatypeProperty(dpURIs.get(i)).getDomain() + "__" + label1;
				
				
				
				splitLabel1=label1.split("__");
				set1=(HashSet<String>)listOfSets.get(i).clone();
				for(int j=i+1;j<size;j++){	
					String label2 = ont.getDatatypeProperty(dpURIs.get(j)).getLabel(null);
					if(label2== "" || label2==null){
						continue;
					}
					
					
					
					
			//		label2 = ont.getDatatypeProperty(dpURIs.get(j)).getDomain() + "__" + label2;
					
					
					
					
					splitLabel2=label2.split("__");
					//顺序都是一一对应的	
					set2=(HashSet<String>) listOfSets.get(j).clone();
					set2.retainAll(set1);
					//如果说两个句子之间没有公共的东西那么不用再计算了。肯定不可能相似。
					if(set2.size()==0){
						continue;
					}
					//如果是同一个概念下的属性，仅仅是为了这个操作,不然可以直接算。
					if(splitLabel1[0].equals(splitLabel2[0])){
						continue;
					}else{
						similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouse,i,j,n);
						if(similarity>Info.FILTER)
							simMatrix.setQuick(i,j,similarity);	
						
				}
			}
				listOfSets.get(i).clear();
				System.out.println("i is    " + i + "   computed similarity successfully");
		 }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simMatrix;		
	}	

	public static SparseDoubleMatrix2D getOPLabelMatchingResult(OntModel ont){	
		ArrayList<String> opURIs = OWLOntParse1.getObjectProptyURIs(ont);
		int size=opURIs.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(size,size);
		
		HashSet<String> propertyDictSet = new HashSet<String>();
		HashMap<String, Integer> propertyDictIDFInfo= new HashMap<String, Integer>();
		ArrayList<HashSet<String>> listOfSets = new ArrayList<HashSet<String>>();
		
		try {		
			//这句话非常耗时(2s)
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
			//先遍历一遍得到对应的词典，与此同时存储对应的词的IDF所需要的信息，这个循环有两个目的。
			for(int i=0; i < size;i++){
				String label = ont.getObjectProperty(opURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//对没有标注的不放入词典
					listOfSets.add(new HashSet<String>());
					continue;
				}
				//This step must have true value. 
				String[] splitLabel = label.split("__");
				
				List<String> chinesePropertyLabels = test_Tokenizer.Tokenizer(splitLabel[1]);
				propertyDictSet.addAll(chinesePropertyLabels);
				
				//在计算IDF之前对每个文档去重
				HashSet<String> set=new HashSet<String>(chinesePropertyLabels);
				listOfSets.add(set);
				
				
				for(String temp : set){
					if(propertyDictIDFInfo.containsKey(temp)){
						propertyDictIDFInfo.put(temp, propertyDictIDFInfo.get(temp) + 1);
					}else{
						propertyDictIDFInfo.put(temp,1);
					}
				}
			}	
			
			
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(propertyDictSet);
			System.out.println(dictHashMap);
			
			//事先构造所有文本的向量空间 (注意这里在28所实测下来，用double存储向量空间在内存中是没有问题的，效率比压缩数组稍快)
			int n = propertyDictSet.size();
			//构造所有文本的向量空间,这里还是用汪老师建议的压缩数组来操作，但是缺点是压缩数组取的时候需要遍历。
			SparseDoubleMatrix2D vecHouse=new SparseDoubleMatrix2D(size, n);
			
			for(int i=0;i<size;i++){
				
				String label = ont.getObjectProperty(opURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//这里开始存0向量
					continue;
				}
				//This step must have value...Don't Worry...
				List<String> temp=test_Tokenizer.Tokenizer(label.split("__")[1]);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(temp, size, propertyDictIDFInfo, dictHashMap);
				//必须循环放入，缺点
				for(int j=0;j<n;j++){
					if(vec[j]==0){
						//不存直接跳过使得矩阵变得稀疏
						continue;
					}
					vecHouse.setQuick(i,j,vec[j]);
				}
			}
			
			//在算相似度之前清空一下内存中其实已经不再用到的数据。
			propertyDictSet.clear();
			propertyDictIDFInfo.clear();
			propertyDictSet=null;
			propertyDictIDFInfo=null;
			System.gc();
			
			
			
			HashSet<String> set1=null;
			HashSet<String> set2=null;
			String[] splitLabel1=null;
			String[] splitLabel2=null;
			double similarity=0.0;
			for(int i=0;i<size;i++){
				String label1 = ont.getObjectProperty(opURIs.get(i)).getLabel(null);
				if(label1== "" || label1==null){
					continue;
				}
				splitLabel1=label1.split("__");
				set1=(HashSet<String>)listOfSets.get(i).clone();
				for(int j=i+1;j<size;j++){	
					String label2 = ont.getObjectProperty(opURIs.get(j)).getLabel(null);
					if(label2== "" || label2==null){
						continue;
					}
					splitLabel2=label2.split("__");
					//顺序都是一一对应的	
					set2=(HashSet<String>) listOfSets.get(j).clone();
					set2.retainAll(set1);
					//如果说两个句子之间没有公共的东西那么不用再计算了。肯定不可能相似。
					if(set2.size()==0){
						continue;
					}
					//如果是同一个概念下的属性，仅仅是为了这个操作,不然可以直接算。
					if(splitLabel1[0].equals(splitLabel2[0])){
						continue;
					}else{
						similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouse,i,j,n);
						if(similarity>Info.FILTER)
							simMatrix.setQuick(i,j,similarity);	
						
				}
			}
				listOfSets.get(i).clear();
				System.out.println("i is    " + i + "   computed similarity successfully");
		 }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simMatrix;	
	}	

}