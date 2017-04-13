package task;

import java.util.ArrayList;

import tools.ClassSimilarity1;
import tools.Operator;
import tools.PropertySimilarity1;
import tools.Tuning;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JssybMatchTask {
	
	public static void main(String[] args){	
		
		String file="file:C:/Users/Administrator/Desktop/jsjxb.owl";
		OntModel ontology = ModelFactory.createOntologyModel();
		ontology.read(file);	
		
		//加入时间变量
		
		SparseDoubleMatrix2D idClassMatrix=ClassSimilarity1.getClassIdMatchingResult(ontology);
		SparseDoubleMatrix2D labelClassMatrix=ClassSimilarity1.getClassLabelMatchingResult(ontology);
		SparseDoubleMatrix2D resultClassMatrix=Operator.averageMatrix_1(idClassMatrix, labelClassMatrix);
		idClassMatrix=null;labelClassMatrix=null;System.gc();
		ArrayList<String> target = Tuning.continusClass(resultClassMatrix, ontology, 0.8, 1.0);
		//接下来  让刘丰将target信息  写入 本体文件。URI:URI
		
	}

}
