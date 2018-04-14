package com.fantasyunlimited.discord;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;

public abstract class GenericsBag<T> {
	private final String rootfolder;
	
	private List<T> items = new ArrayList<>();
	private List<String> usedIds = new ArrayList<>();
	
	public GenericsBag(String rootfolder) {
		this.rootfolder = rootfolder;
	}
	
	public void initialize(XStream xstream) throws InitializationException {
		//TODO: open root folder, get all XML files, traverse and add to list
		//also TODO: sanity checks for same IDs and such
	}
	
	public List<T> getItems() {
		return items;
	}
}
