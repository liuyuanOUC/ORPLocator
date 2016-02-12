package de.uni.heidelberg.ifi.conf.utils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Zhen Dong from Heidelberg University 
 * created on July 27, 2015
 *
 */
public class LineNumCounter {
	private boolean flag = false;
	private int lineNum = 0;

	public static LineNumCounter getInstance(){
		LineNumCounter counter=new LineNumCounter();
		return counter;
	}
	// fileNode: the node corresponds a class file and is the ancestor node of the specified node 
	public int getLineNum(Node fileNode, Node specifiedNode){
		findNode(fileNode,specifiedNode);
		return lineNum+1;
	}
	
	public boolean findNode(Node node, Node specifiedNode) {

		if (!flag) {
			if (node.hasChildNodes()) {
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					findNode(children.item(i), specifiedNode);
					if (children.item(i).isSameNode(specifiedNode))
						flag = true;
				}
			}

			if (node.getNodeType() == Node.TEXT_NODE) {
				lineNum = lineNum
						+ StringUtils.countMatches(node.getTextContent(), "\n");

			}
		}
		return flag;

	}
}
