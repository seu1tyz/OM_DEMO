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
 * 匹配本体内部概念任务的类
 * @author seu1tyz
 */
public class ClassSimilarity1 {
	

	public static SparseDoubleMatrix2D getClassIdMatchingResult(OntModel ont){
		ArrayList<String> classURIs = OWLOntParse1.getAllClassesURIs(ont);
		int size=classURIs.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(size,size);
		int i,j;
		double similarity =0;
		//为了更加清晰的思路，直接遍历这个矩阵，不是对这个一维数组遍历，这样思路更清晰，下标更加明显更加容易理解。本质是一样的
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
		//用于后面对没有交集的集合直接跳过，还是用一次空间换时间。
		ArrayList<HashSet<String>> listOfSets = new ArrayList<HashSet<String>>();
		
		try {	
			//先遍历一遍得到对应的词典，同时获取每个词的IDF的信息
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();		
			for(int i=0; i<size;i++){
				String label = ont.getOntClass(classURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					//对没有标注的不放入词典
					listOfSets.add(new HashSet<String>());
					continue;
				}
				List<String> chineseClassLabels = test_Tokenizer.Tokenizer(label);
				classDictSet.addAll(chineseClassLabels);
				
				//对每个文档去重，保证IDF计算的正确.
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
			
			
			//得到词典中每个词在空间向量中的维度。
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(classDictSet);
			System.out.println(dictHashMap);
			//构造所有文本的向量空间,这里还是用汪老师建议的压缩数组来操作，但是缺点是压缩数组取的时候需要遍历
			int n = classDictSet.size();
			SparseDoubleMatrix2D vecHouse=new SparseDoubleMatrix2D(size, n);
			
			for(int i=0;i<size;i++){
				String label = ont.getOntClass(classURIs.get(i)).getLabel(null);
				if(label == null || label == ""){
					continue;
				}
				List<String> temp=test_Tokenizer.Tokenizer(label);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(temp, size, classDictIDFInfo, dictHashMap);
				//必须循环放入，缺点
				for(int j=0;j<n;j++){
					if(vec[j]==0){
						//不存直接跳过使得矩阵变得稀疏
						continue;
					}
					vecHouse.setQuick(i,j,vec[j]);
				}
			}	
		
			//在算相似度之前清空一下内存中后面其实用不到的数据。
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
					//顺序都是一一对应的	
					set2=(HashSet<String>) listOfSets.get(j).clone();
					set2.retainAll(set1);
					//如果说两个句子之间没有公共的东西那么不用再计算了。肯定不可能相似。
					if(set2.size()==0){
						continue;
					}
					//针对汪老师的建议还是采用对压缩矩阵来计算向量
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
