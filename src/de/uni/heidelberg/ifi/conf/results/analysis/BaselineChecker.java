package de.uni.heidelberg.ifi.conf.results.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import de.uni.heidelberg.ifi.conf.input.LoadOptions;

public class BaselineChecker {

	public  Set<String> confOptions=new HashSet<String>();
	public HashSet<Callsites> orp=new HashSet<Callsites>();
	public HashSet<Callsites> orpOfPublishedOptions=new HashSet<Callsites>();
	public List defaultOptionList=null;

	public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		// TODO Auto-generated method stub
		BaselineChecker bc=new BaselineChecker();
		bc.defaultOptionList=LoadOptions.yarn;
		LoadOptions.init();
		bc.loadBaselineResults();
		Iterator<String> iter=bc.confOptions.iterator();
		int num=0;
		while(iter.hasNext()){
			String tmp=iter.next();
			if(LoadOptions.core.contains(tmp))
				num++;
		}
		System.out.println("detected: "+num);

	}

	
	public  void loadBaselineResults() throws IOException{
		
		
		for (int j = 1; j < 26; j++) {
			
			String path = "./baseline_data/Common/entry"+j+"_output/";

			String files;
			File folder = new File(path);

			File[] listOfFiles = folder.listFiles();
			if(listOfFiles==null)
				continue;
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					files = listOfFiles[i].getName();
					if (files.equals("conf_regex.txt")) {
//						File tmp=listOfFiles[i];
						BufferedReader tmp=new BufferedReader(new FileReader(listOfFiles[i]));
						String line="";
						while((line=tmp.readLine())!=null)
						{
							if(line.startsWith("CONF-"))
							{
								String[] strs=line.split(" ");
								String option=strs[0];
								String classfileName=strs[3];
								String methodName=strs[4];
								String[] s=methodName.split(":");
								int lineNum=Integer.parseInt(s[1]);
								
								orp.add(new BaselineChecker.Callsites(classfileName,lineNum));
								
								option=option.replace("CONF-", "");
								if(option.contains("*")||option.contains("|"))
									continue;
								if(LoadOptions.core.contains(option))
									orpOfPublishedOptions.add(new BaselineChecker.Callsites(classfileName,lineNum));
//								System.out.println(option);
								confOptions.add(option);
								
							}
						}
						
						
					}
				}
			}
			
		}
		System.out.println("number of sites: "+ orp.size());
		System.out.println("number of sites of published options: "+ orpOfPublishedOptions.size());
		System.out.println("number of options: "+confOptions.size());
	}
	
	
	
	private class Callsites{
		private String className;
		private int lineNum;
		public Callsites(String className, int lineNum) {
			this.className = className;
			this.lineNum = lineNum;
		}
		
		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public int getLineNum() {
			return lineNum;
		}

		public void setLineNum(int lineNum) {
			this.lineNum = lineNum;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((className == null) ? 0 : className.hashCode());
			result = prime * result + lineNum;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Callsites other = (Callsites) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (className == null) {
				if (other.className != null)
					return false;
			} else if (!className.equals(other.className))
				return false;
			if (lineNum != other.lineNum)
				return false;
			return true;
		}
		private BaselineChecker getOuterType() {
			return BaselineChecker.this;
		}
		
		
	}
	

}
