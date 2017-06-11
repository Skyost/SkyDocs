package fr.skyost.skydocs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.SkyDocs;

public class Utils {
	
	public static final String LINE_SEPARATOR = System.lineSeparator();
	
	public static final File createFileIfNotExist(final File file) throws IOException {
		if(!file.exists()) {
			if(file.isDirectory()) {
				file.mkdirs();
			}
			else {
				file.createNewFile();
			}
		}
		return file;
	}
	
	public static final Integer parseInt(final String string) {
		try {
			return Integer.parseInt(string);
		}
		catch(final Exception ex) {}
		return null;
	}
	
	public static final File getJARFile() {
		try {
			return new File(SkyDocs.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the JAR parent folder.
	 * 
	 * @return The JAR parent folder.
	 */
	
	public static final File getParentFolder() {
		final File jar = Utils.getJARFile();
		if(jar == null) {
			return null;
		}
		return jar.getParentFile();
	}
	
	public static final String[] separateFileHeader(final File file) {
		try {
			final List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
			if(!lines.get(0).equals(Constants.HEADER_MARK)) {
				return new String[]{null, Utils.join(LINE_SEPARATOR, lines)};
			}
			int headerLimit = -1;
			for(int i = 0; i != lines.size(); i++) {
				if(i != 0 && lines.get(i).equals(Constants.HEADER_MARK)) {
					headerLimit = i;
					break;
				}
			}
			if(headerLimit == -1) {
				return new String[]{null, Utils.join(LINE_SEPARATOR, lines)};
			}
			
			return new String[]{
					Utils.join(LINE_SEPARATOR, lines.subList(1, headerLimit)),
					Utils.join(LINE_SEPARATOR, lines.subList(headerLimit + 1, lines.size()))};
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static final Map<String, Object> decodeFileHeader(final String header) {
		if(header == null) {
			return null;
		}
		final Yaml yaml = new Yaml();
		return (Map<String, Object>)yaml.load(header);
	}
	
	public static final void copyDirectory(final File directory, final File destination) throws IOException {
		if(directory.isFile()) {
			Files.copy(directory.toPath(), destination.toPath());
			return;
		}
		destination.mkdirs();
		for(final File file : directory.listFiles()) {
			copyDirectory(file, new File(destination, file.getName()));
		}
	}
	
	public static final void deleteDirectory(final File directory) {
		if(directory.isFile()) {
			directory.delete();
			return;
		}
		for(final File file : directory.listFiles()) {
			deleteDirectory(file);
		}
		directory.delete();
	}
	
	/**
	 * Extracts a file to the specified directory.
	 * 
	 * @param path Path of the file or directory.
	 * @param toExtract The file or directory to extract.
	 * @param destination The destination directory.
	 * 
	 * @throws IOException If an exception occurs when trying to extract the file.
	 * @throws URISyntaxException If an exception occurs when trying to locate the file on disk.
	 */
	
	public static final void extract(final String path, final String toExtract, File destination) throws IOException, URISyntaxException {
		final File appLocation = new File(SkyDocs.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		if(appLocation.isFile()) {
			final JarFile jar = new JarFile(appLocation);
			final Enumeration<JarEntry> enumEntries = jar.entries();
			
			while(enumEntries.hasMoreElements()) {
				final JarEntry file = (JarEntry)enumEntries.nextElement();
				if(!file.getName().endsWith(toExtract)) {
					continue;
				}
				final File directory = new File(destination, file.getName());
				if(file.isDirectory()) {
					directory.mkdirs();
					continue;
				}
				final InputStream is = jar.getInputStream(file);
				final FileOutputStream fos = new FileOutputStream(directory);
				while(is.available() > 0) {
					fos.write(is.read());
				}
				fos.close();
				is.close();
			}
			jar.close();
			return;
		}
		final File file = new File(appLocation.getPath() + File.separator + path + toExtract);
		if(!file.exists()) {
			throw new FileNotFoundException(file.getPath() + " not found.");
		}
		
		if(file.isFile()) {
			Files.copy(file.toPath(), (destination.isFile() ? destination : new File(destination, file.getName())).toPath());
		}
		else {
			if(destination.isFile()) {
				destination = destination.getParentFile();
			}
			Utils.copyDirectory(file, destination);
		}
	}
	
	public static final String stripHTML(final String s) {
		return s.replaceAll("\\<.*?\\>", "").replace("\n", "").replace("\r", "");
	}
	
	/**
	 * Joins a String list.
	 * 
	 * @param joiner The String used to join arrays.
	 * @param list The list.
	 * 
	 * @return The joined list.
	 */

	public static final String join(final String joiner, final List<String> list) {
		return join(joiner, list.toArray(new String[list.size()]));
	}
	
	/**
	 * Joins a String array.
	 * 
	 * @param joiner The String used to join arrays.
	 * @param strings The array.
	 * 
	 * @return The joined array.
	 */

	public static final String join(final String joiner, final String... strings) {
		if(strings.length == 1) {
			return strings[0];
		}
		else if(strings.length == 0) {
			return new String();
		}
		final StringBuilder builder = new StringBuilder();
		for(final String string : strings) {
			builder.append(string + joiner);
		}
		builder.setLength(builder.length() - joiner.length());
		return builder.toString();
	}
	
}