package task;
import java.util.ArrayList;
import tools.ClassSimilarity1;
import tools.Operator;
import tools.PropertySimilarity1;
import tools.Tuning;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class GxsjkMatchTask {
	
	public static void main(String[] args){
		
		//实际的28所抽出来的本体的一个路径
		String file="file:C:/Users/Administrator/Desktop/gxjsk.owl";
		OntModel ontology = ModelFactory.createOntologyModel();
		ontology.read(file);		
		
		
		SparseDoubleMatrix2D idClassMatrix=ClassSimilarity1.getClassIdMatchingResult(ontology);
		SparseDoubleMatrix2D labelClassMatrix=ClassSimilarity1.getClassLabelMatchingResult(ontology);
		SparseDoubleMatrix2D resultClassMatrix=Operator.averageMatrix_1(idClassMatrix, labelClassMatrix);
		idClassMatrix=null; labelClassMatrix=null; System.gc();
		ArrayList<String> classSameAsInfo = Tuning.continusClass(resultClassMatrix, ontology, 0.8, 1.0);
		resultClassMatrix=null;System.gc();
		//刘丰将这个概念的匹配信息classSameAsInfo写入。URI:URI。
		
		
		
		SparseDoubleMatrix2D idPropMatrix= PropertySimilarity1.getDPIdMatchingResult(ontology);
		SparseDoubleMatrix2D labelPropMatrix=PropertySimilarity1.getDPLabelMatchingResult(ontology);
		SparseDoubleMatrix2D resultDPMatrix=Operator.averageMatrix_1(idPropMatrix, labelPropMatrix);
		idPropMatrix=null; labelPropMatrix=null; System.gc();
		ArrayList<String> dpSameAsInfo = Tuning.continusDP(resultDPMatrix, ontology, 0.8, 1.0);
		resultDPMatrix = null; System.gc();
		//刘丰将这个概念的匹配信息dpSameAsInfo写入。URI:URI。
		
		
		
		
		SparseDoubleMatrix2D idPropMatrix_= PropertySimilarity1.getOPIdMatchingResult(ontology);
		SparseDoubleMatrix2D labelPropMatrix_=PropertySimilarity1.getOPLabelMatchingResult(ontology);
		SparseDoubleMatrix2D resultOPMatrix=Operator.averageMatrix_1(idPropMatrix_, labelPropMatrix_);
		idPropMatrix_=null; labelPropMatrix_=null; System.gc();
		ArrayList<String> opSameAsInfo = Tuning.continusDP(resultOPMatrix, ontology, 0.8, 1.0);
		resultOPMatrix = null; System.gc();
		//刘丰将这个概念的匹配信息opSameAsInfo写入。URI:URI。
		
	
		
	}

}
