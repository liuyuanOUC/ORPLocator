package de.uni.heidelberg.ifi.conf.output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

import de.uni.heidelberg.ifi.conf.elements.Callsite_OptionName;
import de.uni.heidelberg.ifi.conf.input.LoadOptions;
import de.uni.heidelberg.ifi.conf.utils.AnalyzerUtils;

public class OutOptions {
	public static void outFile(List callSite_Options)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("./data/yarn/ORPs.csv", "UTF-8");
		int i = 1;
		Iterator<Callsite_OptionName> iter = callSite_Options.iterator();
		while (iter.hasNext()) {
			String optionValue="";
			int in=0;
			
			Callsite_OptionName co = iter.next();
			Node tmp = co.getCallsite();
			String fileName = AnalyzerUtils.getFileName(tmp);
			String lineNum = AnalyzerUtils.getLineNum(tmp);
			String getters = tmp.getTextContent();
			getters = getters.replaceAll(",", " ");
			getters = getters.replaceAll("\\r|\\n", "");
			
			Iterator<String> options=co.getOptionNames().iterator();
			while(options.hasNext()){
				String name=options.next();
				int type=0;
				if(name!=null&&name!=""){
					String nameNoquote=name.replace("\"", "");
					type=LoadOptions.isConfFile(nameNoquote);
					optionValue=optionValue+","+nameNoquote+","+type;
				}
				if(type!=0)
				in=1;
			}

			writer.println(i + "," + getters + "," + fileName + "," + lineNum+","+in+optionValue);
			writer.flush();
			i++;
		}
		writer.close();

	}

}
