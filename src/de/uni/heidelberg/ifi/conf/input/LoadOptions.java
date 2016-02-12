package de.uni.heidelberg.ifi.conf.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;
import de.uni.heidelberg.ifi.conf.utils.Utils;

public class LoadOptions {

	public static List<String> core=new ArrayList<String>();
	public static List<String> hdfs=new ArrayList<String>();
	public static List<String> mapreduce=new ArrayList<String>();
	public static List<String> yarn=new ArrayList<String>();
	
	
	public static void init() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
		LoadOptions loader=new LoadOptions();
		core=loader.getOptions("./data/common/core.xml");
		System.out.println("core size: "+core.size());
		hdfs=loader.getOptions("./data/common/hdfs.xml");
		System.out.println("hdfs size: "+hdfs.size());
		mapreduce=loader.getOptions("./data/common/mapred.xml");
		System.out.println("mapreduce size: "+mapreduce.size());
		yarn=loader.getOptions("./data/common/yarn.xml");
		System.out.println("yarn size: "+yarn.size());
		
	}
	public List<String> getOptions(String optionFile) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();  
        Document document = docBuilder.parse(new File(optionFile)); 
        List<String> optionList=new ArrayList<String>();
        
        String xpath="//name";
        NodeList nodeList=AnalyzerUtils.getNodeList(document, xpath);
        
        for(int i=0; i<nodeList.getLength();i++){
        	optionList.add(Utils.removeSpaceLineBreaks(nodeList.item(i).getTextContent()));
        }
        return optionList;
		
	}
	
	public static int isConfFile(String name){
		String tmp=name.replace("\"", "");
		if(core.contains(tmp))
			return 1;
		if(hdfs.contains(tmp))
			return 2;
		if(mapreduce.contains(tmp))
			return 3;
		if(yarn.contains(tmp))
			return 4;
		else
			return 0;
	}
	public static void outputFile(List optionList, String path) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		Iterator<String> iter=optionList.iterator();
		
		while(iter.hasNext()){
			String option=iter.next();
			writer.println(option);
		}
		
		writer.flush();
		writer.close();
		
	}
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
		init();
		outputFile(yarn, "./data/yarn.cvs");
	}

}
