package de.uni.heidelberg.ifi.conf.elements;

import java.util.List;

import org.w3c.dom.Node;

public class Callsite_OptionName {
	private Node callsite;
	private List optionNames;
	public Callsite_OptionName(Node callsite, List optionNames) {
		super();
		this.callsite = callsite;
		this.optionNames = optionNames;
	}
	public Node getCallsite() {
		return callsite;
	}
	public void setCallsite(Node callsite) {
		this.callsite = callsite;
	}
	public List getOptionNames() {
		return optionNames;
	}
	public void setOptionNames(List optionNames) {
		this.optionNames = optionNames;
	}
	
}
