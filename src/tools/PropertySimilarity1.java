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
 * ���㱾�������ڲ������ƶȾ���
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
			//��仰�ǳ���ʱ(2s)
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
			//�ȱ���һ��õ���Ӧ�Ĵʵ䣬���ͬʱ�洢��Ӧ�Ĵʵ�IDF����Ҫ����Ϣ�����ѭ��������Ŀ�ġ�
			for(int i=0; i < size;i++){
				String label = ont.getDatatypeProperty(dpURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//��û�б�ע�Ĳ�����ʵ�
					listOfSets.add(new HashSet<String>());
					continue;
				}
				//This step must have true value. 
				
				
				
				
				
//				label = ont.getDatatypeProperty(dpURIs.get(i)).getDomain() + "__" + label;
//				System.out.println(label);
				
				
				
				
				
				String[] splitLabel = label.split("__");
				
				List<String> chinesePropertyLabels = test_Tokenizer.Tokenizer(splitLabel[1]);
				propertyDictSet.addAll(chinesePropertyLabels);
				
				//�ڼ���IDF֮ǰ��ÿ���ĵ�ȥ��
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
			
			//���ȹ��������ı��������ռ� (ע��������28��ʵ����������double�洢�����ռ����ڴ�����û������ģ�Ч�ʱ�ѹ�������Կ�)
			int n = propertyDictSet.size();
			//���������ı��������ռ�,���ﻹ��������ʦ�����ѹ������������������ȱ����ѹ������ȡ��ʱ����Ҫ������
			SparseDoubleMatrix2D vecHouse=new SparseDoubleMatrix2D(size, n);
			
			for(int i=0;i<size;i++){
				
				String label = ont.getDatatypeProperty(dpURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//���￪ʼ��0����
					continue;
				}
				
				
				
				
				
//				label = ont.getDatatypeProperty(dpURIs.get(i)).getDomain() + "__" + label;
//				System.out.println(label);
				
				
				
				
				
				//This step must have value...Don't Worry...
				List<String> temp=test_Tokenizer.Tokenizer(label.split("__")[1]);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(temp, size, propertyDictIDFInfo, dictHashMap);
				//����ѭ�����룬ȱ��
				for(int j=0;j<n;j++){
					if(vec[j]==0){
						//����ֱ������ʹ�þ�����ϡ��
						continue;
					}
					vecHouse.setQuick(i,j,vec[j]);
				}
			}
			
			//�������ƶ�֮ǰ���һ���ڴ�����ʵ�Ѿ������õ������ݡ�
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
					//˳����һһ��Ӧ��	
					set2=(HashSet<String>) listOfSets.get(j).clone();
					set2.retainAll(set1);
					//���˵��������֮��û�й����Ķ�����ô�����ټ����ˡ��϶����������ơ�
					if(set2.size()==0){
						continue;
					}
					//�����ͬһ�������µ����ԣ�������Ϊ���������,��Ȼ����ֱ���㡣
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
			//��仰�ǳ���ʱ(2s)
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
			//�ȱ���һ��õ���Ӧ�Ĵʵ䣬���ͬʱ�洢��Ӧ�Ĵʵ�IDF����Ҫ����Ϣ�����ѭ��������Ŀ�ġ�
			for(int i=0; i < size;i++){
				String label = ont.getObjectProperty(opURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//��û�б�ע�Ĳ�����ʵ�
					listOfSets.add(new HashSet<String>());
					continue;
				}
				//This step must have true value. 
				String[] splitLabel = label.split("__");
				
				List<String> chinesePropertyLabels = test_Tokenizer.Tokenizer(splitLabel[1]);
				propertyDictSet.addAll(chinesePropertyLabels);
				
				//�ڼ���IDF֮ǰ��ÿ���ĵ�ȥ��
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
			
			//���ȹ��������ı��������ռ� (ע��������28��ʵ����������double�洢�����ռ����ڴ�����û������ģ�Ч�ʱ�ѹ�������Կ�)
			int n = propertyDictSet.size();
			//���������ı��������ռ�,���ﻹ��������ʦ�����ѹ������������������ȱ����ѹ������ȡ��ʱ����Ҫ������
			SparseDoubleMatrix2D vecHouse=new SparseDoubleMatrix2D(size, n);
			
			for(int i=0;i<size;i++){
				
				String label = ont.getObjectProperty(opURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//���￪ʼ��0����
					continue;
				}
				//This step must have value...Don't Worry...
				List<String> temp=test_Tokenizer.Tokenizer(label.split("__")[1]);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(temp, size, propertyDictIDFInfo, dictHashMap);
				//����ѭ�����룬ȱ��
				for(int j=0;j<n;j++){
					if(vec[j]==0){
						//����ֱ������ʹ�þ�����ϡ��
						continue;
					}
					vecHouse.setQuick(i,j,vec[j]);
				}
			}
			
			//�������ƶ�֮ǰ���һ���ڴ�����ʵ�Ѿ������õ������ݡ�
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
					//˳����һһ��Ӧ��	
					set2=(HashSet<String>) listOfSets.get(j).clone();
					set2.retainAll(set1);
					//���˵��������֮��û�й����Ķ�����ô�����ټ����ˡ��϶����������ơ�
					if(set2.size()==0){
						continue;
					}
					//�����ͬһ�������µ����ԣ�������Ϊ���������,��Ȼ����ֱ���㡣
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