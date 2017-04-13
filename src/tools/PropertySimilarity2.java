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
public class PropertySimilarity2 {
	/**
	 * ��������֮�俼��sameAs��ƥ�䣬��ID���治���ں��յĶ���,ID�϶����еġ�ID����ֻҪ�����Ƿ�Ϊ�վͿ�����.
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static SparseDoubleMatrix2D getProptiesIdMatchingResult(OntModel o1,OntModel o2){
		
		ArrayList<HashSet<String>> o1Properties = OWLOntParse2.getAllPropertyIDsSameAsInfo2(o1);
		ArrayList<HashSet<String>> o2Properties = OWLOntParse2.getAllPropertyIDsSameAsInfo2(o2);
		int m = o1Properties.size();
		int n = o2Properties.size();
		int i,j;fsadfasdf
		//o1�����ŵģ�o2�Ǻ��ŵ�
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(m,n);
		
		double similarity = 0.0;
		HashSet<String> o1HashSet = null;
		HashSet<String> o2HashSet = null;
		String[] splitPro1=null;
		String[] splitPro2=null;
		for (i = 0; i < m; i++) {
			o1HashSet = o1Properties.get(i);
			int o1Size = o1HashSet.size();
			for (j = 0; j < n; j++) {	
				o2HashSet = o2Properties.get(j);
				int o2Size = o2HashSet.size();
				//��ֹ����ĳ�����
				if(o1HashSet.equals(o2HashSet)){
					simMatrix.setQuick(i, j, 1.0);
					continue;
				}			
				//˵������ֻ��һ��Ԫ�صļ��ϣ���򵥵�һ�����
				if((o1Size + o2Size)==2){
					String propertyId1 = (String)(o1HashSet.toArray()[0]);
					String propertyId2 = (String)(o2HashSet.toArray()[0]);
					splitPro1=propertyId1.split("__");
					splitPro2=propertyId2.split("__");
					//�ж����������Ƿ���ͬһ�������µ�,�����ͬһ��������ô������ƥ�䣬ʵ����28�����������»���__������
					if(splitPro1[0].equals(splitPro2[0])){
						continue;
					}else{
						similarity=StrEDMatcher.getNormEDSim(splitPro1[1],splitPro2[1]);
					}
				//������sameAs������϶���û�пհױ�ǵ�(Id����ȫ��),���Բ��ý����������ж�,Ȼ���������治����������ͬ�ĸ���������ġ�
				}else{
					double sum1=0;
					for(String o1Element:o1HashSet){
						double sum2 = 0.0;
						//_left�Ǹ�����
						String _left = o1Element.split("__")[0];	
						String left = o1Element.split("__")[1];	
						for(String o2Element:o2HashSet){
							String _right = o2Element.split("__")[0];
							String right = o2Element.split("__")[1];
							if(_left.equals(_right)){
								continue;
							}
							sum2 += StrEDMatcher.getNormEDSim(left , right);
						}
						sum1 += (sum2 / o2Size);
					}
					similarity = (sum1 / o1Size);							
				}
				
				if(similarity > Info.FILTER){
					simMatrix.setQuick(i, j, similarity);
				}
			}
			System.out.println("i is    "+i+"   computed similarity of"+o1Properties.get(i)+
					"  and  "+o2Properties.get(j-1)+" similarity is "+similarity);	
		}
		return simMatrix;
	}
	
	/**
	 * �����Ǳȸ���ı�ǩƥ��������������ѡ�
	 * @param o1
	 * @param o2
	 * @return
	*/
	public static SparseDoubleMatrix2D getProptiesLabelMatchingResult(OntModel o1,OntModel o2){
		
		ArrayList<String> o1PropertyLabels = OWLOntParse2.getAllPropertyLabelsSameAsInfo2(o1);
		ArrayList<String> o2PropertyLabels = OWLOntParse2.getAllPropertyLabelsSameAsInfo2(o2);
		//o1�����ŵ�
		int m = o1PropertyLabels.size(); 
		int n = o2PropertyLabels.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(m,n);
	
		
		//�ֱ��������ȡ�������ʵ��ά��һ��Ҫ��ͬ�����ܱ�֤cos()��������ȷ�ԡ��ʵ��ά��һ��Ҫ��ͬ��
		HashSet<String> propertyAllDictSet = new HashSet<String>();
		HashMap<String, Integer> propertyAllDictIDFInfo = new HashMap<String, Integer>();

		ArrayList<HashSet<String>> listOfSetsO1 = new ArrayList<HashSet<String>>();
		ArrayList<HashSet<String>> listOfSetsO2 = new ArrayList<HashSet<String>>();
		try{
			//�ȱ�����һ�����壬ǰ��Ĳ�������Ϊ�˵õ��ʵ��ȫ��IDF��Ϣ�Լ� �洢�ı���ĸ��Ե�  listOfSets����Ϣ��
			JiebaTokenizer test_Tokenizer= new JiebaTokenizer();
			
			for(int i=0; i<m;i++){
				
				List<String> token_list = null;
				String propertyLabel=o1PropertyLabels.get(i);
				//�����治������Null��Ϣ
				if(propertyLabel.contains("@")){
					String change="";
					String[] arrays=propertyLabel.split("@");
					for(int k=0;k<arrays.length;k++){		
						change += (arrays[k].split("__")[1]);
						change += "@";
					}
					token_list = test_Tokenizer.Tokenizer(change);
				}else{
					String[] splitLabel=propertyLabel.split("__");
					if(splitLabel.length==0 || splitLabel.length==1){
						//Ϊ�˺ͺ����һһ��Ӧ����
						listOfSetsO1.add(new HashSet<String>());
						continue;
					}
					token_list = test_Tokenizer.Tokenizer(splitLabel[1]);
				}
				
				propertyAllDictSet.addAll(token_list);
				HashSet<String> set=new HashSet<String>(token_list);
				listOfSetsO1.add(set);
				for(String temp:set){
					if(propertyAllDictIDFInfo.containsKey(temp)){
						propertyAllDictIDFInfo.put(temp, propertyAllDictIDFInfo.get(temp) + 1);
					}else{
						propertyAllDictIDFInfo.put(temp,1);
					}
				}	

			}		
			//Ҫȥ��������һ�����塣
			for(int i=0;i<n;i++){
				List<String> token_list = null;
				String propertyLabel=o2PropertyLabels.get(i);
				//�����治������Null��Ϣ
				if(propertyLabel.contains("@")){
					String change="";
					String[] arrays=propertyLabel.split("@");
					for(int k=0;k<arrays.length;k++){		
						change += (arrays[k].split("__")[1]);
						change += "@";
					}
					token_list = test_Tokenizer.Tokenizer(change);
	
				}else{
					String[] splitLabel=propertyLabel.split("__");
					if(splitLabel.length==0 || splitLabel.length==1){
						//Ϊ�˺ͺ����һһ��Ӧ����
						listOfSetsO2.add(new HashSet<String>());
						continue;
					}
					token_list = test_Tokenizer.Tokenizer(splitLabel[1]);
				}
			
				propertyAllDictSet.addAll(token_list);
				HashSet<String> set=new HashSet<String>(token_list);
				listOfSetsO2.add(set);
				for(String temp:set){
					if(propertyAllDictIDFInfo.containsKey(temp)){
						propertyAllDictIDFInfo.put(temp, propertyAllDictIDFInfo.get(temp) + 1);
					}else{
						propertyAllDictIDFInfo.put(temp,1);
					}
				}
			}
			
			
			
			//�õ��ʵ���ÿ�����ڿռ������е�ά�ȡ�
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(propertyAllDictSet);
			System.out.println(dictHashMap);
			
			//���������ı��������ռ�,���ﻹ��������ʦ�����ѹ������������������ȱ����ѹ������ȡ��ʱ����Ҫ����
			int dictSize = propertyAllDictSet.size();
			
			SparseDoubleMatrix2D vecHouseO1=new SparseDoubleMatrix2D(m,dictSize);
			SparseDoubleMatrix2D vecHouseO2=new SparseDoubleMatrix2D(n,dictSize);
			
			
			for(int i=0;i<m;i++){
				List<String> token_list = null;
				String propertyLabel=o1PropertyLabels.get(i);
				if(propertyLabel.contains("@")){
					
					String change="";
					String[] arrays=propertyLabel.split("@");
					for(int k=0;k<arrays.length;k++){		
						change += (arrays[k].split("__")[1]);
						change += "@";
					}
					token_list=test_Tokenizer.Tokenizer(change);			
				}else{
					String[] splitLabel=propertyLabel.split("__");
					if(splitLabel.length==0 || splitLabel.length==1){
						continue;
					}
					token_list=test_Tokenizer.Tokenizer(splitLabel[1]);	
				}
				//���ﴫ�����Ϣ��Ҫ����һ��ſ���
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), propertyAllDictIDFInfo, dictHashMap);
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
				List<String> token_list = null;
				String propertyLabel=o2PropertyLabels.get(i);
				if(propertyLabel.contains("@")){
					
					String change="";
					String[] arrays=propertyLabel.split("@");
					for(int k=0;k<arrays.length;k++){		
						change += (arrays[k].split("__")[1]);
						change += "@";
					}
					token_list = test_Tokenizer.Tokenizer(change);			
				}else{
					String[] splitLabel=propertyLabel.split("__");
					if(splitLabel.length==0 || splitLabel.length==1){
						continue;
					}
					token_list = test_Tokenizer.Tokenizer(splitLabel[1]);	
				}
				//���ﴫ�����Ϣ��Ҫ����һ��ſ���
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), propertyAllDictIDFInfo, dictHashMap);
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
			propertyAllDictSet.clear();
			propertyAllDictIDFInfo.clear();
		//	o1ProLabels.clear();
		//	o2ProLabels.clear();
		//	o1ClassLabels=null;
		//	o2ClassLabels=null;
			propertyAllDictSet=null;
			propertyAllDictIDFInfo=null;
			System.gc();
			
			
			
			
			HashSet<String> set1=null;
			HashSet<String> set2=null;
//			String[] splitLabel1=null;
//			String[] splitLabel2=null;
			double similarity=0.0 ; 
			int i,j;
			for(i = 0;i < m ; i ++){
//				String propertyLabel1=o1PropertyLabels.get(i);
				set1=(HashSet<String>)listOfSetsO1.get(i).clone();
				for(j = 0;j < n; j ++){
					//˳����һһ��Ӧ��	
					set2=(HashSet<String>) listOfSetsO2.get(j).clone();
					set2.retainAll(set1);
					//���˵��������֮��û�й����Ķ�����ô�����ټ����ˡ��϶����������ơ�����0
					if(set2.size()==0){
						continue;
					}
					
//					String propertyLabel2=o2PropertyLabels.get(j);
					
					
//					splitLabel1=propertyLabel1.split("__");
//					splitLabel2=propertyLabel2.split("__");
//					
//					//�����Ŀ��������28���ڲ����Ե�Label��������м����ǿյģ�����Ҫ�ж�һ�·���������������һ���ǿգ����ƶȾ���0��
//					if(splitLabel1.length <2 || splitLabel2.length <2 ){
//						continue;
//					}
//					
//					//�����ͬһ�������µ����ԣ���Ϊת������ʱ���ǲ�֪���Ƿ���ͬһ������ġ�
//					if(splitLabel1[0].equals(splitLabel2[0])){
//						continue;
//					}else{
//						similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouseO1,vecHouseO2,i,j,dictSize);
//						if(similarity>Info.FILTER)
//							simMatrix.setQuick(i,j,similarity);	
//				}
					
					similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouseO1,vecHouseO2,i,j,dictSize);
					if(similarity>Info.FILTER)
						simMatrix.setQuick(i,j,similarity);	
					
			}
				
				listOfSetsO1.get(i).clear();
				System.out.println("i is    "+i+"   computed similarity of  "+ o1PropertyLabels.get(i) +
						" and "+ o1PropertyLabels.get(j-1) +" similarity is "+similarity);
//				if(splitLabel1.length !=0  && splitLabel2.length!=0){
//					System.out.println("i is    "+i+"   computed similarity of"+ splitLabel1[1]+
//							" and "+ splitLabel2[1] +" similarity is "+similarity);
//				}
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simMatrix;
		
	}					
	
	
	
}