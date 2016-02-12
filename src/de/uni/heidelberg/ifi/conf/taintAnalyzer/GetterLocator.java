package de.uni.heidelberg.ifi.conf.taintAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.uni.heidelberg.ifi.conf.elements.Callsite_OptionName;
import de.uni.heidelberg.ifi.conf.input.LoadOptions;
import de.uni.heidelberg.ifi.conf.locateORPs.ParameterInfer;
import de.uni.heidelberg.ifi.conf.output.OutOptions;
import de.uni.heidelberg.ifi.conf.output.OutputFilesofCallSites;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;


public class GetterLocator {

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {
		// TODO Auto-generated method stub
		AnalyzerUtils.loadXMLDoc("./data/yarn/yarn-lineNum.xml");

		String mainConfClassName = "Configuration";
		
		System.out.println("starting...");
		LoadOptions.init();
		long startT = System.currentTimeMillis();
		System.out.println("extracting all classes...");
		AnalyzerUtils.getAllClasses(AnalyzerUtils.document);
		int count=1;
		List<Callsite_OptionName> results=new ArrayList();
		Set<Node> nodes=new HashSet<Node>();
		

		
/* Given a callsite to infer the value of a parameter */		
//		String fileName="java\\hadoop-hdfs-httpfs\\org\\apache\\hadoop\\fs\\http\\client\\HttpFSFileSystem.java"; 
//		int lineNum=372;
//		String nodeName="call";
//		NodeList calls=AnalyzerUtils.getNodeByLineNum(fileName, lineNum, nodeName);
//		System.out.println("--"+calls.getLength());
//		Node node=calls.item(1);
//		Set<Node> nodes=new HashSet();
//		nodes.add(node);
		
		System.out.println("locating all sub classes of the conf-handling class...");
		GetAllSubClasses getAllSubClasses=new GetAllSubClasses();
		getAllSubClasses.getSubClassList(mainConfClassName);
		Map<String,Node> confClasses=getAllSubClasses.getMapClassNodes();
		confClasses.put(mainConfClassName, AnalyzerUtils.getClassNode("org.apache.hadoop.conf.Configuration"));
		
		CallSitesLocator callSitesLocator=new CallSitesLocator(getAllSubClasses.getMapClassNodes(),mainConfClassName);
		
		nodes=callSitesLocator.getAllCallSites();
	
		System.out.println("The number of callSites: "+nodes.size());
		System.out.println("output files of call sites of conf option...");
//		OutputFilesofCallSites.outputFilesofCallSites(nodes, "./data/yarn/files/");
		
		System.out.println("inferring the option name...");
		Iterator<Node> iterCallSite=nodes.iterator();
		
		while(iterCallSite.hasNext()){
			count++;
			
			Node tmp=iterCallSite.next();
			System.out.println("callSites: "+tmp.getTextContent());
			List<String> optionNames=new ArrayList();
			List<Node> paras=AnalyzerUtils.getParameters(tmp);
			if(paras==null)
				continue;
			Iterator<Node> iterPara=paras.iterator();
			while(iterPara.hasNext()){
				
				ParameterInfer paraInfer=new ParameterInfer();
				Node n=iterPara.next();
				System.out.println("para:  "+n.getTextContent());
				String name=paraInfer.getValueFromExpr(n.getFirstChild());
				System.out.println("The values--------------------- "+ name);
				optionNames.add(name);
			}
			results.add(new Callsite_OptionName(tmp,optionNames));
			System.out.println("The number of callsites processed :"+count);
			System.out.println("The number of cache nodes :"+ParameterInfer.cacheMap.size());
			
			
		}
		System.out.println("Time cost: " + (System.currentTimeMillis() - startT)/(double)1000 + " s");
				
		OutOptions.outFile(results);
	}

}
