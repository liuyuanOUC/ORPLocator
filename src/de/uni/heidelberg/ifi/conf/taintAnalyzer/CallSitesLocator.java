package de.uni.heidelberg.ifi.conf.taintAnalyzer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni.heidelberg.ifi.conf.locateORPs.CallSitesFromInstances;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;

public class CallSitesLocator {
	private Set<Node> allCallSites= new HashSet<Node>();
	private Map<String,Node> confClasses=null;
	private String mainConfClass=null;
	
	
	
	public CallSitesLocator(Map<String, Node> confClasses, String mainConfClass) {
		super();
		this.confClasses = confClasses;
		this.mainConfClass = mainConfClass;
	}
	//get callSites called by return instances from callSites like data.getConf().getInt();
	public  Set<String> getMethodByReuturnType(String returnType) throws XPathExpressionException{
		String xpath=".//function_decl[./type//name[text()='" + returnType + "']]|.//function[./type/name[text()='" + returnType + "']]";
		Set<String> fNames=new HashSet<String>();
		NodeList functions=AnalyzerUtils.getNodeList(AnalyzerUtils.document, xpath);
		for(int i=0;i<functions.getLength();i++){
			List<Node> functionName=AnalyzerUtils.getChildNodeByTagName(functions.item(i), "name");
			Iterator<Node> iter=functionName.iterator();
			String fName=iter.next().getTextContent();
			fNames.add(fName);
		}
		return fNames;
	}
	public  Set<Node> getCallSitesByMethodName(String methodName) throws XPathExpressionException{
		Set<Node> callSites=new HashSet<Node>();
		String xpath=".//expr[./call[last()-1][.//name[text()='" + methodName + "']] and ./call[last()]/name[contains(.,'get')]]";

		NodeList callSitesList=AnalyzerUtils.getNodeList(AnalyzerUtils.document, xpath);
		for(int i=0;i<callSitesList.getLength();i++){
			System.out.println(callSitesList.item(i).getTextContent());
			callSites.add(callSitesList.item(i).getLastChild());
		}
		return callSites;
	}
	public  Set<Node> getCallSitesByMethodName(Set<String> methodNames) throws XPathExpressionException{
		Set<Node> callSites=new HashSet<Node>();
		Iterator<String> iter=methodNames.iterator();
		while(iter.hasNext()){
			String name=iter.next();
			callSites.addAll(getCallSitesByMethodName(name));
		}
		return callSites;
	}
	
	//get callSites inside C
	
	public Set<Node> getCallSitesInsideOfClass(Node classNode) throws XPathExpressionException{
		Set<Node> callSites=new HashSet<Node>();
		String xpath=".//call[./name[contains(.,'get')]]";
		NodeList callSiteNodes=AnalyzerUtils.getNodeList(classNode,xpath);
		for(int i=0;i<callSiteNodes.getLength();i++){
			callSites.add(callSiteNodes.item(i));
		}
		return callSites;
	} 
	
	//get callsites called by instances of Configuration-handling classes
	public Set<Node> getAllCallSites() throws XPathExpressionException{
		
		CallSitesFromInstances loadPoints=new CallSitesFromInstances();
//		allCallSites.addAll(getCallSitesByMethodName(getMethodByReuturnType(mainConfClass)));
//		allCallSites.addAll(loadPoints.getConfGetNodes(mainConfClass));
		
		Iterator iter=confClasses.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			String className = (String) entry.getKey();
			Node node = (Node) entry.getValue();
			
			if(node!=null)
			allCallSites.addAll(getCallSitesInsideOfClass(node));
			System.out.println(getMethodByReuturnType(className)+":::"+className);
			allCallSites.addAll(getCallSitesByMethodName(getMethodByReuturnType(className)));
			
			allCallSites.addAll(loadPoints.getConfGetNodes(className));
			
		}
		return allCallSites;
	}
	
}
