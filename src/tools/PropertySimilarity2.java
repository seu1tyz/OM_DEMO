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
public class PropertySimilarity2 {
	/**
	 * 两个本体之间考虑sameAs的匹配，在ID里面不用在乎空的东西,ID肯定是有的。ID这里只要考虑是否为空就可以了.
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
		//o1是竖着的，o2是横着的
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
				//防止出现某种情况
				if(o1HashSet.equals(o2HashSet)){
					simMatrix.setQuick(i, j, 1.0);
					continue;
				}			
				//说明里面只有一个元素的集合，最简单的一种情况
				if((o1Size + o2Size)==2){
					String propertyId1 = (String)(o1HashSet.toArray()[0]);
					String propertyId2 = (String)(o2HashSet.toArray()[0]);
					splitPro1=propertyId1.split("__");
					splitPro2=propertyId2.split("__");
					//判断两个属性是否是同一个概念下的,如果是同一个概念那么不可能匹配，实际在28所是用两个下划线__作区分
					if(splitPro1[0].equals(splitPro2[0])){
						continue;
					}else{
						similarity=StrEDMatcher.getNormEDSim(splitPro1[1],splitPro2[1]);
					}
				//这里面sameAs组里面肯定是没有空白标记的(Id都是全的),所以不用进行条件的判断,然后是这里面不可能是有相同的概念名下面的。
				}else{
					double sum1=0;
					for(String o1Element:o1HashSet){
						double sum2 = 0.0;
						//_left是概念名
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
	 * 仅仅是比概念的标签匹配多出几个步骤而已。
	 * @param o1
	 * @param o2
	 * @return
	*/
	public static SparseDoubleMatrix2D getProptiesLabelMatchingResult(OntModel o1,OntModel o2){
		
		ArrayList<String> o1PropertyLabels = OWLOntParse2.getAllPropertyLabelsSameAsInfo2(o1);
		ArrayList<String> o2PropertyLabels = OWLOntParse2.getAllPropertyLabelsSameAsInfo2(o2);
		//o1是竖着的
		int m = o1PropertyLabels.size(); 
		int n = o2PropertyLabels.size();
		SparseDoubleMatrix2D simMatrix=new SparseDoubleMatrix2D(m,n);
	
		
		//分别遍历两个取并集，词典的维度一定要相同，才能保证cos()函数的正确性。词典的维度一定要相同。
		HashSet<String> propertyAllDictSet = new HashSet<String>();
		HashMap<String, Integer> propertyAllDictIDFInfo = new HashMap<String, Integer>();

		ArrayList<HashSet<String>> listOfSetsO1 = new ArrayList<HashSet<String>>();
		ArrayList<HashSet<String>> listOfSetsO2 = new ArrayList<HashSet<String>>();
		try{
			//先遍历第一个本体，前面的操作都是为了得到词典和全局IDF信息以及 存储的本体的各自的  listOfSets的信息。
			JiebaTokenizer test_Tokenizer= new JiebaTokenizer();
			
			for(int i=0; i<m;i++){
				
				List<String> token_list = null;
				String propertyLabel=o1PropertyLabels.get(i);
				//这里面不可能有Null信息
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
						//为了和后面的一一对应起来
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
			//要去遍历另外一个本体。
			for(int i=0;i<n;i++){
				List<String> token_list = null;
				String propertyLabel=o2PropertyLabels.get(i);
				//这里面不可能有Null信息
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
						//为了和后面的一一对应起来
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
			
			
			
			//得到词典中每个词在空间向量中的维度。
			HashMap<String,Integer> dictHashMap = Operator.dictToMap(propertyAllDictSet);
			System.out.println(dictHashMap);
			
			//构造所有文本的向量空间,这里还是用汪老师建议的压缩数组来操作，但是缺点是压缩数组取的时候需要遍历
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
				//这里传入的信息都要连在一起才可以
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), propertyAllDictIDFInfo, dictHashMap);
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
				//这里传入的信息都要连在一起才可以
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m+n), propertyAllDictIDFInfo, dictHashMap);
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
					//顺序都是一一对应的	
					set2=(HashSet<String>) listOfSetsO2.get(j).clone();
					set2.retainAll(set1);
					//如果说两个句子之间没有公共的东西那么不用再计算了。肯定不可能相似。就是0
					if(set2.size()==0){
						continue;
					}
					
//					String propertyLabel2=o2PropertyLabels.get(j);
					
					
//					splitLabel1=propertyLabel1.split("__");
//					splitLabel2=propertyLabel2.split("__");
//					
//					//这里的目的是由于28所内部属性的Label这里可能有几个是空的，所以要判断一下否则后面会出错，如果有一个是空，相似度就是0。
//					if(splitLabel1.length <2 || splitLabel2.length <2 ){
//						continue;
//					}
//					
//					//如果是同一个概念下的属性，因为转向量的时候是不知道是否是同一个概念的。
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