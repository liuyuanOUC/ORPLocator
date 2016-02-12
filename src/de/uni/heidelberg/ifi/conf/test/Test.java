package de.uni.heidelberg.ifi.conf.test;

import javax.xml.xpath.XPathExpressionException;

import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;

public class Test {
	public static void main(String[] args) throws XPathExpressionException{
		AnalyzerUtils.getSubClasses("org.apache.hadoop.conf.Configuration");
	}
}
