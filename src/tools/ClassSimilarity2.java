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

import com.hp.hpl.jena.ontology.OntModel;
/**
 * 匹配本体内部概念任务的类
 * @author seu1tyz
 */
public class ClassSimilarity2 {
	
	/**两个本体的ClassId的一个匹配，考虑sameAs的情况。
	 * @param o1    本体1
	 * @param o2    本体2
	 * @returntr
	 */
	 public static SparseDoubleMatrix2D getClassIdMatchingResult(OntModel o1,OntModel o2) {
		 
			int i, j;sfgsdfg
			ArrayList<HashSet<String>> o1ClassIds = OWLOntParse2.getAllClassesIDsSameAsInfo2(o1);
			ArrayList<HashSet<String>> o2ClassIds = OWLOntParse2.getAllClassesIDsSameAsInfo2(o2);
			
			int m = o1ClassIds.size();
			int n = o2ClassIds.size();
			//o1是竖着的，o2是横着的
			SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(m,n);
			double similarity = 0.0;
			HashSet<String> o1HashSet = null;
			HashSet<String> o2HashSet = null;
			for (i = 0; i < m; i++) {
				o1HashSet = o1ClassIds.get(i);
				int o1Size = o1HashSet.size();
				for (j = 0; j < n; j++) {	
					o2HashSet = o2ClassIds.get(j);
					int o2Size = o2HashSet.size();
					//防止出现某种情况
					if(o1HashSet.equals(o2HashSet)){
						simMatrix.setQuick(i, j, 1.0);
						continue;
					}			
					//说明里面只有一个元素的集合，最简单的一种情况
					if((o1Size + o2Size)==2){
						similarity = StrEDMatcher.getNormEDSim((String)(o1HashSet.toArray()[0]), (String)(o2HashSet.toArray()[0]));
					}else{
						double sum1=0;
						for(String o1Element:o1HashSet){
							double sum2=0;
							for(String o2Element:o2HashSet){
								sum2 += StrEDMatcher.getNormEDSim(o1Element, o2Element);
							}
							sum1 += (sum2 / o2Size);
						}
						similarity = (sum1 / o1Size);							
					}
					if(similarity > Info.FILTER){
						simMatrix.setQuick(i, j, similarity);
					}
				}
				System.out.println("i is    "+i+"   computed similarity of"+o1ClassIds.get(i)+
						"  and  "+o2ClassIds.get(j-1)+" similarity is "+similarity);	
			}
			return simMatrix;
	 } 
	 
    /**
 	 * 两个本体之间ClassLabel的一个匹配，考虑sameAs块之间匹配的情况。
 	 * @param o1
 	 * @param o2
 	 * @return
 	 */
	public static SparseDoubleMatrix2D getClassLabelMatchingResult(OntModel o1,OntModel o2){
		
		ArrayList<String> o1ClassLabels = OWLOntParse2.getAllClassesLabelsSameAsInfo2(o1);
		ArrayList<String> o2ClassLabels = OWLOntParse2.getAllClassesLabelsSameAsInfo2(o2);
		//o1是竖着的
		int m = o1ClassLabels.size(); int n = o2ClassLabels.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(m,n);
		
		//分别遍历两个取并集，词典的维度一定要相同，才能保证cos()函数的正确性
		HashSet<String> classAllDictSet = new HashSet<String>();
		HashMap<String, Integer> classAllDictIDFInfo = new HashMap<String, Integer>();

		
		ArrayList<HashSet<String>> listOfSetsO1 = new ArrayList<HashSet<String>>();
		ArrayList<HashSet<String>> listOfSetsO2 = new ArrayList<HashSet<String>>();
		
		
		try {	
			//先遍历第一个本体
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();		
			for(int i=0; i<m;i++){
				List<String> token_list=test_Tokenizer.Tokenizer(o1ClassLabels.get(i));
				classAllDictSet.addAll(token_list);
				//对每个文档去重，保证IDF计算的正确.
				HashSet<String> set=new HashSet<String>(token_list);
				listOfSetsO1.add(set);
				for(String temp:set){
					if(classAllDictIDFInfo.containsKey(temp)){
						classAllDictIDFInfo.put(temp, classAllDictIDFInfo.get(temp) + 1);
					}else{
						classAllDictIDFInfo.put(temp,1);
					}
				}
			}
			
			//遍历另外一个本体
			for(int i=0;i<n;i++){
				List<String> token_list=test_Tokenizer.Tokenizer(o2ClassLabels.get(i));
				classAllDictSet.addAll(token_list);
				HashSet<String> set=new HashSet<String>(token_list);
				listOfSetsO2.add(set);
				for(String temp:set){
					if(classAllDictIDFInfo.containsKey(temp)){
						classAllDictIDFInfo.put(temp, classAllDictIDFInfo.get(temp) + 1);
					}else{
						classAllDictIDFInfo.put(temp,1);
					}
				}		
			}
			
			
			
			//得到词典中每个词在空间向量中的维度。
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(classAllDictSet);
			System.out.println(dictHashMap);
			//构造所有文本的向量空间,这里还是用汪老师建议的压缩数组来操作，但是缺点是压缩数组取的时候需要遍历
			int dictSize = classAllDictSet.size();
			
			SparseDoubleMatrix2D vecHouseO1=new SparseDoubleMatrix2D(m,dictSize);
			SparseDoubleMatrix2D vecHouseO2=new SparseDoubleMatrix2D(n,dictSize);
			
			
			for(int i=0;i<m;i++){
				List<String> token_list = test_Tokenizer.Tokenizer(o1ClassLabels.get(i));
				//这里传入的信息都要连在一起才可以
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), classAllDictIDFInfo, dictHashMap);
				//必须循环放入，缺点
				for(int j=0;j<dictSize;j++){
					if(vec[j]==0){
						//不存直接跳过使得矩阵变得稀疏
						continue;
					}
					vecHouseO1.setQuick(i,j,vec[j]);
				}	
			}
			
			
			for(int i=0;i<n;i++){
				List<String> token_list = test_Tokenizer.Tokenizer(o2ClassLabels.get(i));
				//这里传入的信息都要连在一起才可以
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), classAllDictIDFInfo, dictHashMap);
				//必须循环放入，缺点
				for(int j=0;j<dictSize;j++){
					if(vec[j]==0){
						//不存直接跳过使得矩阵变得稀疏
						continue;
					}
					vecHouseO2.setQuick(i,j,vec[j]);
				}	
			}	
			
			
			//在算相似度之前清空一下内存中后面其实用不到的数据。
			classAllDictSet.clear();
			classAllDictIDFInfo.clear();
		//	o1ClassLabels.clear();
		//	o2ClassLabels.clear();
		//	o1ClassLabels=null;
		//	o2ClassLabels=null;
			classAllDictSet=null;
			classAllDictIDFInfo=null;
			System.gc();
			
			
			HashSet<String> set1=null;
			HashSet<String> set2=null;
			double similarity=0.0 ; int i,j;
			for(i = 0;i < m ; i ++){
				set1=(HashSet<String>)listOfSetsO1.get(i).clone();
				for(j = 0;j < n; j ++){
					//顺序都是一一对应的	
					set2=(HashSet<String>) listOfSetsO2.get(j).clone();
					set2.retainAll(set1);
					//如果说两个句子之间没有公共的东西那么不用再计算了。肯定不可能相似。
					if(set2.size()==0){
						continue;
					}
					//针对汪老师的建议还是采用对压缩矩阵来计算向量
					similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouseO1,vecHouseO2,i,j,dictSize);
					
					if(similarity>Info.FILTER)
						simMatrix.setQuick(i,j,similarity);	
				}
				listOfSetsO1.get(i).clear();
				System.out.println("i is    "+i+"   computed similarity of"+o1ClassLabels.get(i)+
						" and "+ o2ClassLabels.get(j-1) + " similarity is "+similarity);
			}				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simMatrix;
	}		
	
}
