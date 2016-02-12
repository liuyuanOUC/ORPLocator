package de.uni.heidelberg.ifi.conf.taintAnalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni.heidelberg.ifi.conf.Constant.Constants;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;
import de.uni.heidelberg.ifi.conf.utils.Utils;

public class InferSuperClasses {

	private LinkedList<Node> workList = new LinkedList();
	private Set<String> superClasses = new HashSet<String>();

	public Set<String> getSuperClasses(Node classNode) {

		HashMap<String, Node> allClasses = AnalyzerUtils.getAllClasses();
		workList.add(classNode);
		while (!workList.isEmpty()) {
			Node node = workList.remove();
			List<Node> nodeList = AnalyzerUtils.getChildNodeByTagName(node,
					"super");
			if (nodeList.size() == 0)
				return superClasses;
			if (nodeList.size() > 1)
				System.err.println("Wrong inheritance relationships!");
			NodeList extendsAndImplementsNodes = nodeList.iterator().next()
					.getChildNodes();
			for (int i = 0; i < extendsAndImplementsNodes.getLength(); i++) {
				String fullName = "";
				Node nodeExtendImplement = extendsAndImplementsNodes.item(i);
				List<Node> names = AnalyzerUtils.getChildNodeByTagName(
						nodeExtendImplement, "name");
				Iterator iter = names.iterator();
				while (iter.hasNext()) {
					Node n = (Node) iter.next();
					String nameWithoutArgs = Utils.removeArgs(n
							.getTextContent());
					if (nameWithoutArgs.startsWith(Constants.packageStart)) {
						fullName = nameWithoutArgs;
						if (allClasses.containsKey(fullName)) {
							workList.add(allClasses.get(fullName));
							superClasses.add(fullName);
						}

					} else {
						if(isStartWithPackageName(Utils.getFirstPart(nameWithoutArgs),nodeExtendImplement))
							continue;
						
						String name = Utils.getLastPart(nameWithoutArgs);
						Set<String> importedPackageNames = AnalyzerUtils
								.getImportedPackages(nodeExtendImplement);
						Iterator iterString = importedPackageNames.iterator();
						while (iterString.hasNext()) {

							fullName = (String) iterString.next() + name;
							if (allClasses.containsKey(fullName)) {
								workList.add(allClasses.get(fullName));
								superClasses.add(fullName);
							}

						}
					}

				}

			}

		}

		return superClasses;
	}

	public boolean isStartWithPackageName(String firstPart,
			Node nodeExtendImplement) {

		Set<String> importedPackageNames = AnalyzerUtils
				.getImportedPackages(nodeExtendImplement);
		Iterator iterString = importedPackageNames.iterator();
		while (iterString.hasNext()) {
			HashMap<String, Node> allClasses = AnalyzerUtils.getAllClasses();
			String fullName = (String) iterString.next() + firstPart;
			if (allClasses.containsKey(fullName)) {
				return false;
			}
		}
		return true;
	}
}
