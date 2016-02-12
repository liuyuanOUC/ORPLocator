package de.uni.heidelberg.ifi.conf.locateORPs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni.heidelberg.ifi.conf.input.LoadOptions;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;
import de.uni.heidelberg.ifi.conf.utils.LineNumCounter;

public class LocateORPs {

	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		LoadOptions.init();
		System.out.println("core: "+ LoadOptions.core.size());
		System.out.println("hdfs: "+ LoadOptions.hdfs.size());
		System.out.println("mapreduce: "+ LoadOptions.mapreduce.size());
		System.out.println("yarn: "+ LoadOptions.yarn.size());
	}
	

}
