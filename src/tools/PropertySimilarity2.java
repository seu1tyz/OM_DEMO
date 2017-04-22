package tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import matcher.DocMatcher;
import matcher.StrEDMatcher;
import tools.Info;
import tools.JiebaTokenizer;
import tools.OWLOntParse1;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import com.hp.hpl.jena.ontology.OntModel;

public class PropertySimilarity2 {


	public static double cal28sDPEntity(String dp28s1,String dp28s2){
		String a = dp28s1.split("__")[1].split("_")[0];
		String b = dp28s2.split("__")[1].split("_")[0];
		double similarity = StrEDMatcher.getNormEDSim(a,b);
		return similarity;
	}

	// ID Always exits.....
	public static SparseDoubleMatrix2D getDPIdMatchingResult(OntModel o1,OntModel o2){

		ArrayList<HashSet<String>> o1DPURIs = OWLOntParse2.getDPSameAsURIBlocks(o1);
		ArrayList<HashSet<String>> o2DPURIs = OWLOntParse2.getDPSameAsURIBlocks(o2);
		String a = "",b = "";	// DP format needs to be pay attention...
		int m = o1DPURIs.size();
		int n = o2DPURIs.size();
		int i,j;
		//o1是竖着的，o2是横着的
		SparseDoubleMatrix2D simMatrix = new SparseDoubleMatrix2D(m,n);

		double similarity = 0.0;
		HashSet<String> o1HashSet = null;
		HashSet<String> o2HashSet = null;

		String[] splitPro1 = null;
		String[] splitPro2 = null;

		for (i = 0; i < m; i++) {
			o1HashSet = o1DPURIs.get(i);
			int o1Size = o1HashSet.size();

			if(o1Size == 1){
				String URI = (String)(o1DPURIs.get(i).toArray()[0]);
				a = o1.getDatatypeProperty(URI).getLocalName();
			}

			for (j = 0; j < n; j++) {
				o2HashSet = o2DPURIs.get(j);
				int o2Size = o2HashSet.size();
				// Prevent One Situation
				if (o1HashSet.equals(o2HashSet)) {
					simMatrix.setQuick(i, j, 1.0);
					continue;
				}
				// Normal Situation
				if ((o1Size + o2Size) == 2) {
					String URI = (String)(o2DPURIs.get(j).toArray()[0]);
					b = o2.getDatatypeProperty(URI).getLocalName();
					similarity = cal28sDPEntity(a,b);// no necessary to judge is same Concept
				} else {// similarity of two set
					double sum1 = 0;
					for (String o1Element : o1HashSet) {
						// URL to Name
						o1Element = o1.getDatatypeProperty(o1Element).getLocalName();
						double sum2 = 0;
						for (String o2Element : o2HashSet) {
							o2Element = o2.getDatatypeProperty(o2Element).getLocalName();
							sum2 += cal28sDPEntity(o1Element,o2Element);
						}
						sum1 += (sum2 / o2Size);
					}
					similarity = (sum1 / o1Size);
				}

				if (similarity > Info.FILTER) {
					simMatrix.setQuick(i, j, similarity);
				}
			}
			System.out.println("i   " + i + "  is  computed successfully ");
		}

		return simMatrix;
	}

	/**
	 * 仅仅是比概念的标签匹配多出几个步骤而已。
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static SparseDoubleMatrix2D getDPLabelMatchingResult(OntModel o1,OntModel o2){

		ArrayList<HashSet<String>> o1DPURIs = OWLOntParse2.getDPSameAsURIBlocks(o1);
		ArrayList<HashSet<String>> o2DPURIs = OWLOntParse2.getDPSameAsURIBlocks(o2);
		//o1 is || state.
		int m = o1DPURIs.size();
		int n = o2DPURIs.size();
		SparseDoubleMatrix2D simMatrix = new SparseDoubleMatrix2D(m, n);// necessary

		// 分别遍历两个取并集，词典的维度一定要相同，才能保证cos()函数的正确性。词典的维度一定要相同。
		HashSet<String> propertyAllDictSet = new HashSet<String>();
		HashMap<String, Integer> propertyAllDictIDFInfo = new HashMap<String, Integer>();


		ArrayList<HashSet<String>> listOfSetsO1 = new ArrayList<HashSet<String>>();
		ArrayList<HashSet<String>> listOfSetsO2 = new ArrayList<HashSet<String>>();
		try {
			// 先遍历第一个本体，前面的操作都是为了得到词典和全局IDF信息以及 存储的本体的各自的listOfSets的信息。
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
			for (int i = 0; i < m; i++) {
				String label = "";
				HashSet<String> tempSet = o1DPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o1.getDatatypeProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else{
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						DatatypeProperty target = o1.getDatatypeProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else{
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}
				// System.out.println(label);
				List<String> token_list = test_Tokenizer.Tokenizer(label);// big label
				propertyAllDictSet.addAll(token_list);

				HashSet<String> set = new HashSet<String>(token_list);
				listOfSetsO1.add(set);

				for (String temp : set) {
					if (propertyAllDictIDFInfo.containsKey(temp)) {
						propertyAllDictIDFInfo.put(temp, propertyAllDictIDFInfo.get(temp) + 1);
					} else {
						propertyAllDictIDFInfo.put(temp, 1);
					}
				}
			}

			// iterate the second ontology,o1 and o2 feng qing chu
			for (int i = 0; i < n; i++) {
				String label = "";
				HashSet<String> tempSet = o2DPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o2.getDatatypeProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else{
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						DatatypeProperty target = o2.getDatatypeProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else{
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}

				List<String> token_list = test_Tokenizer.Tokenizer(label);
				propertyAllDictSet.addAll(token_list);
				HashSet<String> set = new HashSet<String>(token_list);
				listOfSetsO2.add(set);
				for (String temp : set) {
					if (propertyAllDictIDFInfo.containsKey(temp)) {
						propertyAllDictIDFInfo.put(temp, propertyAllDictIDFInfo.get(temp) + 1);
					} else {
						propertyAllDictIDFInfo.put(temp, 1);
					}
				}
			}

			// 得到词典中每个词在空间向量中的维度。
			HashMap<String, Integer> dictHashMap = Operator.dictToMap(propertyAllDictSet);
			System.out.println(dictHashMap);
			// 构造所有文本的向量空间,这里还是用汪老师建议的压缩数组来操作，但是缺点是压缩数组取的时候需要遍历
			int dictSize = propertyAllDictSet.size();

			SparseDoubleMatrix2D vecHouseO1 = new SparseDoubleMatrix2D(m, dictSize);
			SparseDoubleMatrix2D vecHouseO2 = new SparseDoubleMatrix2D(n, dictSize);


			for (int i = 0; i < m; i++) {
				String label = "";
				HashSet<String> tempSet = o1DPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o1.getDatatypeProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else{
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						DatatypeProperty target = o1.getDatatypeProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else{
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}

				List<String> token_list = test_Tokenizer.Tokenizer(label);

				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m + n), propertyAllDictIDFInfo, dictHashMap);

				for (int j = 0; j < dictSize; j++) {
					if (vec[j] == 0) {
						continue;
					}
					vecHouseO1.setQuick(i, j, vec[j]);
				}
			}


			for (int i = 0; i < n; i++) {
				String label = "";
				HashSet<String> tempSet = o2DPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o2.getDatatypeProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else {
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						DatatypeProperty target = o2.getDatatypeProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else {
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}

				List<String> token_list = test_Tokenizer.Tokenizer(label);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m + n), propertyAllDictIDFInfo, dictHashMap);
				for (int j = 0; j < dictSize; j++) {
					if (vec[j] == 0) {
						continue;
					}
					vecHouseO2.setQuick(i, j, vec[j]);
				}
			}



			propertyAllDictSet.clear();
			propertyAllDictIDFInfo.clear();
			//	o1ClassLabels.clear();
			//	o2ClassLabels.clear();
			//	o1ClassLabels=null;
			//	o2ClassLabels=null;
			propertyAllDictSet = null;
			propertyAllDictIDFInfo = null;
			System.gc();


			HashSet<String> set1 = null;
			HashSet<String> set2 = null;
			double similarity = 0.0;
			int i, j;
			for (i = 0; i < m; i++) {
				set1 = (HashSet<String>) listOfSetsO1.get(i).clone();
				for (j = 0; j < n; j++) {
					set2 = (HashSet<String>) listOfSetsO2.get(j).clone();
					set2.retainAll(set1);

					if (set2.size() == 0) {
						continue;
					}

					similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouseO1, vecHouseO2, i, j, dictSize);

					if (similarity > Info.FILTER)
						simMatrix.setQuick(i, j, similarity);
				}
				listOfSetsO1.get(i).clear();
				System.out.println("i is    " + i + "   computed similarity of successfully");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simMatrix;
	}

	public static SparseDoubleMatrix2D getOPIdMatchingResult(OntModel o1,OntModel o2){

		ArrayList<HashSet<String>> o1OPURIs = OWLOntParse2.getOPSameAsURIBlocks(o1);
		ArrayList<HashSet<String>> o2OPURIs = OWLOntParse2.getOPSameAsURIBlocks(o2);
		String a = "",b = "";	// DP format needs to be pay attention...
		int m = o1OPURIs.size();
		int n = o2OPURIs.size();
		int i,j;
		//o1是竖着的，o2是横着的
		SparseDoubleMatrix2D simMatrix = new SparseDoubleMatrix2D(m,n);

		double similarity = 0.0;
		HashSet<String> o1HashSet = null;
		HashSet<String> o2HashSet = null;

		String[] splitPro1 = null;
		String[] splitPro2 = null;

		for (i = 0; i < m; i++) {
			o1HashSet = o1OPURIs.get(i);
			int o1Size = o1HashSet.size();

			if(o1Size == 1){
				String URI = (String)(o1OPURIs.get(i).toArray()[0]);
				a = o1.getObjectProperty(URI).getLocalName();
			}

			for (j = 0; j < n; j++) {
				o2HashSet = o2OPURIs.get(j);
				int o2Size = o2HashSet.size();
				// Prevent One Situation
				if (o1HashSet.equals(o2HashSet)) {
					simMatrix.setQuick(i, j, 1.0);
					continue;
				}
				// Normal Situation
				if ((o1Size + o2Size) == 2) {
					String URI = (String)(o2OPURIs.get(j).toArray()[0]);
					b = o2.getObjectProperty(URI).getLocalName();
					similarity = cal28sDPEntity(a,b);// no necessary to judge is same Concept
				} else {// similarity of two set
					double sum1 = 0;
					for (String o1Element : o1HashSet) {
						// URL to Name
						o1Element = o1.getObjectProperty(o1Element).getLocalName();
						double sum2 = 0;
						for (String o2Element : o2HashSet) {
							o2Element = o2.getObjectProperty(o2Element).getLocalName();
							sum2 += cal28sDPEntity(o1Element,o2Element);
						}
						sum1 += (sum2 / o2Size);
					}
					similarity = (sum1 / o1Size);
				}

				if (similarity > Info.FILTER) {
					simMatrix.setQuick(i, j, similarity);
				}
			}
			System.out.println("i   " + i + "  is  computed successfully ");
		}

		return simMatrix;
	}

	public static SparseDoubleMatrix2D getOPLabelMatchingResult(OntModel o1,OntModel o2){

		ArrayList<HashSet<String>> o1OPURIs = OWLOntParse2.getOPSameAsURIBlocks(o1);
		ArrayList<HashSet<String>> o2OPURIs = OWLOntParse2.getOPSameAsURIBlocks(o2);
		//o1 is || state.
		int m = o1OPURIs.size();
		int n = o2OPURIs.size();
		SparseDoubleMatrix2D simMatrix = new SparseDoubleMatrix2D(m, n);// necessary

		// 分别遍历两个取并集，词典的维度一定要相同，才能保证cos()函数的正确性。词典的维度一定要相同。
		HashSet<String> propertyAllDictSet = new HashSet<String>();
		HashMap<String, Integer> propertyAllDictIDFInfo = new HashMap<String, Integer>();


		ArrayList<HashSet<String>> listOfSetsO1 = new ArrayList<HashSet<String>>();
		ArrayList<HashSet<String>> listOfSetsO2 = new ArrayList<HashSet<String>>();
		try {
			// 先遍历第一个本体，前面的操作都是为了得到词典和全局IDF信息以及 存储的本体的各自的listOfSets的信息。
			JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
			for (int i = 0; i < m; i++) {
				String label = "";
				HashSet<String> tempSet = o1OPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o1.getObjectProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else{
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						ObjectProperty target = o1.getObjectProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else{
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}
				// System.out.println(label);
				List<String> token_list = test_Tokenizer.Tokenizer(label);// big label
				propertyAllDictSet.addAll(token_list);

				HashSet<String> set = new HashSet<String>(token_list);
				listOfSetsO1.add(set);

				for (String temp : set) {
					if (propertyAllDictIDFInfo.containsKey(temp)) {
						propertyAllDictIDFInfo.put(temp, propertyAllDictIDFInfo.get(temp) + 1);
					} else {
						propertyAllDictIDFInfo.put(temp, 1);
					}
				}
			}

			// iterate the second ontology,o1 and o2 feng qing chu
			for (int i = 0; i < n; i++) {
				String label = "";
				HashSet<String> tempSet = o2OPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o2.getObjectProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else{
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						ObjectProperty target = o2.getObjectProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else{
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}

				List<String> token_list = test_Tokenizer.Tokenizer(label);
				propertyAllDictSet.addAll(token_list);
				HashSet<String> set = new HashSet<String>(token_list);
				listOfSetsO2.add(set);
				for (String temp : set) {
					if (propertyAllDictIDFInfo.containsKey(temp)) {
						propertyAllDictIDFInfo.put(temp, propertyAllDictIDFInfo.get(temp) + 1);
					} else {
						propertyAllDictIDFInfo.put(temp, 1);
					}
				}
			}

			// 得到词典中每个词在空间向量中的维度。
			HashMap<String, Integer> dictHashMap = Operator.dictToMap(propertyAllDictSet);
			System.out.println(dictHashMap);
			// 构造所有文本的向量空间,这里还是用汪老师建议的压缩数组来操作，但是缺点是压缩数组取的时候需要遍历
			int dictSize = propertyAllDictSet.size();

			SparseDoubleMatrix2D vecHouseO1 = new SparseDoubleMatrix2D(m, dictSize);
			SparseDoubleMatrix2D vecHouseO2 = new SparseDoubleMatrix2D(n, dictSize);


			for (int i = 0; i < m; i++) {
				String label = "";
				HashSet<String> tempSet = o1OPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o1.getObjectProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else{
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						ObjectProperty target = o1.getObjectProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else{
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}

				List<String> token_list = test_Tokenizer.Tokenizer(label);

				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m + n), propertyAllDictIDFInfo, dictHashMap);

				for (int j = 0; j < dictSize; j++) {
					if (vec[j] == 0) {
						continue;
					}
					vecHouseO1.setQuick(i, j, vec[j]);
				}
			}


			for (int i = 0; i < n; i++) {
				String label = "";
				HashSet<String> tempSet = o2OPURIs.get(i);
				if(tempSet.size() == 1){
					String URI = (String)tempSet.toArray()[0];
					label = o2.getObjectProperty(URI).getLabel(null);
					if(label == null || label == ""){
						label = "";
					}else {
						label = label.split("__")[1];
					}
				}else{
					for(String subURL:tempSet){
						ObjectProperty target = o2.getObjectProperty(subURL);
						String temp = target.getLabel(null);
						if(temp == null || temp == ""){
							temp = "";
						}else {
							temp = temp.split("__")[1];
						}
						label += temp;
						label += "@";
					}
				}

				List<String> token_list = test_Tokenizer.Tokenizer(label);
				double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m + n), propertyAllDictIDFInfo, dictHashMap);
				for (int j = 0; j < dictSize; j++) {
					if (vec[j] == 0) {
						continue;
					}
					vecHouseO2.setQuick(i, j, vec[j]);
				}
			}



			propertyAllDictSet.clear();
			propertyAllDictIDFInfo.clear();
			//	o1ClassLabels.clear();
			//	o2ClassLabels.clear();
			//	o1ClassLabels=null;
			//	o2ClassLabels=null;
			propertyAllDictSet = null;
			propertyAllDictIDFInfo = null;
			System.gc();


			HashSet<String> set1 = null;
			HashSet<String> set2 = null;
			double similarity = 0.0;
			int i, j;
			for (i = 0; i < m; i++) {
				set1 = (HashSet<String>) listOfSetsO1.get(i).clone();
				for (j = 0; j < n; j++) {
					set2 = (HashSet<String>) listOfSetsO2.get(j).clone();
					set2.retainAll(set1);

					if (set2.size() == 0) {
						continue;
					}

					similarity = DocMatcher.computeSparseMatrixSimilarity(vecHouseO1, vecHouseO2, i, j, dictSize);

					if (similarity > Info.FILTER)
						simMatrix.setQuick(i, j, similarity);
				}
				listOfSetsO1.get(i).clear();
				System.out.println("i is    " + i + "   computed similarity of successfully");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return simMatrix;
	}
}