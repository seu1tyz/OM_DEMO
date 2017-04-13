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
 * @date   2016-10-04
 * describe:
 * 解析本体的类
 ********************/
public class OWLOntParse2 {	
	
	/***********************
	 * 列举全部的SameAs ClassId sameAs对信息
	 ********************
	*/
	public static ArrayList<HashSet<String>> getAllClassesIDsSameAsInfo2(OntModel m) 
	{	
		//最后返回肯定是一个列表
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<OntClass> i = m.listClasses();
		//OntResource和OntClass差不多 嗯就是这样
		while (i.hasNext()) {
			OntClass ontClass = i.next();
			String initial_id = ontClass.getLocalName();
			if(initial_id != null && initial_id != ""){
				resultTemp.add(ontClass.getLocalName());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) ontClass.listSameAs();
				if(j.hasNext()){
					//先把sameAs的信息记录下来
					while(j.hasNext()){
						OntResourceImpl sameAsClass = j.next();
						sameAsMapInfo.add(initial_id+":"+sameAsClass.getLocalName());
					}	
				}
			}
        }
		System.out.println(sameAsMapInfo);
		//到这里出现那个之前的列表和sameAs的Info信息
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split(":");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp 的 剩下来的都是只有一个元素的东西
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//最后再处理sameAsInfo里面的信息
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//先加两个进去
			String firstSameAs = sameAsMapInfo.get(0);
			String[] firstArray = firstSameAs.split(":");
			tailSet.add(firstArray[0]) ; tailSet.add(firstArray[1]);
			ArrayList<String> toRemove = new ArrayList<String>();
			for (String sub : sameAsMapInfo) {
				
				String[] subArray = sub.split(":");
				if(tailSet.contains(subArray[0]) || tailSet.contains(subArray[1])){
					tailSet.add(subArray[0]) ; tailSet.add(subArray[1]);
					toRemove.add(sub);
				}
			}
			sameAsMapInfo.removeAll(toRemove);
			data.add(tailSet);	
			
		}
		
		return data;
	}	
	
	//这里的分隔符不能用:号
	public static ArrayList<HashSet<String>> getAllClassesURLSameAsInfo2(OntModel m) 
	{	
		//最后返回肯定是一个列表
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<OntClass> i = m.listClasses();
		//OntResource和OntClass差不多 嗯就是这样
		while (i.hasNext()) {
			OntClass ontClass = i.next();
			String url = ontClass.getURI();
			if(url != null && url != ""){
				resultTemp.add(ontClass.getURI());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) ontClass.listSameAs();
				if(j.hasNext()){
					//先把sameAs的信息记录下来
					while(j.hasNext()){
						OntResourceImpl sameAsClass = j.next();
						sameAsMapInfo.add(url+"@"+sameAsClass.getURI());
					}	
				}	
			}
        }
		System.out.println(sameAsMapInfo);
		//到这里出现那个之前的列表和sameAs的Info信息
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split("@");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp 的 剩下来的都是只有一个元素的东西
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//最后再处理sameAsInfo里面的信息
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//先加两个进去
			String firstSameAs = sameAsMapInfo.get(0);
			String[] firstArray = firstSameAs.split("@");
			tailSet.add(firstArray[0]) ; tailSet.add(firstArray[1]);
			ArrayList<String> toRemove = new ArrayList<String>();
			for (String sub : sameAsMapInfo) {
				
				String[] subArray = sub.split("@");
				if(tailSet.contains(subArray[0]) || tailSet.contains(subArray[1])){
					tailSet.add(subArray[0]) ; tailSet.add(subArray[1]);
					toRemove.add(sub);
				}
			}
			sameAsMapInfo.removeAll(toRemove);
			data.add(tailSet);				
		}	
		return data;
	}
	
	
	
	/**
	 * 重写得到ClassLabel标签对的算法，考虑28所本地有null的信息SameAs怎么做？ClassLabel基本上都是有的，表的描述。。
	 * @param m
	 * @return
	 */
	
	public static ArrayList<String> getAllClassesLabelsSameAsInfo2(OntModel m) 
	{	
		ArrayList<String> toReturn = new ArrayList<String>();
		ArrayList<HashSet<String>>  idURLResult = getAllClassesURLSameAsInfo2(m);
		for(HashSet<String> set:idURLResult){
			String url = "";
			if(set.size()==1){
				//toArray()方法
				url = (String)set.toArray()[0];
				OntClass target = m.getOntClass(url);
				String label = target.getLabel(null);
				//万一Class没有label。这里的ClassLabel应该都是有的，不可能没有ClassLabel.统一的没有Label的一个标准是什么？空字符串""
				if(label.contains("null") || label==null){
					label = "";
				}
				toReturn.add(label);
			}else{
				String integ = "";
				for(String subURL:set){
					OntClass target = m.getOntClass(subURL);
					String label = target.getLabel(null);
					//这里面可能有那种为空的sameAs的情况
					if(label.contains("null") || label==null){
						label = "";
					}
					integ += label;
					integ += "@";
				}
				toReturn.add(integ);
			}
		}	
		return toReturn;
	}	
	
	public static ArrayList<HashSet<String>> getAllPropertyIDsSameAsInfo2(OntModel m) 
	{	
		//最后返回肯定是一个列表
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();
		//OntResource和OntClass差不多 嗯就是这样
		while (i.hasNext()) {
			DatatypeProperty dataProperty=i.next();
			String initial_id = dataProperty.getLocalName();
			if(initial_id != null && initial_id != ""){
				resultTemp.add(dataProperty.getLocalName());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) dataProperty.listSameAs();
				if(j.hasNext()){
					//先把sameAs的信息记录下来
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_id+":"+sameAsProperty.getLocalName());
					}	
				}
				
			}
        }
		
		ExtendedIterator<ObjectProperty> k = m.listObjectProperties();
		//OntResource和OntClass差不多 嗯就是这样
		while (k.hasNext()) {
			ObjectProperty objectProperty = k.next();
			String initial_id = objectProperty.getLocalName();
			if(initial_id != null && initial_id != ""){
				resultTemp.add(objectProperty.getLocalName());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) objectProperty.listSameAs();
				if(j.hasNext()){
					//先把sameAs的信息记录下来
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_id+":"+sameAsProperty.getLocalName());
					}	
				}
			}
        }	
		
///		System.out.println(sameAsMapInfo);
		//到这里出现那个之前的列表和sameAs的Info信息
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split(":");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp 的 剩下来的都是只有一个元素的东西
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//最后再处理sameAsInfo里面的信息
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//先加两个进去
			String firstSameAs = sameAsMapInfo.get(0);
			String[] firstArray = firstSameAs.split(":");
			tailSet.add(firstArray[0]) ; tailSet.add(firstArray[1]);
			ArrayList<String> toRemove = new ArrayList<String>();
			for (String sub : sameAsMapInfo) {
				
				String[] subArray = sub.split(":");
				if(tailSet.contains(subArray[0]) || tailSet.contains(subArray[1])){
					tailSet.add(subArray[0]) ; tailSet.add(subArray[1]);
					toRemove.add(sub);
				}
			}
			sameAsMapInfo.removeAll(toRemove);
			data.add(tailSet);	
			
		}	
		return data;
	}	

	public static ArrayList<HashSet<String>> getAllPropertyURLsSameAsInfo2(OntModel m) 
	{	
		//最后返回肯定是一个列表
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();
		//OntResource和OntClass差不多 嗯就是这样
		while (i.hasNext()) {
			DatatypeProperty dataProperty=i.next();
			String initial_url = dataProperty.getURI();
			if(initial_url != null && initial_url != ""){
				resultTemp.add(dataProperty.getURI());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) dataProperty.listSameAs();
				if(j.hasNext()){
					//先把sameAs的信息记录下来
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_url+"@"+sameAsProperty.getURI());
					}	
				}
				
			}
        }
		
		ExtendedIterator<ObjectProperty> k = m.listObjectProperties();
		//OntResource和OntClass差不多 嗯就是这样
		while (k.hasNext()) {
			ObjectProperty objectProperty = k.next();
			String initial_url = objectProperty.getURI();
			if(initial_url != null && initial_url != ""){
				resultTemp.add(objectProperty.getURI());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) objectProperty.listSameAs();
				if(j.hasNext()){
					//先把sameAs的信息记录下来
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_url+"@"+sameAsProperty.getURI());
					}	
				}
			}
        }	
		
 		System.out.println(sameAsMapInfo);
		//到这里出现那个之前的列表和sameAs的Info信息
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split("@");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp 的 剩下来的都是只有一个元素的东西
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//最后再处理sameAsInfo里面的信息
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//先加两个进去
			String firstSameAs = sameAsMapInfo.get(0);
			String[] firstArray = firstSameAs.split("@");
			tailSet.add(firstArray[0]) ; tailSet.add(firstArray[1]);
			ArrayList<String> toRemove = new ArrayList<String>();
			for (String sub : sameAsMapInfo) {
				
				String[] subArray = sub.split("@");
				if(tailSet.contains(subArray[0]) || tailSet.contains(subArray[1])){
					tailSet.add(subArray[0]) ; tailSet.add(subArray[1]);
					toRemove.add(sub);
				}
			}
			sameAsMapInfo.removeAll(toRemove);
			data.add(tailSet);		
		}	
		return data;
	}
	
	public static ArrayList<String> getAllPropertyLabelsSameAsInfo2(OntModel m){
		
		ArrayList<String> toReturn = new ArrayList<String>();
		ArrayList<HashSet<String>>  idURLResult = getAllPropertyURLsSameAsInfo2(m);
		for(HashSet<String> set:idURLResult){
			String url = "";
			if(set.size()==1){
				//toArray()方法
				url = (String)set.toArray()[0];
				OntProperty target = m.getOntProperty(url);
				String label = target.getLabel(null);
				//万一Class没有label。这里的ClassLabel应该都是有的，不可能没有ClassLabel.统一的没有Label的一个标准是什么？空字符串""
				//这里要考虑__ 和 Tabel__的情况。
				if(label.contains("null") || label==null || 1>4){
					label = "";
				}
				toReturn.add(label);
			}else{
				String integ = "";
				for(String subURL:set){
					OntProperty target = m.getOntProperty(subURL);
					String label = target.getLabel(null);
					//这里面可能有那种为空的sameAs的情况
					if(label.contains("null") || label==null || 1>4){
						continue;
					}
					integ += label;
					integ += "@";
				}
				toReturn.add(integ.substring(0, integ.length()-1));
			}
		}	
		return toReturn;			
	}
	
	/**********************
	 * 汤叶舟对属性的解析的重写
	 * 列举全部的数据属性，不考虑baseURI和是否匿名
	 *********************/
	public static ArrayList<String> getAllPropertyLabels(OntModel m) 
	{
		ArrayList<String> result=new ArrayList<String>();
		ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();
 		//Output all the Properties
		while (i.hasNext()) {
			DatatypeProperty temp=i.next();
			String label_temp=temp.getLabel(null);
			result.add(label_temp);
        }	
		ExtendedIterator<ObjectProperty> j = m.listObjectProperties();
 		//Output all the Properties
		while (j.hasNext()) {
			String p_temp = (String) i.next().getLabel(null);
			result.add(p_temp);
        }
		return result;
	}

	
	public static ArrayList<String> getAllClassesURIs(OntModel m) {
		ArrayList<String> result=new ArrayList<String>();
		ExtendedIterator<OntClass> i = m.listClasses();
		
		while (i.hasNext()) {
			//getLocalName()好像是为了得到ID
			String c_temp =  i.next().getURI();
			if(c_temp != null && c_temp != "")
				result.add(c_temp);
        }
		return result;	
	}
	
}