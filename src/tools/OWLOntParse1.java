package tools;
import java.util.ArrayList;
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
public class OWLOntParse1 {	
	
	
	public static ArrayList<String> getAllClassesURIs(OntModel m) {
		
		ArrayList<String> result=new ArrayList<String>();
		ExtendedIterator<OntClass> i = m.listClasses();
		
		while (i.hasNext()) {
			//getLocalName()好像是为了得到 Id而已
			String c_temp =  i.next().getURI();
			if(c_temp != null && c_temp != "")
				result.add(c_temp);
        }
		return result;	
	}
	
	
//	public static ArrayList<String> getAllClassesIDs(OntModel m) 
//	{	
//		ArrayList<String> result = new ArrayList<String>();
//		ArrayList<String> uris = getAllClassesURIs(m);
//		for (String uri : uris) {
//			OntClass ontClass = m.getOntClass(uri);
//			String id = ontClass.getLocalName();
//			if(id != null && id != ""){
//				result.add(id);
//			}else{
//				//这里概念的ID和Label都不可能是空尤其是ID
//				result.add("null");
//			}
//		}
//		return result;
//	}
	
	
	
//	public static ArrayList<String> getAllClassesLabels(OntModel m) 
//	{
//		ArrayList<String> result = new ArrayList<String>();
//		ArrayList<String> uris = getAllClassesURIs(m);
//		for (String uri : uris) {
//			OntClass ontClass = m.getOntClass(uri);
//			String label = ontClass.getLabel(null);
//			if(label != null && label != ""){
//				result.add(label);
//			}else{
//				result.add("__");
//			}
//		}
//		return result;
//	}
	
	
	public static ArrayList<String> getDatatypeProptyURIs(OntModel m) 
	{
		
		ArrayList<String> result=new ArrayList<String>();
		ExtendedIterator<DatatypeProperty> i = m.listDatatypeProperties();

		while (i.hasNext()) {
			String c_temp =  i.next().getURI();
			if(c_temp != null && c_temp != "")
				result.add(c_temp);
        }
		return result;
		
	}
	
	
	public static ArrayList<String> getObjectProptyURIs(OntModel m) 
	{
		
		ArrayList<String> result=new ArrayList<String>();
		ExtendedIterator<ObjectProperty> i = m.listObjectProperties();

		while (i.hasNext()) {
			String c_temp =  i.next().getURI();
			if(c_temp != null && c_temp != "")
				result.add(c_temp);
        }
		return result;
	}

	
//	public static ArrayList<String> getDatatypeProptyIDs(OntModel m) 
//	{	
//		ArrayList<String> result = new ArrayList<String>();
//		ArrayList<String> uris = getDatatypeProptyURIs(m);
//		
//		for (String uri : uris) {
//			OntProperty datatypeProperty = m.getOntProperty(uri);
//			String id = datatypeProperty.getLocalName();
//			if(id != null && id != ""){
//				result.add(id);
//			}else{
//				//这里概念的ID和Label都不可能是空尤其是ID
//				result.add("__");
//			}
//		}
//		return result;
//	}
//	
//	public static ArrayList<String> getDatatypeProptyLabels(OntModel m) 
//	{	
//		ArrayList<String> result = new ArrayList<String>();
//		ArrayList<String> uris = getDatatypeProptyURIs(m);
//		
//		for (String uri : uris) {
//			OntProperty datatypeProperty = m.getOntProperty(uri);
//			String label = datatypeProperty.getLabel(null);
//			if(label != null && label != "" && (!label.equals(""))){
//				result.add(label);
//			}else{
//				result.add("__");
//			}
//		}
//		return result;
//	}
	
	
//	public static ArrayList<String> getObjectProptyIDs(OntModel m) 
//	{	
//		ArrayList<String> result = new ArrayList<String>();
//		ArrayList<String> uris = getObjectProptyURIs(m);
//		
//		for (String uri : uris) {
//			OntProperty objectProperty = m.getOntProperty(uri);
//			String id = objectProperty.getLocalName();
//			if(id != null && id != ""){
//				result.add(id);
//			}else{
//				//这里概念的ID和Label都不可能是空尤其是ID
//				result.add("__");
//			}
//		}
//		return result;
//	}
//	
//	public static ArrayList<String> getObjectProptyLabels(OntModel m) 
//	{	
//		ArrayList<String> result = new ArrayList<String>();
//		ArrayList<String> uris = getObjectProptyURIs(m);
//		
//		for (String uri : uris) {
//			OntProperty objectProperty = m.getOntProperty(uri);
//			String label = objectProperty.getLabel(null);
//			if(label != null && label != "" && (!label.equals(""))){
//				result.add(label);
//			}else{
//				result.add("__");
//			}
//		}
//		return result;
//	}
	
}