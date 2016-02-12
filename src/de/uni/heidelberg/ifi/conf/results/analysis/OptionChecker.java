package de.uni.heidelberg.ifi.conf.results.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import de.uni.heidelberg.ifi.conf.input.LoadOptions;
import de.uni.heidelberg.ifi.conf.utils.Utils;

public class OptionChecker {

	public static Set commonLocated;
	public static Set hdfsLocated;
	public static Set mapreduceLocated;
	public static Set yarnLocated;
	
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		PrintWriter writer = new PrintWriter("./data/hadoop/m.csv", "UTF-8");
		LoadOptions.init();
		initOptionLists();
		List<String> core=LoadOptions.yarn;
		
		Iterator<String> iter=core.iterator();
		while(iter.hasNext()){
			String option=iter.next();
			boolean isCore=commonLocated.contains(option);
			boolean isHdfs=hdfsLocated.contains(option);
			boolean isMapred=mapreduceLocated.contains(option);
			boolean isYarn=yarnLocated.contains(option);
			boolean isLocated=isCore||isHdfs||isMapred||isYarn;
			
			String line=option+","+isLocated+","+isCore+","+isHdfs+","+isMapred+","+isYarn;
			writer.println(line);
		}
		writer.flush();
		writer.close();
	}
	
	public static Set readCSVFile(String path) throws IOException{
		BufferedReader bf= new BufferedReader(new FileReader(path));
		String line="";
		Set options=new HashSet();
		while((line=bf.readLine())!=null){
			String[] content=line.split(",");
			if(content.length<6)
				continue;
			String option=content[5];
			option = Utils.removeSpaceLineBreaks(option);
			options.add(option);
		}
		
		bf.close();
		return options;
	}
	public static void initOptionLists() throws IOException{
		commonLocated=readCSVFile("./data/hadoop/commonInfered.csv");
		//remove the title
		System.out.println("Common: "+ (commonLocated.size()-1));
		hdfsLocated=readCSVFile("./data/hadoop/hdfsInfered.csv");
		System.out.println("HDFS: "+(hdfsLocated.size()-1));
		mapreduceLocated=readCSVFile("./data/hadoop/mapreduceInfered.csv");
		System.out.println("Mapreduce: "+ (mapreduceLocated.size()-1));
		yarnLocated=readCSVFile("./data/hadoop/yarnInfered.csv");
		//remove the title and add an option in the second argument
		System.out.println("Yarn: "+ (yarnLocated.size()-1+1));
		
	}
	
}
