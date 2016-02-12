package de.uni.heidelberg.ifi.conf.taintAnalyzer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import de.uni.heidelberg.ifi.conf.Constant.Constants;
import de.uni.heidelberg.ifi.conf.locateORPs.CallSitesFromInstances;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;


public class Test {
	
	public static HashSet allClasses=null;
		public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
			// XPath object is compiled for reuse  

	        
	         
	        // Use DOM API to get full XML document  
	         
	       
//	        NodeList unit=(NodeList)expression.evaluate(document,  XPathConstants.NODESET);
//	        String source="io.compression.codecs";
//	        NodeList nodelist=Utils.getTaintSourceMehtods(document,Utils.getXPath(), source);
//	        
	        Constants.preDirecotry="java\\hadoop-common";  
	        
	        String fileLocal="org.apache.hadoop.hdfs.tools.HDFSConcat.java";
	        String functionLocal="main";
	        String variableLocal="srcs";  
	        
	        String fileInstanceVariable="org.apache.hadoop.fs.FileSystem.java";
	        String functionInstanceVariable="add";
	        String variableInstanceVariable="readOps";
	        
	        String fileClassVariable="org.apache.hadoop.hdfs.server.namenode.BackupNode.java";
	        String functionClassVariable="loadNamesystem";
	        String variableClassVariable="DFS_NAMENODE_SAFEMODE_EXTENSION_KEY";
	        
	        String fileExternalInstanceVariable="org.apache.hadoop.fs.FileSystem.java";
	        String functionExternalInstanceVariable="getInternal";
	        String variableExternalInstanceVariable="key";
	        
	        
	        AnalyzerUtils.loadXMLDoc("./data/common/hadoo-common-lineNum.xml");
	    	System.out.println("starting...");
//	    	LoadingPointsLocator vl=new LoadingPointsLocator();
//	    	Set nodes=vl.getConfGetNodes("Configuration");
//	    	System.out.println("start writing..");
//	    	AnalyzerUtils.outFile(nodes);
//			AnalyzerUtils.getVariablesOfConfClass("Configuration");
	        AnalyzerUtils.getAllClasses(AnalyzerUtils.document); 
	        
//	        NodeList unit=AnalyzerUtils.getSourceVariable(AnalyzerUtils.document, fileClassVariable, functionClassVariable, variableClassVariable);
//	        System.out.println(unit.getLength());
//	        
//	        InitDVariable initV= new InitDVariable();
//	        DVariable dV=initV.init(unit.item(0));
//	        Constants.logger.debug(dV.toString());
//	       
//
//	        AnalyzerUtils.println(unit);
//	        System.out.println(unit.item(0).getTextContent());
//	        System.out.println(LineNumCounter.getInstance().getLineNum(AnalyzerUtils.getFileNode(unit.item(0)),unit.item(0)));
	        
	        InferSuperClasses superClasses=new InferSuperClasses();
	        Set<String> superClassNames=superClasses.getSuperClasses(AnalyzerUtils.getClassNode("org.apache.hadoop.fs.CommonConfigurationKeys"));
	       
	        
	        Iterator iter=superClassNames.iterator();
	        while(iter.hasNext()){
	     		System.out.println(iter.next());
	     	}
	          
	    }  
			

}
