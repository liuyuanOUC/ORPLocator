package de.uni.heidelberg.ifi.conf.locateORPs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni.heidelberg.ifi.conf.elements.ConfClassVariable;
import de.uni.heidelberg.ifi.conf.taintAnalyzer.GetAllSubClasses;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;

public class CallSitesFromInstances {
	

	public List<ConfClassVariable> getVariablesOfConfClass(String fullClassName)
			throws XPathExpressionException {
		List list = new ArrayList<ConfClassVariable>();
		NodeList declNodes = AnalyzerUtils
				.getVariablesOfConfClass(fullClassName);
		for (int i = 0; i < declNodes.getLength(); i++) {
			String variableName = getConfClassName(declNodes.item(i));
			Node scope = getScopeOfConfClass(declNodes.item(i));
			list.add(new ConfClassVariable(variableName, scope));
		}

		return list;
	}

	public String getConfClassName(Node node) {
		List<Node> nameNode = AnalyzerUtils.getChildNodeByTagName(node, "name");
		if (nameNode.size() != 1)
			System.err.println("Incorrect declaration!");
		Node name= nameNode.iterator().next();
		return name.getTextContent();
	}

	public Node getScopeOfConfClass(Node node) {
		Node parentNode = node.getParentNode();
		Node grandParentNode = parentNode.getParentNode();
		Node scope = null;
		if (parentNode.getNodeName().equals("parameter")
				&& grandParentNode.getNodeName().equals("parameter_list")) {
			
				return scope = grandParentNode.getParentNode();
		}

		scope = AnalyzerUtils.getScope(node);
		return scope;

	}
	public Set getConfGetNodes(String fullClassName) throws XPathExpressionException{
		Set statments= new HashSet<Node>();
		List list=getVariablesOfConfClass(fullClassName);
		Iterator iter=list.iterator();
		while(iter.hasNext()){
			ConfClassVariable tmp=(ConfClassVariable)iter.next();
			Node scope=tmp.getScope();
			statments.addAll(searchConfCallSites(tmp.getVariableName(),scope));

			if(scope.getParentNode().getNodeName().equals("class")||scope.getParentNode().getNodeName().equals("interface"))
			{
				
				String className=AnalyzerUtils.getClassName(scope.getParentNode());
				System.out.println("has subClasses: "+className);
				GetAllSubClasses gSubClasses=new GetAllSubClasses();				
				gSubClasses.getSubClassList(className);
				statments.addAll(searchConfClassSitesOfSubClasses(tmp.getVariableName(), gSubClasses.getSubClassNodes()));
			}
			
			
		}
		return statments;

	}
	public Set<Node> searchConfCallSites(String variableName, Node scope) throws XPathExpressionException{
		Set statements=new HashSet();
		String xpath=".//call[./name/name[last()-1][text()='"+variableName+"']][./name/operator[text()='.']][./name/name[last()][contains(.,'get')]]";
		NodeList nodes=AnalyzerUtils.getNodeList(scope, xpath);
		for(int i=0;i<nodes.getLength();i++)
		{
			statements.add(nodes.item(i));
			System.out.println(nodes.item(i).getTextContent());
		}
		
		
		return statements;
	}
	public Set<Node> searchConfClassSitesOfSubClasses(String variableName,Set<Node> childrens) throws XPathExpressionException{
		Set statements=new HashSet();
		Iterator<Node> iter=childrens.iterator();
		while(iter.hasNext()){
			Node node=iter.next();
			statements.addAll(searchConfCallSites(variableName,node));
		}
		return statements;
	}
	
}
