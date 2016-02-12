package de.uni.heidelberg.ifi.conf.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni.heidelberg.ifi.conf.Constant.Constants;

public class AnalyzerUtils {
	public static XPath xpath = null;
	public static HashMap allClasses = null;
	public static Document document = null;
	public static int num = 0;
	public static boolean flag = false;

	public static XPath getXPath() {
		if (xpath == null) {
			XPathFactory factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
		}
		return xpath;
	}

//	public static NodeList getTaintSourceMehtods(Document doc, String source)
//			throws XPathExpressionException {
//
//		// The name of an option is quoted in the source code. Adding quotes
//		// distinguish the option from source code
//		source = "\"" + source + "\"";
//		String confLoaderClass = "conf";
//		String confLoaderMethod = "get";
//
//		xpath = getXPath();
//
//		// find all functions which contains the statement which loads the value
//		// of an option read point
//		// XPathExpression expression =
//		// xpath.compile("//function[descendant::call[./name/name='" +call+
//		// "' and ./name/name='get' and ./argument_list/argument/expr='"+source+"']]");
//
//		// find all call statements which loads the value of an option read
//		// point
//
//		String path = "//call[./name/name='" + confLoaderClass
//				+ "' and ./name/name='" + confLoaderMethod
//				+ "' and ./argument_list/argument/expr='" + source + "']";
//		XPathExpression expression = xpath.compile(path);
//		final NodeList result = (NodeList) expression.evaluate(doc,
//				XPathConstants.NODESET);
//
//		return result;
//	}

	public static Node getMethod(Node stm) {

		Node parent = null;
		Node temp = stm.getParentNode();
		while (temp != null) {
			if (temp.getNodeName().equals("function")
					|| temp.getNodeName().equals("constructor")) {
				parent = temp;
				break;
			}

			temp = temp.getParentNode();
		}
		return parent;

	}

	public static Node getClass(Node stm) {
		Node parent = null;
		Node temp = stm.getParentNode();
		if(temp==null)
			return null;

		while (temp.getNodeName() != "unit") {
			if (temp.getNodeName().equals("class")
					|| temp.getNodeName().equals("interface")) {
				parent = temp;
				break;
			}
			temp = temp.getParentNode();
		}
		if(parent==null)
			parent=temp;
		return parent;
	}

	public static Node getFileNode(Node stm) {
		Node parent = null;
		Node temp = stm.getParentNode();
		while (temp != null) {
			if (temp.getNodeName().equals("unit")) {
				parent = temp;
				break;
			}
			temp = temp.getParentNode();
		}
		return parent;
	}

	public static NodeList getNodeList(Node node, String path)
			throws XPathExpressionException {
		XPath xpath = getXPath();
		XPathExpression expression = xpath.compile(path);

		return (NodeList) expression.evaluate(node, XPathConstants.NODESET);

	}

	public static Node getNode(Node node, String path)
			throws XPathExpressionException {
		XPath xpath = getXPath();
		XPathExpression expression = xpath.compile(path);

		return (Node) expression.evaluate(node, XPathConstants.NODE);

	}

	public static String extractPackageName(String fileName) {
		String packageName = "";
		String[] tmp = fileName.split("\\\\");
		for (int i = 2; i < tmp.length - 1; i++)
			packageName = packageName + tmp[i] + ".";
		return packageName;
	}

	public static HashMap<String, Node> getAllClasses(Document doc)
			throws XPathExpressionException {

		allClasses = new HashMap<String, Node>();
		String classpath = ".//class/name|.//interface/name";

		NodeList classpathNode = getNodeList(doc, classpath);
		for (int j = 0; j < classpathNode.getLength(); j++) {
			Node file = getFileNode(classpathNode.item(j));
			String fileName = file.getAttributes().getNamedItem("filename")
					.getNodeValue();
			String packageName = extractPackageName(fileName);
			//Here only class name is extracted without class parameters, for instance, generic type class A<T>
			String fullClassName = packageName
					+ classpathNode.item(j).getFirstChild().getTextContent();
			allClasses
					.put(fullClassName, classpathNode.item(j).getParentNode());

		}

		return allClasses;
	}

	public static HashMap<String, Node> getAllClasses() {
		if (allClasses != null)
			return allClasses;
		else
			return null;
	}

	public static NodeList getSourceVariable(Document doc, String fileName,
			String function, String variable) throws XPathExpressionException {
		xpath = getXPath();
		fileName = getFileNameML(fileName);
		System.out.println(fileName);
		String path = "unit/unit[@filename='" + fileName
				+ "']//function[./name='" + function + "']//name[text()='"
				+ variable + "']";
		XPathExpression xExpression = xpath.compile(path);
		NodeList nodeList = (NodeList) xExpression.evaluate(doc,
				XPathConstants.NODESET);

		return nodeList;
	}

	public static String getFileNameML(String fileName) {

		String name = Constants.preDirecotry;
		String[] tmp = fileName.split("\\.");
		for (int i = 0; i < tmp.length; i++) {
			if (i == tmp.length - 2) {
				name = name + "\\" + tmp[i] + "." + tmp[i + 1];
				break;
			} else
				name = name + "\\" + tmp[i];
		}

		return name;
	}

	public static void println(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			System.out.println("NODE[" + i + "]: "
					+ nodeList.item(i).getTextContent());
		}
	}

//	public static NodeList getClassVariables(Document doc)
//			throws XPathExpressionException {
//		xpath = getXPath();
//		String path = "//expr/name[text()='.']";
//		XPathExpression xEpression = xpath.compile(path);
//		NodeList nodeList = (NodeList) xEpression.evaluate(doc,
//				XPathConstants.NODESET);
//		return nodeList;
//	}

	public static boolean isClassVariable(Node node) {
		
		
		if (node.getPreviousSibling() != null) {
			Node preNode = node.getPreviousSibling();
			if (preNode.getTextContent().equals(".")) {
				if (preNode.getPreviousSibling() != null) {
					Node classNode = preNode.getPreviousSibling();
					if (classNode.getNodeName().equals("name"))
						if (classNode.getParentNode() != null) {
							Node nodeTotalName = classNode.getParentNode();
							if (nodeTotalName.getNodeName().equals("name"))
								if (nodeTotalName.getParentNode() != null
										&& nodeTotalName.getParentNode()
												.getNodeName().equals("expr"))
									return true;
						}
				}

			}

		}

		return false;
	}

	public static NodeList searchDeclFromFields(Node variableName)
			throws XPathExpressionException {
		Node classNode = AnalyzerUtils.getClass(variableName);
		System.out.println("ClassNode: "+variableName.getTextContent());
		String path = "./block/decl_stmt[./decl/name='"+ variableName.getTextContent() + "']";
		NodeList declNode = AnalyzerUtils.getNodeList(classNode, path);
		return declNode;
	}

	public static Node searchDeclsFromFuction(Node variableName)
			throws XPathExpressionException {
		Node parentNode = variableName.getParentNode();
		Node declNode = null;
		while (parentNode != null) {
			String path = ".//decl_stmt[./decl/name='"
					+ variableName.getTextContent() + "']";
			declNode = AnalyzerUtils.getNode(parentNode, path);
			if (declNode != null || parentNode.getNodeName().equals("function"))
				break;
			parentNode = parentNode.getParentNode();
		}
		return declNode;
	}

	public static Set<String> getImportedPackages(Node node) {

		Set<String> importedPackageNames = new HashSet<String>();
		Node fileUnit = getFileNode(node);
		if (fileUnit == null)
			System.out.println("The node is out of unit node");
		if (fileUnit instanceof Element) {
			Element packages = (Element) fileUnit;
			NodeList packageNames = packages.getElementsByTagName("import");
			for (int i = 0; i < packageNames.getLength(); i++) {
				String packageName = "";
				Node tmp = packageNames.item(i);
				Node tmp_1 = tmp.getFirstChild().getNextSibling();
				if (!tmp_1.getTextContent().startsWith(Constants.packageStart))
					continue;
				NodeList tmp_2 = tmp_1.getChildNodes();
				for (int j = 0; j < tmp_2.getLength(); j++) {
					Node tmp_3 = tmp_2.item(j);
					String name = tmp_3.getTextContent();
					if ((name.charAt(0) >= 'A' && name.charAt(0) <= 'Z')
							|| name.equals("*"))
						break;
					packageName = packageName + name;
				}
				importedPackageNames.add(packageName);
			}

			// add current package name
			NodeList currentPackageNode = packages
					.getElementsByTagName("package");
			if (currentPackageNode.getLength() != 1) {
				System.err.println("There is two package names in this file");
			} else {
				Node currentPackageNameNode = currentPackageNode.item(0)
						.getFirstChild().getNextSibling();
				String currentPackageName = currentPackageNameNode
						.getTextContent();
				importedPackageNames.add(currentPackageName + ".");
			}

		}
		return importedPackageNames;

	}

	public static String getPackageOfExternalClass(Node instanceVariable) {
		String mathchedPackage = null;
		Set<String> packageNames = getImportedPackages(instanceVariable);
		HashMap<String, Node> allClasses = getAllClasses();
		String className = instanceVariable.getTextContent();

		Iterator iter = packageNames.iterator();
		while (iter.hasNext()) {
			String packageName = (String) iter.next();
			String fullClassName = packageName + className;
			if (allClasses.containsKey(fullClassName)) {
				mathchedPackage = packageName;
				break;
			}
		}
		return mathchedPackage;
	}

	public static String getPackageOfExternalClass(Node instanceVariable,
			String instanceType) {
		String mathchedPackage = null;
		Set<String> packageNames = getImportedPackages(instanceVariable);
		HashMap<String, Node> allClasses = getAllClasses();

		Iterator iter = packageNames.iterator();
		while (iter.hasNext()) {
			String packageName = (String) iter.next();
			String fullClassName = packageName + instanceType;
			if (allClasses.containsKey(fullClassName)) {
				mathchedPackage = packageName;
				break;
			}
		}
		return mathchedPackage;
	}

	public static Node locateClassNode(String packageName, String className)
			throws XPathExpressionException {
		String classFileName = packageName + className + ".java";
		String classFileNameML = getFileNameML(classFileName);

		String path = "unit/unit[@filename='" + classFileNameML
				+ "']/class[./name='" + className + "']";
		XPathExpression xExpression = xpath.compile(path);
		Node node = (Node) xExpression.evaluate(document, XPathConstants.NODE);
		
		return node;

	}

	public static NodeList getInstanceVariableDecleration(String packageName,
			String className, String variableName)
			throws XPathExpressionException {
		Node classNode = locateClassNode(packageName, className);	
		String path = "./block/decl_stmt[./decl/name='" + variableName + "']";
		NodeList declNode = AnalyzerUtils.getNodeList(classNode, path);
		
		return declNode;
	}

	public static void loadXMLDoc(String path)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		document = docBuilder.parse(new File(path));
	}

//	public static String getFullClassName(Node variableNode, String className) {
//		String fullClassName = null;
//
//		Set<String> packageNames = AnalyzerUtils
//				.getImportedPackages(variableNode);
//		HashMap<String, Node> allClasses = getAllClasses();
//
//		Iterator iter = packageNames.iterator();
//		while (iter.hasNext()) {
//			String packageName = (String) iter.next();
//			fullClassName = packageName + className;
//			if (allClasses.containsKey(fullClassName)) {
//				break;
//			}
//		}
//		return fullClassName;
//	}

	public static Node getClassNode(String fullClassName) {
		//very important to format the class names before retrieving
		String className=Utils.removeSpaceLineBreaks(fullClassName);
		HashMap<String, Node> allClasses = getAllClasses();
		return allClasses.get(className);
	}

	public static List<Node> getChildNodeByTagName(Node node, String tagName) {
		List<Node> set = new ArrayList<Node>();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(tagName))
				set.add(children.item(i));
		}
		return set;
	}

	public static String getFileName(String fileName) {
		String packageName = "";
		String[] tmp = fileName.split("\\\\");
		for (int i = 2; i < tmp.length - 1; i++)
			packageName = packageName + tmp[i] + ".";
		packageName = packageName + tmp[tmp.length - 1];
		return packageName;
	}

	public static NodeList getSubClasses(String superClass)
			throws XPathExpressionException {

		String[] tmp = superClass.split("\\.");
		String packageName = tmp[0];
		for (int i = 1; i < tmp.length - 1; i++) {
			packageName = packageName + "." + tmp[i];
		}
		String className = tmp[tmp.length - 1];

		String xpath = "//class[./super/extends/name[text()='" + className
				+ "']]";
		NodeList subClassNodes = AnalyzerUtils.getNodeList(document, xpath);
		return subClassNodes;
	}

	public static NodeList getVariablesOfConfClass(String fullClassName)
			throws XPathExpressionException {
		String[] tmp = fullClassName.split("\\.");
		String packageName = tmp[0];
		for (int i = 1; i < tmp.length - 1; i++) {
			packageName = packageName + "." + tmp[i];
		}
		String className = tmp[tmp.length - 1];

		String xpath = "//decl[./type/name[text()='" + className + "']|./type/name/name[last()][text()='" + className + "']]";
		NodeList variables = AnalyzerUtils.getNodeList(document, xpath);

		return variables;

	}

	public static Node getScope(Node declStmt) {
		Node parent = null;
		Node temp = declStmt.getParentNode();
		while (!temp.getNodeName().equals("class")
				&& !temp.getNodeName().equals("interface")) {
			String name = temp.getNodeName();
			if (name.equals("block") || name.equals("for")
					|| name.equals("while")) {
				parent = temp;
				break;
			}
			temp = temp.getParentNode();
		}
		return parent;
	}

	public static String getFileName(Node node) {
		Node file = getFileNode(node);
		String fileName = file.getAttributes().getNamedItem("filename")
				.getNodeValue();
		return getFileName(fileName);
	}

	public static void printNode(Node node) {
		String fileName = getFileName(node);
		Element e = (Element) node;
		String lineNum = getLineNum(node);
		System.out.println(fileName + ": " + lineNum + "---"
				+ node.getTextContent());
	}

	public static void outFile(Set nodes) throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("./data/common/ORPs.csv", "UTF-8");
		int i = 1;
		Iterator<Node> iter = nodes.iterator();
		while (iter.hasNext()) {
			Node tmp = iter.next();
			String fileName = getFileName(tmp);
			String lineNum = getLineNum(tmp);
			String getters = tmp.getTextContent();
			getters = getters.replaceAll(",", " ");
			getters = getters.replaceAll("\\r|\\n", "");
			writer.println(i + "," + getters + "," + fileName + "," + lineNum);
			writer.flush();

			PrintWriter writerFiles = new PrintWriter("./data/common/files/"
					+ i + ".txt");
			writerFiles.write(AnalyzerUtils.getFileNode(tmp).getTextContent());
			writerFiles.flush();
			writerFiles.close();
			i++;
		}
		writer.close();

	}

	public static String getLineNum(Node node) {
		Element e = (Element) node;
		String lineNum = "";
		if (e.hasAttribute("pos:line"))
			lineNum = e.getAttribute("pos:line");
		else {
			while (e.hasChildNodes()) {
				Element tmp = (Element) e.getFirstChild();
				if (tmp.hasAttribute("pos:line")) {
					lineNum = tmp.getAttribute("pos:line");
					break;
				}
				e = tmp;
			}
		}
		return lineNum;
	}

	public static List<Node> getParameters(Node callSites) {


		ArrayList<Node> paras = new ArrayList<Node>();
		List<Node> argList = AnalyzerUtils.getChildNodeByTagName(callSites,
				"argument_list");
		if (argList.size() == 0)
		{
			System.out.println("No paras of this call sites!");
			return null;
		}
		Iterator<Node> iter = argList.iterator();
		List<Node> args=null;
		if(iter.hasNext()){
			args = AnalyzerUtils.getChildNodeByTagName(iter.next(),
					"argument");
			paras.addAll(args);
		
		}
		
		return args;

	}
	public static NodeList getNodeByLineNum(String fileName, int lineNum, String nodeName) throws XPathExpressionException{
		String path = "unit/unit[@filename='" + fileName+ "']//"+nodeName+"[.//name[@ line='" + lineNum + "']]";
		return AnalyzerUtils.getNodeList(document, path);
	}
	public static String getClassName(Node classNode){
		List<Node> nameNodes=getChildNodeByTagName(classNode, "name");
		Node nameNode=nameNodes.iterator().next();
		return nameNode.getFirstChild().getTextContent();
	}
}
