package tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import matcher.DocMatcher;
import matcher.StrEDMatcher;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import com.hp.hpl.jena.ontology.OntModel;

public class ClassSimilarity2 {


    public static SparseDoubleMatrix2D getClassIdMatchingResult(OntModel o1, OntModel o2) {

        int i, j;
        String a = "";
        String b= "";
        ArrayList<HashSet<String>> o1ClassURIs = OWLOntParse2.getClassSameAsURIBlocks(o1);
        ArrayList<HashSet<String>> o2ClassURIs = OWLOntParse2.getClassSameAsURIBlocks(o2);
        int m = o1ClassURIs.size();
        int n = o2ClassURIs.size();

        SparseDoubleMatrix2D simMatrix = new SparseDoubleMatrix2D(m, n);

        double similarity = 0.0;
        HashSet<String> o1HashSet = null;
        HashSet<String> o2HashSet = null;

        for (i = 0; i < m; i++) {
            o1HashSet = o1ClassURIs.get(i);
            int o1Size = o1HashSet.size();
            if(o1Size == 0){
                String URI = (String)(o1ClassURIs.get(i).toArray()[0]);
                a = o1.getOntClass(URI).getLocalName();
            }
            for (j = 0; j < n; j++) {
                o2HashSet = o2ClassURIs.get(j);
                int o2Size = o2HashSet.size();
                // Prevent One Situation
                if (o1HashSet.equals(o2HashSet)) {
                    simMatrix.setQuick(i, j, 1.0);
                    continue;
                }
                // Normal Situation
                if ((o1Size + o2Size) == 2) {
                    String URI = (String)(o2ClassURIs.get(j).toArray()[0]);
                    b = o2.getOntClass(URI).getLocalName();
                    similarity = StrEDMatcher.getNormEDSim(a,b);
                } else {
                    double sum1 = 0;
                    for (String o1Element : o1HashSet) {
                        // URL to Name
                        o1Element = o1.getOntClass(o1Element).getLocalName();
                        double sum2 = 0;
                        for (String o2Element : o2HashSet) {
                            o2Element = o2.getOntClass(o2Element).getLocalName();
                            sum2 += StrEDMatcher.getNormEDSim(o1Element, o2Element);
                        }
                        sum1 += (sum2 / o2Size);
                    }
                    similarity = (sum1 / o1Size);
                }

                if (similarity > Info.FILTER) {
                    simMatrix.setQuick(i, j, similarity);
                }
            }
            System.out.println("i   " + i + "  is  computed successfully");
        }
        return simMatrix;
    }


    public static SparseDoubleMatrix2D getClassLabelMatchingResult(OntModel o1, OntModel o2) {

        ArrayList<HashSet<String>> o1ClassURIs = OWLOntParse2.getClassSameAsURIBlocks(o1);
        ArrayList<HashSet<String>> o2ClassURIs = OWLOntParse2.getClassSameAsURIBlocks(o2);
        //o1 is || state.
        int m = o1ClassURIs.size();
        int n = o2ClassURIs.size();
        SparseDoubleMatrix2D simMatrix = new SparseDoubleMatrix2D(m, n);

        //�ֱ��������ȡ�������ʵ��ά��һ��Ҫ��ͬ�����ܱ�֤cos()��������ȷ��
        HashSet<String> classAllDictSet = new HashSet<String>();
        HashMap<String, Integer> classAllDictIDFInfo = new HashMap<String, Integer>();


        ArrayList<HashSet<String>> listOfSetsO1 = new ArrayList<HashSet<String>>();
        ArrayList<HashSet<String>> listOfSetsO2 = new ArrayList<HashSet<String>>();


        try {
            // iterate the first ontology
            JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
            for (int i = 0; i < m; i++) {
                String label = "";
                HashSet<String> tempSet = o1ClassURIs.get(i);
                if(tempSet.size() == 1){
                    String URI = (String)tempSet.toArray()[0];
                    label = o1.getOntClass(URI).getLabel(null);
                    if(label == null || label == ""){
                        label = "";
                    }
                }else{
                    for(String subURL:tempSet){
                        OntClass target = o1.getOntClass(subURL);
                        String temp = target.getLabel(null);
                        if(temp == null || temp == ""){
                            temp = "";
                        }
                        label += temp;
                        label += "@";
                    }
                }

                List<String> token_list = test_Tokenizer.Tokenizer(label);
                classAllDictSet.addAll(token_list);
                //��ÿ���ĵ�ȥ�أ���֤IDF�������ȷ.
                HashSet<String> set = new HashSet<String>(token_list);
                listOfSetsO1.add(set);
                for (String temp : set) {
                    if (classAllDictIDFInfo.containsKey(temp)) {
                        classAllDictIDFInfo.put(temp, classAllDictIDFInfo.get(temp) + 1);
                    } else {
                        classAllDictIDFInfo.put(temp, 1);
                    }
                }
            }

            // iterate the second ontology,o1 and o2 feng qing chu
            for (int i = 0; i < n; i++) {
                String label = "";
                HashSet<String> tempSet = o2ClassURIs.get(i);
                if(tempSet.size() == 1){
                    String URI = (String)tempSet.toArray()[0];
                    label = o2.getOntClass(URI).getLabel(null);
                    if(label == null || label == ""){
                        label = "";
                    }
                }else{
                    for(String subURL:tempSet){
                        OntClass target = o2.getOntClass(subURL);
                        String temp = target.getLabel(null);
                        if(temp == null || temp == ""){
                            temp = "";
                        }
                        label += temp;
                        label += "@";
                    }
                }

                List<String> token_list = test_Tokenizer.Tokenizer(label);
                classAllDictSet.addAll(token_list);
                HashSet<String> set = new HashSet<String>(token_list);
                listOfSetsO2.add(set);
                for (String temp : set) {
                    if (classAllDictIDFInfo.containsKey(temp)) {
                        classAllDictIDFInfo.put(temp, classAllDictIDFInfo.get(temp) + 1);
                    } else {
                        classAllDictIDFInfo.put(temp, 1);
                    }
                }
            }


            //�õ��ʵ���ÿ�����ڿռ������е�ά�ȡ�
            HashMap<String, Integer> dictHashMap = Operator.dictToMap(classAllDictSet);
            System.out.println(dictHashMap);
            //���������ı��������ռ�,���ﻹ��������ʦ�����ѹ������������������ȱ����ѹ������ȡ��ʱ����Ҫ����
            int dictSize = classAllDictSet.size();

            SparseDoubleMatrix2D vecHouseO1 = new SparseDoubleMatrix2D(m, dictSize);
            SparseDoubleMatrix2D vecHouseO2 = new SparseDoubleMatrix2D(n, dictSize);


            for (int i = 0; i < m; i++) {

                String label = "";
                HashSet<String> tempSet = o1ClassURIs.get(i);
                if(tempSet.size() == 1){
                    String URI = (String)tempSet.toArray()[0];
                    label = o1.getOntClass(URI).getLabel(null);
                    if(label == null || label == ""){
                        label = "";
                    }
                }else{
                    for(String subURL:tempSet){
                        OntClass target = o1.getOntClass(subURL);
                        String temp = target.getLabel(null);
                        if(temp == null || temp == ""){
                            temp = "";
                        }
                        label += temp;
                        label += "@";
                    }
                }

                List<String> token_list = test_Tokenizer.Tokenizer(label);
                //���ﴫ�����Ϣ��Ҫ����һ��ſ���
                double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m + n), classAllDictIDFInfo, dictHashMap);
                //����ѭ�����룬ȱ��
                for (int j = 0; j < dictSize; j++) {
                    if (vec[j] == 0) {
                        //����ֱ������ʹ�þ�����ϡ��
                        continue;
                    }
                    vecHouseO1.setQuick(i, j, vec[j]);
                }
            }


            for (int i = 0; i < n; i++) {

                String label = "";
                HashSet<String> tempSet = o2ClassURIs.get(i);
                if(tempSet.size() == 1){
                    String URI = (String)tempSet.toArray()[0];
                    label = o2.getOntClass(URI).getLabel(null);
                    if(label == null || label == ""){
                        label = "";
                    }
                }else{
                    for(String subURL:tempSet){
                        OntClass target = o2.getOntClass(subURL);
                        String temp = target.getLabel(null);
                        if(temp == null || temp == ""){
                            temp = "";
                        }
                        label += temp;
                        label += "@";
                    }
                }

                List<String> token_list = test_Tokenizer.Tokenizer(label);
                //���ﴫ�����Ϣ��Ҫ����һ��ſ���
                double[] vec = DocMatcher.convert2VecUsingDictHashMap(token_list, (m + n), classAllDictIDFInfo, dictHashMap);
                //����ѭ�����룬ȱ��
                for (int j = 0; j < dictSize; j++) {
                    if (vec[j] == 0) {
                        //����ֱ������ʹ�þ�����ϡ��
                        continue;
                    }
                    vecHouseO2.setQuick(i, j, vec[j]);
                }
            }


            //�������ƶ�֮ǰ���һ���ڴ��к�����ʵ�ò��������ݡ�
            classAllDictSet.clear();
            classAllDictIDFInfo.clear();
            //	o1ClassLabels.clear();
            //	o2ClassLabels.clear();
            //	o1ClassLabels=null;
            //	o2ClassLabels=null;
            classAllDictSet = null;
            classAllDictIDFInfo = null;
            System.gc();


            HashSet<String> set1 = null;
            HashSet<String> set2 = null;
            double similarity = 0.0;
            int i, j;
            for (i = 0; i < m; i++) {
                set1 = (HashSet<String>) listOfSetsO1.get(i).clone();
                for (j = 0; j < n; j++) {
                    //˳����һһ��Ӧ��
                    set2 = (HashSet<String>) listOfSetsO2.get(j).clone();
                    set2.retainAll(set1);
                    //���˵��������֮��û�й����Ķ�����ô�����ټ����ˡ��϶����������ơ�
                    if (set2.size() == 0) {
                        continue;
                    }
                    //�������ʦ�Ľ��黹�ǲ��ö�ѹ����������������
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
