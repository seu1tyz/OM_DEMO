package tools;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import matcher.DocMatcher;
import matcher.StrEDMatcher;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.sparql.pfunction.library.concat;
/**
 * ƥ�䱾���ڲ������������
 * @author seu1tyz
 */
public class ClassSimilarity1 {
	

	public static SparseDoubleMatrix2D getClassIdMatchingResult(OntModel ont){
		ArrayList<String> classURIs = OWLOntParse1.getAllClassesURIs(ont);
		int size=classURIs.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(size,size);
		int i,j;
		double similarity =0;
		//Ϊ�˸���������˼·��ֱ�ӱ���������󣬲��Ƕ����һά�������������˼·���������±�������Ը���������⡣������һ����
		for(i=0;i<size;i++){
			String a = ont.getOntClass(classURIs.get(i)).getLocalName();
			if(a == null || a == ""){
				continue;
			}
			for(j=i+1;j<size;j++){
				String b = ont.getOntClass(classURIs.get(j)).getLocalName();
				if(b == null || b == ""){
					continue;
				}
				similarity = StrEDMatcher.getNormEDSim(a,b);
				if(similarity > Info.FILTER)
					simMatrix.setQuick(i,j,similarity);
			}
			System.out.println("i is    " + i + "   computed similarity successfully");
		}	
		return simMatrix;
	}
	
	
	
	
	public static SparseDoubleMatrix2D getClassLabelMatchingResult(OntModel ont){	
		
		ArrayList<String> classURIs = OWLOntParse1.getAllClassesURIs(ont);
		int size=classURIs.size();
		
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(size,size);
		
		HashSet<String> classDictSet = new HashSet<String>();
		HashMap<String, Integer> classDictIDFInfo = new HashMap<String, Integer>();
		//���ں����û�н����ļ���ֱ��������������һ�οռ任ʱ�䡣
		ArrayList<HashSet<String>> listOfSets = new ArrayList<HashSet<String>>();
		
		try {	
			//�ȱ���һ��õ���Ӧ�Ĵʵ䣬ͬʱ��ȡÿ���ʵ�IDF����Ϣ
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();		
			for(int i=0; i<size;i++){
				String label = ont.getOntClass(classURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//��û�б�ע�Ĳ�����ʵ�
					listOfSets.add(new HashSet<String>());
					continue;
				}
				List<String> chineseClassLabels = test_Tokenizer.Tokenizer(label);
				classDictSet.addAll(chineseClassLabels);
				
				//��ÿ���ĵ�ȥ�أ���֤IDF�������ȷ.
				HashSet<String> set=new HashSet<String>(chineseClassLabels);
				listOfSets.add(set);
				
				
				for(String temp:set){
					if(classDictIDFInfo.containsKey(temp)){
						classDictIDFInfo.put(temp, classDictIDFInfo.get(temp) + 1);
					}else{
						classDictIDFInfo.put(temp,1);
					}
				}
			}	
			
			
			//�õ��ʵ���ÿ�����ڿռ������е�ά�ȡ�
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(classDictSet);
			System.out.println(dictHashMap);
			//���������ı��������ռ�,���ﻹ��������ʦ�����ѹ������������������ȱ����ѹ������ȡ��ʱ����Ҫ����
			int n = classDictSet.size();
			SparseDoubleMatrix2D vecHouse=new SparseDoubleMatrix2D(size, n);
			
			for(int i=0;i<size;i++){
				String label = ont.getOntClass(classURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					continue;
				}
				List<String> temp=test_Tokenizer.Tokenizer(label);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(temp, size, classDictIDFInfo, dictHashMap);
				//����ѭ�����룬ȱ��
				for(int j=0;j<n;j++){
					if(vec[j]==0){
						//����ֱ������ʹ�þ�����ϡ��
						continue;
					}
					vecHouse.setQuick(i,j,vec[j]);
				}
			}	
		
			//�������ƶ�֮ǰ���һ���ڴ��к�����ʵ�ò��������ݡ�
			classDictSet.clear();
			classDictIDFInfo.clear();
			classDictSet=null;
			classDictIDFInfo=null;
			System.gc();
			
			
			HashSet<String> set1=null;
			HashSet<String> set2=null;
			double similarity=0.0;
			int i,j;
			
			for(i=0;i<size;i++){
				set1=(HashSet<String>)listOfSets.get(i).clone();
				for(j=i+1;j<size;j++){
					//˳����һһ��Ӧ��	
					set2=(HashSet<String>) listOfSets.get(j).clone();
					set2.retainAll(set1);
					//���˵��������֮��û�й����Ķ�����ô�����ټ����ˡ��϶����������ơ�
					if(set2.size()==0){
						continue;
					}
					//�������ʦ�Ľ��黹�ǲ��ö�ѹ����������������
					similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouse,i,j,n);
						
					if(similarity>Info.FILTER)
						simMatrix.setQuick(i,j,similarity);	
				}
				listOfSets.get(i).clear();
				System.out.println("i is    " + i +"   computed similarity successfully");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simMatrix;
	}
	
	
}
