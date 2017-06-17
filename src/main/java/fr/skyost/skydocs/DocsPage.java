package fr.skyost.skydocs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import fr.skyost.skydocs.utils.Utils;

/**
 * Represent a MarkDown or HTML page.
 */

public class DocsPage {
	
	/**
	 * The page's title.
	 */
	
	private String title;
	
	/**
	 * The page's language.
	 */
	
	private String language;
	
	/**
	 * Absolute path of this page.
	 */
	
	private final String absolutePath;
	
	/**
	 * Path of this page.
	 */
	
	private final String path;
	
	/**
	 * Relative URL of this page.
	 */
	
	private final String relativeURL;
	
	/**
	 * The page's header.
	 */
	
	private final HashMap<String, Object> header;
	
	/**
	 * Creates a new DocsPage instance.
	 * 
	 * @param project The project this page belongs to.
	 * @param title The title of this page.
	 * @param language The language of this page.
	 * @param file The file (HTML or MarkDown) that represents the content of this page.
	 */
	
	public DocsPage(final DocsProject project, final String title, final String language, final File file) {
		this.title = title;
		this.language = language;
		this.absolutePath = file.getPath();
		this.path = absolutePath.replace(project.getContentDirectory().getPath(), "").replace(project.getBuildDirectory().getPath(), "");
		this.relativeURL = getBuildDestination(project).getPath().replace(project.getBuildDirectory().getPath(), "").replace(File.separator, "/");
		
		final Map<String, Object> header = Utils.decodeFileHeader(Utils.separateFileHeader(file)[0]);
		this.header = header == null ? new HashMap<String, Object>() : new HashMap<String, Object>(header);
	}
	
	/**
	 * Gets the title of this page.
	 * 
	 * @return The title of this page.
	 */
	
	public final String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title of this page.
	 * 
	 * @param title The new title of this page.
	 */
	
	public final void setTitle(final String title) {
		this.title = title;
	}
	
	/**
	 * Gets the language of this page.
	 * 
	 * @return The language of this page.
	 */
	
	public final String getLanguage() {
		return language;
	}
	
	/**
	 * Sets the language of this page.
	 * 
	 * @param language The new language of this page.
	 */
	
	public final void setLanguage(final String language) {
		this.language = language;
	}
	
	/**
	 * Gets the file of this page.
	 * 
	 * @return The file of this page.
	 */
	
	public final File getFile() {
		return new File(absolutePath);
	}
	
	/**
	 * Gets the path of this page relative to the project's directory.
	 * 
	 * @return The path of this page.
	 */
	
	public final String getPath() {
		return path;
	}
	
	/**
	 * Gets the page file's header.
	 * 
	 * @return The page file's header.
	 */
	
	public final Map<String, Object> getHeader() {
		return header;
	}
	
	/**
	 * Gets the field (put by the user in the header).
	 * 
	 * @param key The key.
	 * 
	 * @return If found, the corresponding value.
	 */
	
	public final Object getField(final String key) {
		if(header == null) {
			return "This page has no header.";
		}
		if(!header.containsKey(key)) {
			return "This page's header does not contains the specified key \"" + key + "\".";
		}
		return header.get(key);
	}
	
	/**
	 * Gets the URL of this page relative to the project's directory.
	 * 
	 * @return The URL of this page.
	 */
	
	public final String getPageRelativeURL() {
		return relativeURL;
	}
	
	/**
	 * Gets the page file's content.
	 * 
	 * @return The page file's content.
	 */
	
	public final String getContent() {
		return Utils.separateFileHeader(getFile())[1];
	}
	
	/**
	 * Gets the build destination of this page.
	 * 
	 * @param project The project this page belongs to.
	 */
	
	public final File getBuildDestination(final DocsProject project) {
		return new File(project.getBuildDirectory().getPath()
				+ (path.split(Pattern.quote(File.separator))[1].equals(getLanguage()) ? "" : getLanguage() + File.separator)
				+ path.substring(0, path.lastIndexOf(".")) + ".html");
	}
	
	/**
	 * Creates a DocsPage instance from a file.
	 * 
	 * @param project The project this page belongs to.
	 * @param file The page's content.
	 * 
	 * @return The DocsPage instance.
	 */
	
	public static final DocsPage createFromFile(final DocsProject project, final File file) {
		final String[] parts = Utils.separateFileHeader(file);
		String title = file.getName().replaceAll(".(?i)html", "").replaceAll(".(?i)md", "");
		String language = project.getDefaultLanguage();
		if(parts[0] != null) {
			final Map<String, Object> headers = Utils.decodeFileHeader(parts[0]);
			title = headers.containsKey(Constants.KEY_HEADER_TITLE) ? headers.get(Constants.KEY_HEADER_TITLE).toString() : title;
			language = headers.containsKey(Constants.KEY_HEADER_LANGUAGE) ? headers.get(Constants.KEY_HEADER_LANGUAGE).toString() : language;
		}
		return new DocsPage(project, title, language, file);
	}
	
}