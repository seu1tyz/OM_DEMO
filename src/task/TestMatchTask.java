package task;

import java.io.FileNotFoundException;
import java.io.IOException;

import tools.Operator;
import tools.PropertySimilarity1;
import tools.Tuning;
import tools.WriteObjToFile;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * �������Ȿ���ڲ���֮��������Լ���������ƶȵķ���
 * @author seu1tyz
 */
public class TestMatchTask {
	/**
	 * ����ǲ�����ں���
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws IOException{	
			
		
		String file="file:C:/Users/Ethan/Desktop/onto1.rdf";
	//	String file1="file:C:/Users/Ethan/Desktop/small.owl";
		
		OntModel ontology = ModelFactory.createOntologyModel();
		ontology.read(file);	

		
//		System.out.println(OWLOntParse.getAllClassesIDsSameAsInfo2(ontology1));
//		System.out.println(OWLOntParse.getAllClassesLabelsSameAsInfo2(ontology));
//		System.out.println(OWLOntParse1.getDatatypeProptyIDs(ontology));
//		System.out.println(OWLOntParse1.getDatatypeProptyLabels(ontology));
//	    System.out.println(ClassSimilarity.getClassLabelMatchingResult(ontology, ontology));
//		System.out.println(PropertySimilarity.getProptiesLabelMatchingResult(ontology, ontology));
		
//      ����ʱ�����
//		long timeTestStart=System.currentTimeMillis();	
//		SparseDoubleMatrix2D idClassMatrix = ClassSimilarity1.getClassIdMatchingResult(ontology);
//		SparseDoubleMatrix2D labelClassMatrix=ClassSimilarity1.getClassLabelMatchingResult(ontology);
//		SparseDoubleMatrix2D resultClassMatrix=Operator.averageMatrix_1(idClassMatrix, labelClassMatrix);
//		System.out.println(resultClassMatrix);
//		Tuning.continusClass(resultClassMatrix, ontology, 0.7, 1.0);
//		WriteObjToFile.writeObjectToFile(resultClassMatrix);
//		idClassMatrix=null;labelClassMatrix=null;
		
		SparseDoubleMatrix2D idPropMatrix= PropertySimilarity1.getDPIdMatchingResult(ontology);
		SparseDoubleMatrix2D labelPropMatrix=PropertySimilarity1.getDPLabelMatchingResult(ontology);
		SparseDoubleMatrix2D resultPropMatrix=Operator.averageMatrix_1(idPropMatrix, labelPropMatrix);
//		System.out.println(resultPropMatrix);
		Tuning.continusDP(resultPropMatrix, ontology, 0.8, 1.0);
		idPropMatrix=null;labelPropMatrix=null;
//
//		long timeTestEnd=System.currentTimeMillis();
//		
//		System.out.println("һ�����е�ʱ����"+(timeTestEnd-timeTestStart)+"ms");
//		System.out.println("����ľ�����:    "+resultClassMatrix);		
//		System.out.println("���Եľ�����:    "+resultPropMatrix);		

	}
	
}