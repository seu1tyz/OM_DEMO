package matcher;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * �ı�ƥ��������ķ�װ
 * @author seu1tyz
 */
public class DocMatcher {
	
	public DocMatcher() {
		
	}	
	/**�ʼ�İ汾
	 * �����������������ƶ�
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	public static double computeVecSimilarity(double[] vec1, double[] vec2)
	{
		if(vec1.length==0||vec2.length==0)
			return 0.0;
		if(vec1.length!=vec2.length)
			return 0.0;
		
		double similarity = 0;
		double normOfVec1 = 0;
		double normOfVec2 = 0;
		int length = vec1.length;
		for(int i=0;i<length;i++)
		{
			similarity+=vec1[i]*vec2[i];
			normOfVec1+=Math.pow(vec1[i], 2);
			normOfVec2+=Math.pow(vec2[i], 2);
		}
		if(normOfVec1==0||normOfVec2==0)
			similarity = 0.0;
		else
			similarity = (similarity/(Math.sqrt(normOfVec1)*Math.sqrt(normOfVec2)));
		
		double temp = Double.valueOf(String.format("%.3f", similarity));
		return temp;	
	}
	
	/**
	 * ��������ʦ�Ľ�����дһ���ھ�������ֱ��ȡ���ݵķ���
	 * @param vecHouse
	 * @param i
	 * @param j
	 * @param n vecHouse�Ŀ��
	 * @return
	 */
	
	@SuppressWarnings("null")
	public static double computeSparseMatrixSimilarity(
					SparseDoubleMatrix2D vecHouse, int i, int j,int n) {
		double[] vec1=new double[n];
		double[] vec2=new double[n];
		//û�취ֻ��ѭ��ȡ
		for(int k=0;k<n;k++){
			vec1[k]=vecHouse.getQuick(i, k);
			vec2[k]=vecHouse.getQuick(j, k);
		}
		return computeVecSimilarity2(vec1,vec2);	
	}	
	
	
	/**  
	 * ��cos()������������ʦ�Ľ�����м���Ч������ĸ�д
	 * @author seu1tyz
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	
	public static double computeVecSimilarity2(double[] vec1, double[] vec2)
	{
		
		if(vec1.length==0 || vec2.length==0)
			return 0.0;
		
		if(vec1.length != vec2.length)
			return 0.0;
		
		double target = 0;
		double fengzi = 0;
		double sumVec1 = 0.0;
		double sumVec2 = 0.0;
		int length = vec1.length;
		
		for(int i=0;i<length;i++)
		{
			if(vec1[i]==0 || vec2[i]==0){
				//�����Ż��жϵ��߼�
				if(vec1[i]!=0){
					sumVec1 += vec1[i]*vec1[i];
				}
				if(vec2[i]!=0){
					sumVec2 += vec2[i]*vec2[i];
				}
			}else{
				fengzi  += vec1[i]*vec2[i];
				sumVec1 += vec1[i]*vec1[i];
				sumVec2 += vec2[i]*vec2[i];
			}	
		}
		
		if(sumVec1==0 || sumVec2==0)
			target = 0.0;
		else
			target = (fengzi/(Math.sqrt(sumVec1)*Math.sqrt(sumVec2)));
		
		return Double.valueOf(String.format("%.3f", target));
	}
	
	/**
	 * ���ı�ת�������ķ���
	 * @param comment ĳһ���ĵ�
	 * @param dictHashMap ��HashMap�洢�ʵ���ÿ���ʶ�Ӧ�������е�ά�ȡ�
	 * @param docNum �����ʾ�����ĵ��ĸ�����ʵ����ָ�ľ��Ǹ�����ԣ��ĸ�����
	 * @return vec
	 */
	//�����comment��û��ȥ�صģ�����TF��Ϣ������һ�����⣬comment���������ظ���Ϣ��
	public static double[] convert2VecUsingDictHashMap(List<String> comment,
				              int docNum,HashMap<String, Integer> dictNum
				              				,HashMap<String, Integer> dictHashMap) {   		
		int dictSize = dictHashMap.size();
		int commentSize = comment.size();
		double[] vec = new double[dictSize];
		int index = 0;
		HashSet<String> record = new HashSet<String>();
		for(String commentWord : comment){
			//comment���������ظ���Ϣ��
			if(!record.contains(commentWord)){
				int count = countTF(comment,commentWord);
				double TF = count/((double)commentSize);	
				int tempDocCount = dictNum.get(commentWord);
				double IDF =  Math.log((double)docNum/(tempDocCount +1));
				index = dictHashMap.get(commentWord);
				vec[index] = TF * IDF ;	
				//�����˼���ȥ
				record.add(commentWord);
			}		
		}		
		return vec;
	}

	
	
	
	/**
	 * ���ı�ת�������ķ���
	 * @param comment ĳһ���ĵ�
	 * @param dictList �ʵ�
	 * @param docNum �����ʾ�����ĵ��ĸ�����ʵ����ָ�ľ��Ǹ�����ԣ��ĸ�����
	 * @return vec
	 */
	public static double[] convert2Vec(List<String> comment,
				              HashSet<String> dictSet,int docNum,HashMap<String, Integer> dictNum) {   
		int dictSize=dictSet.size();
		int commentSize=comment.size();
		double[] vec=new double[dictSize];
		int i=0;
		for(String temp : dictSet){
			int count=countTF(comment,temp);
			double TF=count/((double)commentSize);	
			int tempDocCount = dictNum.get(temp);
			double IDF=  Math.log((double)docNum/(tempDocCount +1));
			vec[i++] = TF * IDF ;		
		}	
		return vec;
	}	
	/**
	 * ����TF��Ƶ�ķ���
	 * @param comment 
	 * @param temp
	 * @return TF
	 */
	private static int countTF(List<String> comment, String commentWord) {
		int count=0;
		for(int i=0;i<comment.size();i++){
			if(comment.get(i).equals(commentWord)){
				count++;
			}
		}
		return count;
	}
	
	public static double computeSparseMatrixSimilarity(
					SparseDoubleMatrix2D vecHouseO1, SparseDoubleMatrix2D vecHouseO2,
									int i, int j, int dictSize) {
		// TODO Auto-generated method stub
		double[] vec1=new double[dictSize];
		double[] vec2=new double[dictSize];
		//û�취ֻ��ѭ��ȡ
		
		for(int k=0;k<dictSize;k++){
			vec1[k]=vecHouseO1.getQuick(i, k);
			vec2[k]=vecHouseO2.getQuick(j, k);
		}
		
		return computeVecSimilarity2(vec1,vec2);	
		
	}
}
