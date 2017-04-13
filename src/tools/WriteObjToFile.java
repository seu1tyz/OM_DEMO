package tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class WriteObjToFile {
	
	 public static boolean writeObjectToFile(Object obj,String fileName)
	    {
		 	if(fileName.equals("")||fileName==null){
		 		return false;
		 	}
	        File file =new File(fileName);
	        FileOutputStream out;
	        try {
	            out = new FileOutputStream(file);
	            ObjectOutputStream objOut=new ObjectOutputStream(out);
	            objOut.writeObject(obj);
	            objOut.flush();
	            objOut.close();
	            System.out.println("write object success!");
	            return true;
	        } catch (IOException e) {
	            System.out.println("write object failed");
	            e.printStackTrace();
	            return false;
	        }
	    }
	 
	  public static Object readObjectFromFile(String fileName)
	    {
		  	if(fileName.equals("")||fileName==null){
		 		return null;
		 	}
	        Object temp=null;
	        File file =new File(fileName);
	        FileInputStream in;
	        try {
	            in = new FileInputStream(file);
	            ObjectInputStream objIn=new ObjectInputStream(in);
	            temp=objIn.readObject();
	            objIn.close();
	            System.out.println("read object success!");
	        } catch (IOException e) {
	            System.out.println("read object failed");
	            e.printStackTrace();
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	        return temp;
	    }
}
