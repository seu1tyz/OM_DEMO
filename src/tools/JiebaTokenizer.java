package tools;

/*********************
 * Class information
 * 使用JIEBA分词API进行中文分词模块编写；
 * -------------------
 * @author Devin Hua
 * @date   2016-10-18
 * describe:
 * 中文分词，并利用正则表达式和UnicodeBlock删去句中标点符号.
 * 使用“哈工大停用词词库”、“四川大学停用词库”、“百度停用词表”，整理得到共1598个停用词的词表
 * 使用该词表进行停用词的筛除。
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
 * 对结巴分词相关的重写
 * @author huaYunCheng,seu1tyz
 *
 */
public class JiebaTokenizer {
	
	private JiebaSegmenter segmenter;
	private Set<String> specialPunctuationSet;//使用特殊标点符号集合，判断是否为特殊标点符号;
	private Set<String> stopwordSet;//使用停用词集合，判断是否为频繁词;
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
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf-8"));//输入流
           
		while((str=br.readLine())!=null){//按行读取
		    String word = str.trim();
		    wordSet.add(word);			    
		}
		br.close();//关闭流
		return wordSet;
	}

	public List<String> Tokenizer(String sentence) {             
           
		List<String> Tokenizerlist = new ArrayList<String>(); //去除标点符号后，返回的分词结果列表; 
		List<SegToken> resultList = this.segmenter.process(sentence, SegMode.SEARCH); //通过JIEBA分词得到的分词结果列表;
        //List<SegToken> resultList = this.segmenter.process(sentence, SegMode.INDEX);
        Iterator<SegToken> it = resultList.iterator();  
        if (!it.hasNext())  
            return Tokenizerlist;    
         
        while (it.hasNext()) {  
            SegToken s = it.next();  
            if(!" ".equals(s.word)){
            	//若分词后的某个句子片段只有一个字符，则判断是否为Punctuation或者stopwords，若是则去除；
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
		//使用UnicodeBlock判断是否为中文标点符号;
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
		//使用正则表达式判断是否为中文或英文标点符号;		
		return temp.matches("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]");	
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
		        new String[] {"这是一个伸手不见五指的【黑夜】。我叫孙悟空，我爱北京，我爱Python和C++。", 
				"我不喜欢日本和服。", "雷猴回归人间迟早充分充其极充其量抽冷子臭。",
				"工信处女干事每月经过下属科室都要亲口交代交换机等技术性器件的安装工作。", "结果婚的和尚未结过婚的。", "表示军队的作战方案。",
				"java中怎样将char[]和String相互转换?",
				"上面的三个方案中给出的都是采用 Unicode标点属性方式的正则表达式，可以去掉所有的标点符号，包括全角、半角、横排、竖排等的标点符号。",
				"我一个月的工资是￥30000人民币，出国的那位同学却高达不亦乐乎的$90000美元！",
				"我叫孙悟空，我爱北京，我爱Python和C++||我一个月的工资是￥30000人民币，出国的那位同学却高达不亦乐乎的$90000美元||java中怎样将char[]和String相互转换?||",
				"When the reference was first published"};  
        for (String sentence : sentences) {  
        	resultList = test_Tokenizer.Tokenizer(sentence);
    		System.out.println(test_Tokenizer.ListToString(resultList));  
        }
	}
}
