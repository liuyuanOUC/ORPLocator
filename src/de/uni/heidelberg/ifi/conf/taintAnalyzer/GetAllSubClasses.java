package de.uni.heidelberg.ifi.conf.taintAnalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;

public class GetAllSubClasses {
	private LinkedList<String> workList=new LinkedList<String>();
	private HashSet classNames=new HashSet<String>();
	private HashSet classNodes=new HashSet<Node> ();
	private Map<String,Node> childClasses=new HashMap();
	
	public Set getSubClassList(String className) throws XPathExpressionException{
		Set<String> confClassNames=getDirectSubClassNames(className);
		workList.addAll(confClassNames);
		
		while(!workList.isEmpty()){
			String tmp=workList.removeFirst();
			classNames.add(tmp);
			workList.addAll(getDirectSubClassNames(tmp));
		}
		
		return classNames;	
		
	}
	public Set<String> getDirectSubClassNames(String className) throws XPathExpressionException{
		Set<String> confClassNames=new HashSet<String>();
		NodeList list=AnalyzerUtils.getSubClasses(className);
		for(int i=0;i<list.getLength();i++){
			List<Node> confClassNodes=AnalyzerUtils.getChildNodeByTagName(list.item(i), "name");	
			String name=confClassNodes.iterator().next().getFirstChild().getTextContent();
			confClassNames.add(name);
			classNodes.add(list.item(i));
			childClasses.put(name, list.item(i));
		}
		return confClassNames;
	}
	public Set<Node> getSubClassNodes(){
		return classNodes;
	}
	public Map getMapClassNodes(){
		return childClasses;
	}
}
