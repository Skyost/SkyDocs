package fr.skyost.skydocs;

import java.io.File;
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
	}
	
	/**
	 * Creates a new DocsPage instance.
	 * 
	 * @param title The title of this page.
	 * @param language The language of this page.
	 * @param absolutePath The absolute path of the file that represents this page's content.
	 * @param path The path of this file (relative to the project's directory).
	 * @param relativeURL The URL of this page (relative to the project's build directory).
	 */
	
	private DocsPage(final String title, final String language, final String absolutePath, final String path, final String relativeURL) {
		this.title = title;
		this.language = language;
		this.absolutePath = absolutePath;
		this.path = path;
		this.relativeURL = relativeURL;
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
	 * Gets the URL of this page relative to the project's directory.
	 * 
	 * @return The URL of this page.
	 */
	
	public final String getPageRelativeURL() {
		return relativeURL;
	}
	
	/**
	 * Gets the page file's header.
	 * 
	 * @return The page file's header.
	 */
	
	public final String getHeader() {
		if(absolutePath == null) {
			return "{{ page.getHeader() }}";
		}
		return Utils.separateFileHeader(getFile())[0];
	}
	
	/**
	 * Gets the page file's content.
	 * 
	 * @return The page file's content.
	 */
	
	public final String getContent() {
		if(absolutePath == null) {
			return "{{ page.getContent() }}";
		}
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
		String title = file.getName().replace(".md", "").replace(".html", ""); // TODO: Handle .HTML & .MD
		String language = project.getDefaultLanguage();
		if(parts[0] != null) {
			final Map<String, Object> headers = Utils.decodeFileHeader(parts[0]);
			title = headers.containsKey(Constants.KEY_HEADER_TITLE) ? headers.get(Constants.KEY_HEADER_TITLE).toString() : title;
			language = headers.containsKey(Constants.KEY_HEADER_LANGUAGE) ? headers.get(Constants.KEY_HEADER_LANGUAGE).toString() : language;
		}
		return new DocsPage(project, title, language, file);
	}
	
	/**
	 * Creates a blank parsable page (used to leave default tags when parsed).
	 * 
	 * @return The blank parsable page.
	 */
	
	public static final DocsPage createBlankParsablePage() {
		return new DocsPage("{{ page.getTitle() }}", "{{ page.getLanguage() }}", null, "{{ page.getPath() }}", "{{ page.getPageRelativeURL() }}");
	}
	
}