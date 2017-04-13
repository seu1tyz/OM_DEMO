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
 * @date   2016-10-04
 * describe:
 * �����������
 ********************/
public class OWLOntParse2 {	
	
	/***********************
	 * �о�ȫ����SameAs ClassId sameAs����Ϣ
	 ********************
	*/
	public static ArrayList<HashSet<String>> getAllClassesIDsSameAsInfo2(OntModel m) 
	{	
		//��󷵻ؿ϶���һ���б�
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<OntClass> i = m.listClasses();
		//OntResource��OntClass��� �ž�������
		while (i.hasNext()) {
			OntClass ontClass = i.next();
			String initial_id = ontClass.getLocalName();
			if(initial_id != null && initial_id != ""){
				resultTemp.add(ontClass.getLocalName());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) ontClass.listSameAs();
				if(j.hasNext()){
					//�Ȱ�sameAs����Ϣ��¼����
					while(j.hasNext()){
						OntResourceImpl sameAsClass = j.next();
						sameAsMapInfo.add(initial_id+":"+sameAsClass.getLocalName());
					}	
				}
			}
        }
		System.out.println(sameAsMapInfo);
		//����������Ǹ�֮ǰ���б��sameAs��Info��Ϣ
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split(":");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp �� ʣ�����Ķ���ֻ��һ��Ԫ�صĶ���
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//����ٴ���sameAsInfo�������Ϣ
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//�ȼ�������ȥ
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
	
	//����ķָ���������:��
	public static ArrayList<HashSet<String>> getAllClassesURLSameAsInfo2(OntModel m) 
	{	
		//��󷵻ؿ϶���һ���б�
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<OntClass> i = m.listClasses();
		//OntResource��OntClass��� �ž�������
		while (i.hasNext()) {
			OntClass ontClass = i.next();
			String url = ontClass.getURI();
			if(url != null && url != ""){
				resultTemp.add(ontClass.getURI());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) ontClass.listSameAs();
				if(j.hasNext()){
					//�Ȱ�sameAs����Ϣ��¼����
					while(j.hasNext()){
						OntResourceImpl sameAsClass = j.next();
						sameAsMapInfo.add(url+"@"+sameAsClass.getURI());
					}	
				}	
			}
        }
		System.out.println(sameAsMapInfo);
		//����������Ǹ�֮ǰ���б��sameAs��Info��Ϣ
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split("@");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp �� ʣ�����Ķ���ֻ��һ��Ԫ�صĶ���
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//����ٴ���sameAsInfo�������Ϣ
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//�ȼ�������ȥ
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
	 * ��д�õ�ClassLabel��ǩ�Ե��㷨������28��������null����ϢSameAs��ô����ClassLabel�����϶����еģ������������
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
				//toArray()����
				url = (String)set.toArray()[0];
				OntClass target = m.getOntClass(url);
				String label = target.getLabel(null);
				//��һClassû��label�������ClassLabelӦ�ö����еģ�������û��ClassLabel.ͳһ��û��Label��һ����׼��ʲô�����ַ���""
				if(label.contains("null") || label==null){
					label = "";
				}
				toReturn.add(label);
			}else{
				String integ = "";
				for(String subURL:set){
					OntClass target = m.getOntClass(subURL);
					String label = target.getLabel(null);
					//���������������Ϊ�յ�sameAs�����
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
		//��󷵻ؿ϶���һ���б�
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();
		//OntResource��OntClass��� �ž�������
		while (i.hasNext()) {
			DatatypeProperty dataProperty=i.next();
			String initial_id = dataProperty.getLocalName();
			if(initial_id != null && initial_id != ""){
				resultTemp.add(dataProperty.getLocalName());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) dataProperty.listSameAs();
				if(j.hasNext()){
					//�Ȱ�sameAs����Ϣ��¼����
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_id+":"+sameAsProperty.getLocalName());
					}	
				}
				
			}
        }
		
		ExtendedIterator<ObjectProperty> k = m.listObjectProperties();
		//OntResource��OntClass��� �ž�������
		while (k.hasNext()) {
			ObjectProperty objectProperty = k.next();
			String initial_id = objectProperty.getLocalName();
			if(initial_id != null && initial_id != ""){
				resultTemp.add(objectProperty.getLocalName());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) objectProperty.listSameAs();
				if(j.hasNext()){
					//�Ȱ�sameAs����Ϣ��¼����
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_id+":"+sameAsProperty.getLocalName());
					}	
				}
			}
        }	
		
///		System.out.println(sameAsMapInfo);
		//����������Ǹ�֮ǰ���б��sameAs��Info��Ϣ
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split(":");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp �� ʣ�����Ķ���ֻ��һ��Ԫ�صĶ���
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//����ٴ���sameAsInfo�������Ϣ
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//�ȼ�������ȥ
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
		//��󷵻ؿ϶���һ���б�
		ArrayList<HashSet<String>> data=new ArrayList<HashSet<String>>();
		ArrayList<String> sameAsMapInfo = new ArrayList<String>();
		ArrayList<String> resultTemp = new ArrayList<String>();
		ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();
		//OntResource��OntClass��� �ž�������
		while (i.hasNext()) {
			DatatypeProperty dataProperty=i.next();
			String initial_url = dataProperty.getURI();
			if(initial_url != null && initial_url != ""){
				resultTemp.add(dataProperty.getURI());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) dataProperty.listSameAs();
				if(j.hasNext()){
					//�Ȱ�sameAs����Ϣ��¼����
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_url+"@"+sameAsProperty.getURI());
					}	
				}
				
			}
        }
		
		ExtendedIterator<ObjectProperty> k = m.listObjectProperties();
		//OntResource��OntClass��� �ž�������
		while (k.hasNext()) {
			ObjectProperty objectProperty = k.next();
			String initial_url = objectProperty.getURI();
			if(initial_url != null && initial_url != ""){
				resultTemp.add(objectProperty.getURI());
				ExtendedIterator<OntResourceImpl> j = (ExtendedIterator<OntResourceImpl>) objectProperty.listSameAs();
				if(j.hasNext()){
					//�Ȱ�sameAs����Ϣ��¼����
					while(j.hasNext()){
						OntResourceImpl sameAsProperty = j.next();
						sameAsMapInfo.add(initial_url+"@"+sameAsProperty.getURI());
					}	
				}
			}
        }	
		
 		System.out.println(sameAsMapInfo);
		//����������Ǹ�֮ǰ���б��sameAs��Info��Ϣ
		HashSet<String> remove = new HashSet<String>();
		for(String sub:sameAsMapInfo){
			String[] subArray = sub.split("@");
			remove.add(subArray[0]);
			remove.add(subArray[1]);
		}
		resultTemp.removeAll(remove);
		
		//resultTemp �� ʣ�����Ķ���ֻ��һ��Ԫ�صĶ���
		for(String temp:resultTemp){
			HashSet<String> a = new HashSet<String>();
			a.add(temp);
			data.add(a);
		}
		
		//����ٴ���sameAsInfo�������Ϣ
		while(sameAsMapInfo.size()!=0){
			
			HashSet<String> tailSet =new HashSet<String>();
			//�ȼ�������ȥ
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
				//toArray()����
				url = (String)set.toArray()[0];
				OntProperty target = m.getOntProperty(url);
				String label = target.getLabel(null);
				//��һClassû��label�������ClassLabelӦ�ö����еģ�������û��ClassLabel.ͳһ��û��Label��һ����׼��ʲô�����ַ���""
				//����Ҫ����__ �� Tabel__�������
				if(label.contains("null") || label==null || 1>4){
					label = "";
				}
				toReturn.add(label);
			}else{
				String integ = "";
				for(String subURL:set){
					OntProperty target = m.getOntProperty(subURL);
					String label = target.getLabel(null);
					//���������������Ϊ�յ�sameAs�����
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
	 * ��Ҷ�۶����ԵĽ�������д
	 * �о�ȫ�����������ԣ�������baseURI���Ƿ�����
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
			//getLocalName()������Ϊ�˵õ�ID
			String c_temp =  i.next().getURI();
			if(c_temp != null && c_temp != "")
				result.add(c_temp);
        }
		return result;	
	}
	
}