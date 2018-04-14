package com.fantasyunlimited.discord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.fantasyunlimited.discord.xml.GenericItem;
import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;

public abstract class GenericsBag<T extends GenericItem> {
	private final String rootfolder;

	private List<T> items = new ArrayList<>();
	private List<String> usedIds = new ArrayList<>();

	public GenericsBag(String rootfolder) {
		this.rootfolder = rootfolder;
	}

	public void initialize(XStream xstream) throws InitializationException, IOException {
		
		for(String filename: getResourceFiles(rootfolder)) {
			@SuppressWarnings("unchecked")
			T item = (T) xstream.fromXML(getResourceAsStream(rootfolder + "/" + filename));
			
			if(usedIds.contains(item.getId())) {
				throw new InitializationException("Item Id" + item.getId() + " already in use!");
			}
			
			if(!passSanityChecks(item)) {
				throw new InitializationException("Item Id " + item.getId() + " (" + item.getClass().getName() + ")" + " didn't pass sanity checks.");
			}
			
			items.add(item);
			usedIds.add(item.getId());
		}
	}
	
	public abstract boolean passSanityChecks(T item);
	
	public List<T> getItems() {
		return items;
	}

	private List<String> getResourceFiles(String path) throws IOException {
		List<String> filenames = new ArrayList<>();

		try (InputStream in = getResourceAsStream(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String resource;

			while ((resource = br.readLine()) != null) {
				filenames.add(resource);
			}
		}

		return filenames;
	}

	private InputStream getResourceAsStream(String resource) {
		final InputStream in = getContextClassLoader().getResourceAsStream(resource);

		return in == null ? getClass().getResourceAsStream(resource) : in;
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
