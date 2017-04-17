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
 * 对Lily匹配系统做的本体解析的部分重写
 * @author seu1tyz
 * @date 2016-10-04
 * describe:
 * 解析本体的类
 ********************/
public class OWLOntParse2 {
    /* 用自己上学期设计的算法列举全部的SameAs实体的sameAs对信息*/

    public static ArrayList<HashSet<String>> getClassSameAsURIBlocks(OntModel m) {
        //最后返回肯定是一个列表
        ArrayList<HashSet<String>> data = new ArrayList<HashSet<String>>();
        ArrayList<String> sameAsMapInfo = new ArrayList<String>();
        ArrayList<String> resultTemp = new ArrayList<String>();
        ExtendedIterator<OntClass> i = m.listClasses();
        //OntResource和OntClass差不多 嗯就是这样
        while (i.hasNext()) {
            OntClass ontClass = i.next();
            String url = ontClass.getURI();
            if (url != null && url != "") {
                resultTemp.add(ontClass.getURI());
                ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) ontClass.listSameAs();
                if (j.hasNext()) {
                    //先把sameAs的信息记录下来
                    while (j.hasNext()) {
                        OntResourceImpl sameAsClass = j.next();
                        // 这里的分隔符不能用:号
                        sameAsMapInfo.add(url + "@" + sameAsClass.getURI());
                    }
                }
            }
        }
        System.out.println(sameAsMapInfo);
        //到这里出现那个之前的列表和sameAs的Info信息
        HashSet<String> remove = new HashSet<String>();
        for (String sub : sameAsMapInfo) {
            String[] subArray = sub.split("@");
            remove.add(subArray[0]);
            remove.add(subArray[1]);
        }
        resultTemp.removeAll(remove);

        //resultTemp 的 剩下来的都是只有一个元素的东西
        for (String temp : resultTemp) {
            HashSet<String> a = new HashSet<String>();
            a.add(temp);
            data.add(a);
        }

        //最后再处理sameAsInfo里面的信息
        while (sameAsMapInfo.size() != 0) {

            HashSet<String> tailSet = new HashSet<String>();
            //先加两个进去
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
        //最后返回肯定是一个列表
        ArrayList<HashSet<String>> data = new ArrayList<HashSet<String>>();
        ArrayList<String> sameAsMapInfo = new ArrayList<String>();
        ArrayList<String> resultTemp = new ArrayList<String>();
        ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();
        //OntResource和OntClass差不多 嗯就是这样
        while (i.hasNext()) {
            DatatypeProperty dataProperty = i.next();
            String initial_url = dataProperty.getURI();
            if (initial_url != null && initial_url != "") {
                resultTemp.add(dataProperty.getURI());
                ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) dataProperty.listSameAs();
                if (j.hasNext()) {
                    //先把sameAs的信息记录下来
                    while (j.hasNext()) {
                        OntResourceImpl sameAsProperty = j.next();
                        sameAsMapInfo.add(initial_url + "@" + sameAsProperty.getURI());
                    }
                }

            }
        }
        System.out.println(sameAsMapInfo);
        //到这里出现那个之前的列表和sameAs的Info信息
        HashSet<String> remove = new HashSet<String>();
        for (String sub : sameAsMapInfo) {
            String[] subArray = sub.split("@");
            remove.add(subArray[0]);
            remove.add(subArray[1]);
        }
        resultTemp.removeAll(remove);

        //resultTemp 的 剩下来的都是只有一个元素的东西
        for (String temp : resultTemp) {
            HashSet<String> a = new HashSet<String>();
            a.add(temp);
            data.add(a);
        }

        //最后再处理sameAsInfo里面的信息
        while (sameAsMapInfo.size() != 0) {

            HashSet<String> tailSet = new HashSet<String>();
            //先加两个进去
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
        //最后返回肯定是一个列表
        ArrayList<HashSet<String>> data = new ArrayList<HashSet<String>>();
        ArrayList<String> sameAsMapInfo = new ArrayList<String>();
        ArrayList<String> resultTemp = new ArrayList<String>();
        ExtendedIterator<ObjectProperty> i = m.listObjectProperties();
        //OntResource和OntClass差不多 嗯就是这样
        while (i.hasNext()) {
            ObjectProperty objectProperty = i.next();
            String initial_url = objectProperty.getURI();
            if (initial_url != null && initial_url != "") {
                resultTemp.add(objectProperty.getURI());
                ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) objectProperty.listSameAs();
                if (j.hasNext()) {
                    //先把sameAs的信息记录下来
                    while (j.hasNext()) {
                        OntResourceImpl sameAsProperty = j.next();
                        sameAsMapInfo.add(initial_url + "@" + sameAsProperty.getURI());
                    }
                }

            }
        }
        System.out.println(sameAsMapInfo);
        //到这里出现那个之前的列表和sameAs的Info信息
        HashSet<String> remove = new HashSet<String>();
        for (String sub : sameAsMapInfo) {
            String[] subArray = sub.split("@");
            remove.add(subArray[0]);
            remove.add(subArray[1]);
        }
        resultTemp.removeAll(remove);

        //resultTemp 的 剩下来的都是只有一个元素的东西
        for (String temp : resultTemp) {
            HashSet<String> a = new HashSet<String>();
            a.add(temp);
            data.add(a);
        }

        //最后再处理sameAsInfo里面的信息
        while (sameAsMapInfo.size() != 0) {

            HashSet<String> tailSet = new HashSet<String>();
            //先加两个进去
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