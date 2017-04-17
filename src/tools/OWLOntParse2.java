package tools;

import java.util.ArrayList;
import java.util.HashSet;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.impl.OntResourceImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/*********************
 * Class information
 * ��Lilyƥ��ϵͳ���ı�������Ĳ�����д
 * @author seu1tyz
 * @date 2016-10-04
 * describe:
 * �����������
 ********************/
public class OWLOntParse2 {
    /* ���Լ���ѧ����Ƶ��㷨�о�ȫ����SameAsʵ���sameAs����Ϣ*/

    public static ArrayList<HashSet<String>> getClassSameAsURIBlocks(OntModel m) {
        //��󷵻ؿ϶���һ���б�
        ArrayList<HashSet<String>> data = new ArrayList<HashSet<String>>();
        ArrayList<String> sameAsMapInfo = new ArrayList<String>();
        ArrayList<String> resultTemp = new ArrayList<String>();
        ExtendedIterator<OntClass> i = m.listClasses();
        //OntResource��OntClass��� �ž�������
        while (i.hasNext()) {
            OntClass ontClass = i.next();
            String url = ontClass.getURI();
            if (url != null && url != "") {
                resultTemp.add(ontClass.getURI());
                ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) ontClass.listSameAs();
                if (j.hasNext()) {
                    //�Ȱ�sameAs����Ϣ��¼����
                    while (j.hasNext()) {
                        OntResourceImpl sameAsClass = j.next();
                        // ����ķָ���������:��
                        sameAsMapInfo.add(url + "@" + sameAsClass.getURI());
                    }
                }
            }
        }
        System.out.println(sameAsMapInfo);
        //����������Ǹ�֮ǰ���б��sameAs��Info��Ϣ
        HashSet<String> remove = new HashSet<String>();
        for (String sub : sameAsMapInfo) {
            String[] subArray = sub.split("@");
            remove.add(subArray[0]);
            remove.add(subArray[1]);
        }
        resultTemp.removeAll(remove);

        //resultTemp �� ʣ�����Ķ���ֻ��һ��Ԫ�صĶ���
        for (String temp : resultTemp) {
            HashSet<String> a = new HashSet<String>();
            a.add(temp);
            data.add(a);
        }

        //����ٴ���sameAsInfo�������Ϣ
        while (sameAsMapInfo.size() != 0) {

            HashSet<String> tailSet = new HashSet<String>();
            //�ȼ�������ȥ
            String firstSameAs = sameAsMapInfo.get(0);
            String[] firstArray = firstSameAs.split("@");
            tailSet.add(firstArray[0]);
            tailSet.add(firstArray[1]);
            ArrayList<String> toRemove = new ArrayList<String>();
            for (String sub : sameAsMapInfo) {

                String[] subArray = sub.split("@");
                if (tailSet.contains(subArray[0]) || tailSet.contains(subArray[1])) {
                    tailSet.add(subArray[0]);
                    tailSet.add(subArray[1]);
                    toRemove.add(sub);
                }
            }
            sameAsMapInfo.removeAll(toRemove);
            data.add(tailSet);
        }
        return data;
    }

    public static ArrayList<HashSet<String>> getDPSameAsURIBlocks(OntModel m) {
        //��󷵻ؿ϶���һ���б�
        ArrayList<HashSet<String>> data = new ArrayList<HashSet<String>>();
        ArrayList<String> sameAsMapInfo = new ArrayList<String>();
        ArrayList<String> resultTemp = new ArrayList<String>();
        ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();
        //OntResource��OntClass��� �ž�������
        while (i.hasNext()) {
            DatatypeProperty dataProperty = i.next();
            String initial_url = dataProperty.getURI();
            if (initial_url != null && initial_url != "") {
                resultTemp.add(dataProperty.getURI());
                ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) dataProperty.listSameAs();
                if (j.hasNext()) {
                    //�Ȱ�sameAs����Ϣ��¼����
                    while (j.hasNext()) {
                        OntResourceImpl sameAsProperty = j.next();
                        sameAsMapInfo.add(initial_url + "@" + sameAsProperty.getURI());
                    }
                }

            }
        }
        System.out.println(sameAsMapInfo);
        //����������Ǹ�֮ǰ���б��sameAs��Info��Ϣ
        HashSet<String> remove = new HashSet<String>();
        for (String sub : sameAsMapInfo) {
            String[] subArray = sub.split("@");
            remove.add(subArray[0]);
            remove.add(subArray[1]);
        }
        resultTemp.removeAll(remove);

        //resultTemp �� ʣ�����Ķ���ֻ��һ��Ԫ�صĶ���
        for (String temp : resultTemp) {
            HashSet<String> a = new HashSet<String>();
            a.add(temp);
            data.add(a);
        }

        //����ٴ���sameAsInfo�������Ϣ
        while (sameAsMapInfo.size() != 0) {

            HashSet<String> tailSet = new HashSet<String>();
            //�ȼ�������ȥ
            String firstSameAs = sameAsMapInfo.get(0);
            String[] firstArray = firstSameAs.split("@");
            tailSet.add(firstArray[0]);
            tailSet.add(firstArray[1]);
            ArrayList<String> toRemove = new ArrayList<String>();
            for (String sub : sameAsMapInfo) {

                String[] subArray = sub.split("@");
                if (tailSet.contains(subArray[0]) || tailSet.contains(subArray[1])) {
                    tailSet.add(subArray[0]);
                    tailSet.add(subArray[1]);
                    toRemove.add(sub);
                }
            }
            sameAsMapInfo.removeAll(toRemove);
            data.add(tailSet);
        }
        return data;
    }

    public static ArrayList<HashSet<String>> getOPSameAsURIBlocks(OntModel m) {
        //��󷵻ؿ϶���һ���б�
        ArrayList<HashSet<String>> data = new ArrayList<HashSet<String>>();
        ArrayList<String> sameAsMapInfo = new ArrayList<String>();
        ArrayList<String> resultTemp = new ArrayList<String>();
        ExtendedIterator<ObjectProperty> i = m.listObjectProperties();
        //OntResource��OntClass��� �ž�������
        while (i.hasNext()) {
            ObjectProperty objectProperty = i.next();
            String initial_url = objectProperty.getURI();
            if (initial_url != null && initial_url != "") {
                resultTemp.add(objectProperty.getURI());
                ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) objectProperty.listSameAs();
                if (j.hasNext()) {
                    //�Ȱ�sameAs����Ϣ��¼����
                    while (j.hasNext()) {
                        OntResourceImpl sameAsProperty = j.next();
                        sameAsMapInfo.add(initial_url + "@" + sameAsProperty.getURI());
                    }
                }

            }
        }
        System.out.println(sameAsMapInfo);
        //����������Ǹ�֮ǰ���б��sameAs��Info��Ϣ
        HashSet<String> remove = new HashSet<String>();
        for (String sub : sameAsMapInfo) {
            String[] subArray = sub.split("@");
            remove.add(subArray[0]);
            remove.add(subArray[1]);
        }
        resultTemp.removeAll(remove);

        //resultTemp �� ʣ�����Ķ���ֻ��һ��Ԫ�صĶ���
        for (String temp : resultTemp) {
            HashSet<String> a = new HashSet<String>();
            a.add(temp);
            data.add(a);
        }

        //����ٴ���sameAsInfo�������Ϣ
        while (sameAsMapInfo.size() != 0) {

            HashSet<String> tailSet = new HashSet<String>();
            //�ȼ�������ȥ
            String firstSameAs = sameAsMapInfo.get(0);
            String[] firstArray = firstSameAs.split("@");
            tailSet.add(firstArray[0]);
            tailSet.add(firstArray[1]);
            ArrayList<String> toRemove = new ArrayList<String>();
            for (String sub : sameAsMapInfo) {

                String[] subArray = sub.split("@");
                if (tailSet.contains(subArray[0]) || tailSet.contains(subArray[1])) {
                    tailSet.add(subArray[0]);
                    tailSet.add(subArray[1]);
                    toRemove.add(sub);
                }
            }
            sameAsMapInfo.removeAll(toRemove);
            data.add(tailSet);
        }
        return data;
    }
}