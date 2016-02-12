package de.uni.heidelberg.ifi.conf.locateORPs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni.heidelberg.ifi.conf.Constant.Constants;
import de.uni.heidelberg.ifi.conf.taintAnalyzer.InferSuperClasses;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;
import de.uni.heidelberg.ifi.conf.utils.Utils;

public class ParameterInfer {

	private int category = 1;
	private String compoundName = "";
	public static HashMap<String, Node> cacheMap = new HashMap<String, Node>();

	public String getValueFromExpr(Node expression)
			throws XPathExpressionException {
		Node expr = null;
		List<Node> oprands = new ArrayList<Node>();
		if (expression == null) {
			compoundName = null;
			return null;
		}
		if (expression.getNodeName().equals("decl_stmt")) {
			expr = getExpressionFromDecl(expression);
			if (expr == null)
				return null;
		} else
			expr = expression;
		System.out.println("expression: " + expr.getTextContent());
		List<Node> elements = Utils.removeWhileSpaceNode(expr.getChildNodes());
		Iterator<Node> iter = elements.iterator();
		int i = 0;
		while (iter.hasNext()) {
			Node n = iter.next();

			if (i % 2 == 0) {
				Element oprand = (Element) n;
				System.out.println("operand: " + n.getTextContent());
				if (!oprand.getNodeName().equals("name")
						&& !oprand.getNodeName().equals("literal")) {
					oprands.clear();
					compoundName = null;
					break;
				}
				if (oprand.getNodeName().equals("name")) {
					List<Node> parts = new ArrayList<Node>();
					if (oprand.getChildNodes().getLength() == 2)
						parts.add(oprand);
					else
						parts = Utils.removeWhileSpaceNode(oprand
								.getChildNodes());
					System.out.println("length:" + parts.size());
					if (parts.size() <= 3) {
						getValueFromExpr(findDecl(parts));
					}
					if (parts.size() > 3) {
						getValueFromExpr(findDeclWithFullName(oprand));
					}

				}
				if (oprand.getNodeName().equals("literal")
						&& oprand.getAttribute("type").equals("string"))
					compoundName = compoundName + oprand.getTextContent();
			} else {
				Node oper = n;
				if (!oper.getTextContent().equals("+")) {
					oprands.clear();
					compoundName = null;
					break;
				}

			}
			i++;
		}

		return compoundName;

	}

	public Node getExpressionFromDecl(Node decl)
			throws XPathExpressionException {
		String path = "./decl/init/expr";
		NodeList list = AnalyzerUtils.getNodeList(decl, path);
		if (list.getLength() == 0)
			return null;
		if (list.getLength() > 1) {
			System.err.println("Wrong declaration!");
			return null;
		}
		return list.item(0);
	}

	public Node findDecl(Node variableName) throws XPathExpressionException {

		Node decl = null;
		// The variable is a variable of an instance or class
		if (AnalyzerUtils.isClassVariable(variableName)) {

			Node objectVariable = (variableName.getPreviousSibling())
					.getPreviousSibling();
			// The variable is an instance variable
			if (objectVariable.getTextContent().equals("this")) {

				decl = getDeclofInstanceVar(
						AnalyzerUtils.getClass(variableName),
						variableName.getTextContent());

			} else {
				// The variable is an instance variable

				Node declNode = AnalyzerUtils
						.searchDeclsFromFuction(objectVariable);
				NodeList declField = AnalyzerUtils
						.searchDeclFromFields(objectVariable);
				if (declNode != null || declField.getLength() > 0) {
					Node declInstanceVariable = null;
					if (declNode != null)
						declInstanceVariable = declNode;
					else
						declInstanceVariable = declField.item(0);
					this.category = 2;
					decl = getDeclofInstanceVar(
							findClassOfInstance(declInstanceVariable),
							variableName.getTextContent());
				} else {
					String packageName = AnalyzerUtils
							.getPackageOfExternalClass(objectVariable);
					System.out.println("package " + packageName);
					if ((packageName != null)) {
						this.category = 3;
						String fullClassName = packageName
								+ objectVariable.getTextContent();
						decl = getDeclofInstanceVar(
								AnalyzerUtils.getClassNode(fullClassName),
								variableName.getTextContent());
					}
				}

			}

		} else {
			decl = AnalyzerUtils.searchDeclsFromFuction(variableName);
			if (decl == null) {
				decl = getDeclofInstanceVar(
						AnalyzerUtils.getClass(variableName),
						variableName.getTextContent());
				if (decl == null) {
					String fullClassName = getFullClassNameForImportedVariable(variableName);
					System.out.println("It is an imported variable        "
							+ fullClassName);
					if (fullClassName != null)
						decl = getDeclofInstanceVar(
								AnalyzerUtils.getClassNode(fullClassName),
								variableName.getTextContent());
				}

			}
		}

		return decl;
	}

	public Node findDecl(List<Node> variableName)
			throws XPathExpressionException {

		Node decl = null;
		int size = variableName.size();
		switch (size) {
		case 3: {
			Node instanceVar = variableName.get(0);
			Node referSyl = variableName.get(1);
			Node var = variableName.get(2);
			if (instanceVar.getNodeName().equals("name")
					&& Utils.removeSpaceLineBreaks(referSyl.getTextContent())
							.equals(".") && var.getNodeName().equals("name")) {
				// The variable is an instance variable
				if (Utils.removeSpaceLineBreaks(instanceVar.getTextContent())
						.equals("this")) {

					decl = getDeclofInstanceVar(
							AnalyzerUtils.getClass(instanceVar),
							Utils.removeSpaceLineBreaks(var.getTextContent()));

				} else {
					// The variable is an instance variable

					Node declNode = AnalyzerUtils
							.searchDeclsFromFuction(instanceVar);
					NodeList declField = AnalyzerUtils
							.searchDeclFromFields(instanceVar);
					if (declNode != null || declField.getLength() > 0) {
						Node declInstanceVariable = null;
						if (declNode != null)
							declInstanceVariable = declNode;
						else
							declInstanceVariable = declField.item(0);
						this.category = 2;
						decl = getDeclofInstanceVar(
								findClassOfInstance(declInstanceVariable),
								Utils.removeSpaceLineBreaks(var
										.getTextContent()));
					} else {
						String packageName = AnalyzerUtils
								.getPackageOfExternalClass(instanceVar);
						System.out.println("package " + packageName);
						if ((packageName != null)) {
							this.category = 3;

							String fullClassName = packageName
									+ Utils.removeSpaceLineBreaks(instanceVar
											.getTextContent());
							
							//cache the class nodes located
							Node externalClass=cacheMap.get(fullClassName);
							if(externalClass==null)
							{
								 externalClass = AnalyzerUtils
										.getClassNode(fullClassName);
								 if(externalClass!=null)
									 cacheMap.put(fullClassName, externalClass);
							}
								

							decl = getDeclofInstanceVar(externalClass,
									Utils.removeSpaceLineBreaks(var
											.getTextContent()));
						}
					}

				}
			}
			break;
		}
		case 1: {
			Node var = variableName.get(0);
			decl = AnalyzerUtils.searchDeclsFromFuction(var);
			if (decl == null) {
				decl = getDeclofInstanceVar(AnalyzerUtils.getClass(var),
						Utils.removeSpaceLineBreaks(var.getTextContent()));
				if (decl == null) {
					
					String fullClassName = getFullClassNameForImportedVariable(var);
					System.out.println("It is an imported variable        "
							+ fullClassName);
					if (fullClassName != null){
						
						Node externalClass=cacheMap.get(fullClassName);
						if(externalClass==null)
						{
							 externalClass = AnalyzerUtils
									.getClassNode(fullClassName);
							 if(externalClass!=null)
								 cacheMap.put(fullClassName, externalClass);
						}
						decl = getDeclofInstanceVar(externalClass,
								Utils.removeSpaceLineBreaks(var
										.getTextContent()));
					}
				}

			}
		}
			break;
		case 2: {
			System.err.println("the oprand's length is two!");
		}
		}

		return decl;
	}

	public Node findClassOfInstance(Node declInstanceVarNode)
			throws XPathExpressionException, DOMException {

		String instanceType = getVariableType(declInstanceVarNode);
		String packageName = AnalyzerUtils.getPackageOfExternalClass(
				declInstanceVarNode, instanceType);
		if ((packageName != null)) {
			return AnalyzerUtils.getClassNode(packageName + instanceType);
		}

		return null;
	}

	public Node getDeclofInstanceVar(Node classNode, String variableName)
			throws XPathExpressionException {
		if (classNode == null)
			return null;
		String path = "./block/decl_stmt[./decl/name='" + variableName + "']";
		Node declNode =  AnalyzerUtils.getNode(classNode, path);
			if (declNode == null) {
				InferSuperClasses inferClass = new InferSuperClasses();
				Set<String> superClasses = inferClass
						.getSuperClasses(classNode);
				Iterator<String> iter = superClasses.iterator();
				while (iter.hasNext()) {
					String fullClassName = iter.next();
					System.out.println("Class names: " + fullClassName);
					Node superClass = AnalyzerUtils.getClassNode(fullClassName);
					declNode = getDeclofInstanceVar(superClass, variableName);
					if (declNode != null)
						break;

				}
			}

		return declNode;
	}

	public String getVariableType(Node declNode) {
		String typeName = null;
		Node type = declNode.getFirstChild().getFirstChild();

		NodeList name = type.getChildNodes();
		for (int i = 0; i < name.getLength(); i++) {
			Node tmp = name.item(i);
			if (tmp.getNodeName().equals("name")) {
				typeName = tmp.getTextContent();

			}

		}
		return typeName;

	}

	public String getFullClassNameForImportedVariable(Node variable) {
		String className = null;
		Node fileUnit = AnalyzerUtils.getFileNode(variable);
		String variableName = variable.getTextContent();
		variableName = Utils.removeSpaceLineBreaks(variableName);
		if (fileUnit == null)
			System.out.println("The node is out of unit node");
		if (fileUnit instanceof Element) {
			Element packages = (Element) fileUnit;
			NodeList packageNames = packages.getElementsByTagName("import");
			for (int i = 0; i < packageNames.getLength(); i++) {
				String packageName = "";
				Node tmp = packageNames.item(i);
				String packName = tmp.getFirstChild().getNextSibling()
						.getTextContent();
				if (!packName.startsWith(Constants.packageStart))
					continue;
				if (packName.endsWith("." + variableName)) {

					className = packName.substring(0, packName.length()
							- variableName.length() - 1);
					break;
				} else {
					if (packName.endsWith("." + "*"))
						className = packName
								.substring(0, packName.length() - 2);
				}
			}
		}
		return className;
	}

	public Node findDeclWithFullName(Node node) throws XPathExpressionException {

		Node decl = null;
		String str = node.getTextContent();
		str = Utils.removeSpaceLineBreaks(str);
		String[] s = str.split("\\.");
		String variableName = s[s.length - 1];
		String fullClassName = "";
		for (int i = 0; i < s.length - 2; i++) {
			fullClassName = fullClassName + s[i] + ".";
		}
		fullClassName = fullClassName + s[s.length - 2];
		Node classNode = AnalyzerUtils.getClassNode(fullClassName);
		if (classNode != null)
			decl = getDeclofInstanceVar(classNode, variableName);
		return decl;

	}

}
