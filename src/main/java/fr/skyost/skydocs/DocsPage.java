package fr.skyost.skydocs;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

import fr.skyost.skydocs.utils.IncludeFileFunction;
import fr.skyost.skydocs.utils.Utils;

/**
 * Represent a MarkDown or HTML page.
 */

public class DocsPage {
	
	/**
	 * CommonMark extensions.
	 */
	
	private static final List<Extension> CM_EXTENSIONS = Arrays.asList(AutolinkExtension.create(), StrikethroughExtension.create(), TablesExtension.create(), HeadingAnchorExtension.create());
	
	/**
	 * CommonMark parser.
	 */
	
	private static final Parser CM_PARSER = Parser.builder().extensions(CM_EXTENSIONS).build();
	
	/**
	 * CommonMark renderer.
	 */
	
	private static final HtmlRenderer CM_RENDERER = HtmlRenderer.builder().extensions(CM_EXTENSIONS).build();
	
	/**
	 * The project this page belongs to.
	 */
	
	private final DocsProject project;
	
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
		this.project = project;
		this.title = title;
		this.language = language;
		this.absolutePath = file.getPath();
		this.path = absolutePath.replace(project.getContentDirectory().getPath(), "").replace(project.getBuildDirectory().getPath(), "");
		this.relativeURL = getBuildDestination(project).getPath().replace(project.getBuildDirectory().getPath(), "").replace(File.separator, "/");
		
		final Map<String, Object> header = Utils.decodeFileHeader(Utils.separateFileHeader(file)[0]);
		this.header = header == null ? new HashMap<String, Object>() : new HashMap<String, Object>(header);
	}
	
	/**
	 * Gets the project this page belongs to.
	 * 
	 * @return The project this page belongs to.
	 */
	
	public final DocsProject getProject() {
		return project;
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
	 * Gets the page file's content (formatted with CommonMark).
	 * 
	 * @return The page file's content.
	 */
	
	public final String getContent() {
		final JtwigModel model = project.getTemplate().createModel().with(Constants.VARIABLE_PAGE, this);
		
		final IncludeFileFunction includeFile = new IncludeFileFunction(project.getContentDirectory(), model, DocsTemplate.RANGE_FUNCTION);
		final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(includeFile).add(DocsTemplate.RANGE_FUNCTION).and().build();
		
		return CM_RENDERER.render(CM_PARSER.parse(JtwigTemplate.inlineTemplate(getRawContent(), configuration).render(model)));
	}
	
	/**
	 * Gets the page file's raw content.
	 * 
	 * @return The page file's raw content.
	 */
	
	public final String getRawContent() {
		return Utils.separateFileHeader(getFile())[1];
	}
	
	/**
	 * Gets the last modification time formatted by the default locale.
	 * 
	 * @return The last modification time formatted by the default locale.
	 */
	
	public final String getLastModificationTime() {
		return getLastModificationTimeForLocale(Locale.getDefault());
	}
	
	/**
	 * Gets the last modification time formatted with the specified format.
	 * 
	 * @param format The format.
	 * 
	 * @return The last modification time formatted with the specified format.
	 */
	
	public final String getLastModificationTime(final String format) {
		return new SimpleDateFormat(format).format(new Date(getRawLastModificationTime()));
	}
	
	/**
	 * Gets the last modification time formatted with the specified locale.
	 * 
	 * @param locale The locale (will be parsed).
	 * 
	 * @return The last modification time formatted with the specified locale.
	 */
	
	public final String getLastModificationTimeForLocale(final String locale) {
		Locale currentLocale = null;
		try {
			currentLocale = LocaleUtils.toLocale(locale);
		}
		catch(final Exception ex) {}
		return getLastModificationTimeForLocale(currentLocale);
	}
	
	/**
	 * Gets the last modification time formatted with the specified locale.
	 * 
	 * @param locale The locale.
	 * 
	 * @return The last modification time formatted with the specified locale.
	 */
	
	public final String getLastModificationTimeForLocale(Locale locale) {
		if(locale == null || !LocaleUtils.isAvailableLocale(locale)) {
			locale = Locale.getDefault();
		}
		final Date date = new Date(getRawLastModificationTime());
		return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale).format(date);
	}
	
	/**
	 * Gets the last modification time in milliseconds.
	 * 
	 * @return The last modification time in milliseconds.
	 */
	
	public final long getRawLastModificationTime() {
		return getFile().lastModified();
	}
	
	/**
	 * Gets the build destination path of this page.
	 * 
	 * @param project The project this page belongs to.
	 */
	
	public final String getBuildDestinationPath(final DocsProject project) {
		return project.getBuildDirectory().getPath()
				+ (path.split(Pattern.quote(File.separator))[1].equals(getLanguage()) ? "" : File.separator + getLanguage())
				+ path.substring(0, path.lastIndexOf(".")) + ".html";
	}
	
	/**
	 * Gets the build destination of this page.
	 * 
	 * @param project The project this page belongs to.
	 */
	
	public final File getBuildDestination(final DocsProject project) {
		return new File(getBuildDestinationPath(project));
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
		final Map<String, Object> headers = Utils.decodeFileHeader(Utils.separateFileHeader(file)[0]);
		String title = headers != null && headers.containsKey(Constants.KEY_HEADER_TITLE) ? headers.get(Constants.KEY_HEADER_TITLE).toString() : StringUtils.capitalize(FilenameUtils.removeExtension(file.getName()));
		String language = headers != null && headers.containsKey(Constants.KEY_HEADER_LANGUAGE) ? headers.get(Constants.KEY_HEADER_LANGUAGE).toString() : project.getDefaultLanguage();
		return new DocsPage(project, title, language, file);
	}
	
}