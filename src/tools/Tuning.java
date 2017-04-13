package tools;

import java.io.IOException;
import java.util.ArrayList;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import com.hp.hpl.jena.ontology.OntModel;


public class Tuning {
	/**
	 * @param args
	 * @throws IOException 
	 */
//	public static void main(String[] args) throws IOException {
//		// TODO Auto-generated method stub
//		String file="file:C:/Users/Administrator/Desktop/OAEII.owl";
//		OntModel ontology = ModelFactory.createOntologyModel();
//		ontology.read(file);	
//		
//		double left = 0.8;
//		double right = 0.9;
//		ArrayList<String> classIDs = OWLOntParse.getAllClassesIDs(ontology);
//		ArrayList<String> classLabels = OWLOntParse.getAllClassesLabels(ontology);
//		ArrayList<String> outputList = new ArrayList<String>();
//		
//		SparseDoubleMatrix2D classAvrMatrix = (SparseDoubleMatrix2D)WriteObjToFile.readObjectFromFile();
//		
//		int size = classAvrMatrix.rows();
//		//size*size的矩阵，遍历这个矩阵本身
//		for(int i=0;i<size;i++){
//			for(int j=i+1;j<size;j++){
//				double similarity = classAvrMatrix.getQuick(i, j);
//				if((similarity>=left) && (similarity<=right)){
//					String output = classIDs.get(i)+"("+classLabels.get(i)+")"+":"+classIDs.get(j)+"("+
//							classLabels.get(j)+")"+"-->"+similarity;
//					outputList.add(output);
//				}
//			}
//		}	
//		Operator.writeInfo2file(outputList);
//	}
	
	public static ArrayList<String> continusClass(SparseDoubleMatrix2D classAvrMatrix,OntModel ontology,double left1,double right1) throws IOException  {
		
		double left = left1;
		double right = right1;
		
		ArrayList<String> classURIs = OWLOntParse1.getAllClassesURIs(ontology);
		ArrayList<String> outputList = new ArrayList<String>();
		
		int size = classAvrMatrix.rows();
		//size*size的矩阵，遍历这个矩阵本身
		for(int i=0;i<size;i++){
			for(int j=i+1;j<size;j++){
				double similarity = classAvrMatrix.getQuick(i, j);
				if((similarity >= left) && (similarity <= right)){
					String output = classURIs.get(i)+","+classURIs.get(j)+","+similarity;
					outputList.add(output);
				}
			}
		}	
		Operator.writeInfo2file(outputList);
		return outputList;
	}
	
	public static ArrayList<String> continusDP(SparseDoubleMatrix2D dpAvrMatrix,OntModel ontology,double left1,double right1) throws IOException  {
		
		double left = left1;
		double right = right1;
		
		ArrayList<String> dpURIs = OWLOntParse1.getDatatypeProptyURIs(ontology);
		ArrayList<String> outputList = new ArrayList<String>();
		
		int size = dpAvrMatrix.rows();
		//size*size的矩阵，遍历这个矩阵本身
		for(int i=0;i<size;i++){
			for(int j=i+1;j<size;j++){
				double similarity = dpAvrMatrix.getQuick(i, j);
				if((similarity >= left) && (similarity <= right)){
					String output = dpURIs.get(i)+","+dpURIs.get(j)+","+similarity;
					outputList.add(output);
				}
			}
		}
		Operator.writeInfo2file(outputList);
		return outputList;
	}
	
	
	
	public static ArrayList<String> continusOP(SparseDoubleMatrix2D opAvrMatrix,OntModel ontology,double left1,double right1)  {
		
		double left = left1;
		double right = right1;
		
		ArrayList<String> opURIs = OWLOntParse1.getObjectProptyURIs(ontology);
		ArrayList<String> outputList = new ArrayList<String>();
		
		
		int size = opAvrMatrix.rows();
		//size*size的矩阵，遍历这个矩阵本身
		for(int i=0;i<size;i++){
			for(int j=i+1;j<size;j++){
				double similarity = opAvrMatrix.getQuick(i, j);
				if((similarity >= left) && (similarity <= right)){
					String output = opURIs.get(i)+":"+opURIs.get(j);
					outputList.add(output);
				}
			}
		}	
		return outputList;
	}
	
}