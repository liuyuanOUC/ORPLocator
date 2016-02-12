package de.uni.heidelberg.ifi.conf.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;

public class CountLine {


	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	    public static void main(String[] args) throws IOException {
	        String allInput = readFile("./data/text.txt", StandardCharsets.UTF_8);
	        System.out.println(allInput);
	        System.out.println(StringUtils.countMatches(allInput, "\n"));
	    }

	
}
