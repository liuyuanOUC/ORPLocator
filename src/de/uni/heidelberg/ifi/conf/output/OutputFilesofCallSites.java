package de.uni.heidelberg.ifi.conf.output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Node;

import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;

public  class OutputFilesofCallSites {
	public static void outputFilesofCallSites(Set nodes, String path) throws FileNotFoundException, UnsupportedEncodingException{
		int i=1;
		Iterator<Node> iter=nodes.iterator();
		while(iter.hasNext()){
			PrintWriter writer = new PrintWriter(path+i+".txt", "UTF-8");
			Node node=iter.next();
			String file=AnalyzerUtils.getFileNode(node).getTextContent();
			writer.println(file);
			writer.flush();
			writer.close();
			i++;
		}
		
	}
	
}
