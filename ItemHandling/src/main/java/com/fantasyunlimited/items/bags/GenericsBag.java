package com.fantasyunlimited.items.bags;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.fantasyunlimited.items.entity.GenericItem;
import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.PostConstruct;

public abstract class GenericsBag<T extends GenericItem> {
	@Autowired
	private XStream xstream;
	@Autowired
	private ResourceLoader resourceLoader;

	private final String rootfolder;

	private Map<String, T> items = new HashMap<>();
	private Map<String, T> itemsByName = new HashMap<>();

	public GenericsBag(String rootfolder) {
		this.rootfolder = rootfolder;
	}

	@PostConstruct
	public void initialize() throws InitializationException, IOException, URISyntaxException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Arrays.stream(resourcePatternResolver.getResources("/" + rootfolder + "/**/*.xml"))
				.filter(Resource::exists)
				.map(resource -> {
					try {
						return resource.getInputStream();
					} catch (IOException e) {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.forEach(stream -> {
					T item = (T) xstream.fromXML(stream);

					if (items.containsKey(item.getId())) {
						throw new InitializationException("Item Id" + item.getId() + " already in use!");
					}

					items.put(item.getId(), item);
					itemsByName.put(item.getName(), item);
				});

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
