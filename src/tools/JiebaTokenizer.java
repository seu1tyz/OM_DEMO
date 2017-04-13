package tools;

/*********************
 * Class information
 * ʹ��JIEBA�ִ�API�������ķִ�ģ���д��
 * -------------------
 * @author Devin Hua
 * @date   2016-10-18
 * describe:
 * ���ķִʣ�������������ʽ��UnicodeBlockɾȥ���б�����.
 * ʹ�á�������ͣ�ôʴʿ⡱�����Ĵ���ѧͣ�ôʿ⡱�����ٶ�ͣ�ôʱ�������õ���1598��ͣ�ôʵĴʱ�
 * ʹ�øôʱ����ͣ�ôʵ�ɸ����
 ********************/
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;   
import com.huaban.analysis.jieba.JiebaSegmenter;  
import com.huaban.analysis.jieba.SegToken;  
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
/**
 * �Խ�ͷִ���ص���д
 * @author huaYunCheng,seu1tyz
 *
 */
public class JiebaTokenizer {
	
	private JiebaSegmenter segmenter;
	private Set<String> specialPunctuationSet;//ʹ����������ż��ϣ��ж��Ƿ�Ϊ���������;
	private Set<String> stopwordSet;//ʹ��ͣ�ôʼ��ϣ��ж��Ƿ�ΪƵ����;
	private String stopwordFileName = "config/stopwords.dat";
	private String specialPunctionsFileName = "config/special_punctions.dat";
	

	public JiebaTokenizer() throws FileNotFoundException, IOException{
		this.segmenter = new JiebaSegmenter();
		this.stopwordSet =  GetWords(stopwordFileName);
		this.specialPunctuationSet = GetWords(specialPunctionsFileName);
	}
	
	public Set<String> GetWords(String filename)  throws IOException, FileNotFoundException {	
		String str;
		Set<String> wordSet =new HashSet<String>();
        BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf-8"));//������
           
		while((str=br.readLine())!=null){//���ж�ȡ
		    String word = str.trim();
		    wordSet.add(word);			    
		}
		br.close();//�ر���
		return wordSet;
	}

	public List<String> Tokenizer(String sentence) {             
           
		List<String> Tokenizerlist = new ArrayList<String>(); //ȥ�������ź󣬷��صķִʽ���б�; 
		List<SegToken> resultList = this.segmenter.process(sentence, SegMode.SEARCH); //ͨ��JIEBA�ִʵõ��ķִʽ���б�;
        //List<SegToken> resultList = this.segmenter.process(sentence, SegMode.INDEX);
        Iterator<SegToken> it = resultList.iterator();  
        if (!it.hasNext())  
            return Tokenizerlist;    
         
        while (it.hasNext()) {  
            SegToken s = it.next();  
            if(!" ".equals(s.word)){
            	//���ִʺ��ĳ������Ƭ��ֻ��һ���ַ������ж��Ƿ�ΪPunctuation����stopwords��������ȥ����
            	if((s.endOffset==(s.startOffset+1))
            			&&(isChinesePunctuation(s.word)||isPunctuations(s.word)
            				||this.specialPunctuationSet.contains(s.word)))
            		continue;
            	else if(this.stopwordSet.contains(s.word))
            		continue;
            	else
            		Tokenizerlist.add(s.word);
            	}            		  
            }   
        return Tokenizerlist;          
    }
	
	
	public static boolean isChinesePunctuation(String temp) {
		//ʹ��UnicodeBlock�ж��Ƿ�Ϊ���ı�����;
		if(temp.length()!=1)
			return false;
		char[] tempArray = temp.toCharArray();
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(tempArray[0]);
        if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS
                ) {
            return true;
        } else {
            return false;
        }
    }
	
	
	public boolean isPunctuations(String temp){
		//ʹ��������ʽ�ж��Ƿ�Ϊ���Ļ�Ӣ�ı�����;		
		return temp.matches("[\\p{P}+~$`^=|<>�����ޣ�������������]");	
	}
	
	public String ListToString(List<String> list){
		
		String[] printArray = list.toArray(new String[list.size()]);
		String printString = "";
		for(String temp:printArray)
		{
			printString+=temp;
			printString+=" ";
		}
		printString = printString.trim();
		return printString;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		JiebaTokenizer test_Tokenizer = new JiebaTokenizer();
		
		List<String> resultList = null;
		String[] sentences =  
		        new String[] {"����һ�����ֲ�����ָ�ġ���ҹ�����ҽ�����գ��Ұ��������Ұ�Python��C++��", 
				"�Ҳ�ϲ���ձ��ͷ���", "�׺�ع��˼�����ֳ��伫�����������ӳ���",
				"���Ŵ�Ů����ÿ�¾����������Ҷ�Ҫ�׿ڽ����������ȼ����������İ�װ������", "�����ĺ���δ�����ġ�", "��ʾ���ӵ���ս������",
				"java��������char[]��String�໥ת��?",
				"��������������и����Ķ��ǲ��� Unicode������Է�ʽ��������ʽ������ȥ�����еı����ţ�����ȫ�ǡ���ǡ����š����ŵȵı����š�",
				"��һ���µĹ����ǣ�30000����ң���������λͬѧȴ�ߴﲻ���ֺ���$90000��Ԫ��",
				"�ҽ�����գ��Ұ��������Ұ�Python��C++||��һ���µĹ����ǣ�30000����ң���������λͬѧȴ�ߴﲻ���ֺ���$90000��Ԫ||java��������char[]��String�໥ת��?||",
				"When the reference was first published"};  
        for (String sentence : sentences) {  
        	resultList = test_Tokenizer.Tokenizer(sentence);
    		System.out.println(test_Tokenizer.ListToString(resultList));  
        }
	}
}
