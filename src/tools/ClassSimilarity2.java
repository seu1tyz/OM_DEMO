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
 * ƥ�䱾���ڲ������������
 * @author seu1tyz
 */
public class ClassSimilarity2 {
	
	/**���������ClassId��һ��ƥ�䣬����sameAs�������
	 * @param o1    ����1
	 * @param o2    ����2
	 * @returntr
	 */
	 public static SparseDoubleMatrix2D getClassIdMatchingResult(OntModel o1,OntModel o2) {
		 
			int i, j;sfgsdfg
			ArrayList<HashSet<String>> o1ClassIds = OWLOntParse2.getAllClassesIDsSameAsInfo2(o1);
			ArrayList<HashSet<String>> o2ClassIds = OWLOntParse2.getAllClassesIDsSameAsInfo2(o2);
			
			int m = o1ClassIds.size();
			int n = o2ClassIds.size();
			//o1�����ŵģ�o2�Ǻ��ŵ�
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
					//��ֹ����ĳ�����
					if(o1HashSet.equals(o2HashSet)){
						simMatrix.setQuick(i, j, 1.0);
						continue;
					}			
					//˵������ֻ��һ��Ԫ�صļ��ϣ���򵥵�һ�����
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
 	 * ��������֮��ClassLabel��һ��ƥ�䣬����sameAs��֮��ƥ��������
 	 * @param o1
 	 * @param o2
 	 * @return
 	 */
	public static SparseDoubleMatrix2D getClassLabelMatchingResult(OntModel o1,OntModel o2){
		
		ArrayList<String> o1ClassLabels = OWLOntParse2.getAllClassesLabelsSameAsInfo2(o1);
		ArrayList<String> o2ClassLabels = OWLOntParse2.getAllClassesLabelsSameAsInfo2(o2);
		//o1�����ŵ�
		int m = o1ClassLabels.size(); int n = o2ClassLabels.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(m,n);
		
		//�ֱ��������ȡ�������ʵ��ά��һ��Ҫ��ͬ�����ܱ�֤cos()��������ȷ��
		HashSet<String> classAllDictSet = new HashSet<String>();
		HashMap<String, Integer> classAllDictIDFInfo = new HashMap<String, Integer>();

		
		ArrayList<HashSet<String>> listOfSetsO1 = new ArrayList<HashSet<String>>();
		ArrayList<HashSet<String>> listOfSetsO2 = new ArrayList<HashSet<String>>();
		
		
		try {	
			//�ȱ�����һ������
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();		
			for(int i=0; i<m;i++){
				List<String> token_list=test_Tokenizer.Tokenizer(o1ClassLabels.get(i));
				classAllDictSet.addAll(token_list);
				//��ÿ���ĵ�ȥ�أ���֤IDF�������ȷ.
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
			
			//��������һ������
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
			
			
			
			//�õ��ʵ���ÿ�����ڿռ������е�ά�ȡ�
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(classAllDictSet);
			System.out.println(dictHashMap);
			//���������ı��������ռ�,���ﻹ��������ʦ�����ѹ������������������ȱ����ѹ������ȡ��ʱ����Ҫ����
			int dictSize = classAllDictSet.size();
			
			SparseDoubleMatrix2D vecHouseO1=new SparseDoubleMatrix2D(m,dictSize);
			SparseDoubleMatrix2D vecHouseO2=new SparseDoubleMatrix2D(n,dictSize);
			
			
			for(int i=0;i<m;i++){
				List<String> token_list = test_Tokenizer.Tokenizer(o1ClassLabels.get(i));
				//���ﴫ�����Ϣ��Ҫ����һ��ſ���
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), classAllDictIDFInfo, dictHashMap);
				//����ѭ�����룬ȱ��
				for(int j=0;j<dictSize;j++){
					if(vec[j]==0){
						//����ֱ������ʹ�þ�����ϡ��
						continue;
					}
					vecHouseO1.setQuick(i,j,vec[j]);
				}	
			}
			
			
			for(int i=0;i<n;i++){
				List<String> token_list = test_Tokenizer.Tokenizer(o2ClassLabels.get(i));
				//���ﴫ�����Ϣ��Ҫ����һ��ſ���
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), classAllDictIDFInfo, dictHashMap);
				//����ѭ�����룬ȱ��
				for(int j=0;j<dictSize;j++){
					if(vec[j]==0){
						//����ֱ������ʹ�þ�����ϡ��
						continue;
					}
					vecHouseO2.setQuick(i,j,vec[j]);
				}	
			}	
			
			
			//�������ƶ�֮ǰ���һ���ڴ��к�����ʵ�ò��������ݡ�
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
					//˳����һһ��Ӧ��	
					set2=(HashSet<String>) listOfSetsO2.get(j).clone();
					set2.retainAll(set1);
					//���˵��������֮��û�й����Ķ�����ô�����ټ����ˡ��϶����������ơ�
					if(set2.size()==0){
						continue;
					}
					//�������ʦ�Ľ��黹�ǲ��ö�ѹ����������������
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
