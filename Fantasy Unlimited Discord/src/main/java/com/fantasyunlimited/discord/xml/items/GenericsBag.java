package com.fantasyunlimited.discord.xml.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fantasyunlimited.discord.xml.GenericItem;
import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;

public abstract class GenericsBag<T extends GenericItem> {
	private final String rootfolder;

	private Map<String, T> items = new HashMap<>();
	private Map<String, T> itemsByName = new HashMap<>();

	public GenericsBag(String rootfolder) {
		this.rootfolder = rootfolder;
	}

	public void initialize(XStream xstream) throws InitializationException, IOException, URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(rootfolder);

		for (Path path : listFiles(Paths.get(url.toURI()))) {

			File file = path.toFile();
			FileInputStream stream = new FileInputStream(file);
			@SuppressWarnings("unchecked")
			T item = (T) xstream.fromXML(stream);

			if (items.containsKey(item.getId())) {
				throw new InitializationException("Item Id" + item.getId() + " already in use!");
			}

			items.put(item.getId(), item);
			itemsByName.put(item.getName(), item);
		}

		for (T item : items.values()) {
			try {
				passSanityChecks(item);
			} catch (SanityException e) {
				throw new InitializationException("Item Id " + item.getId() + " (" + item.getClass().getName() + ")"
						+ " didn't pass sanity checks.", e);
			}
		}
	}

	public abstract boolean passSanityChecks(T item) throws SanityException;

	public Collection<T> getItems() {
		return items.values();
	}

	public T getItem(String id) {
		return items.get(id);
	}

	public Collection<T> getItemsByValue(String value) {
		Collection<T> items = getItemsById(value);
		if (items == null || items.isEmpty()) {
			items = getItemsByName(value);
		}
		return items;
	}

	public Collection<T> getItemsByName(String name) {
		return itemsByName.values().stream().filter(item -> item.getName().toLowerCase().contains(name.toLowerCase()))
				.collect(Collectors.toList());
	}

	public Collection<T> getItemsById(String id) {
		return itemsByName.values().stream().filter(item -> item.getId().toLowerCase().contains(id.toLowerCase()))
				.collect(Collectors.toList());
	}

	private List<Path> listFiles(Path path) throws IOException {
		List<Path> all = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					all.addAll(listFiles(entry));
				} else {
					all.add(entry);
				}
			}
		}
		return all;
	}

	public Map<String, T> getItemsByName() {
		return itemsByName;
	}

	public void setItemsByName(Map<String, T> itemsByName) {
		this.itemsByName = itemsByName;
	}
}
