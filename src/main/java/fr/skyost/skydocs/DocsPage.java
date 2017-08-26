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
	 * The previous page.
	 */
	
	private String previous;
	
	/**
	 * The next page.
	 */
	
	private String next;
	
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
	
	private final HashMap<String, Object> header = new HashMap<String, Object>();
	
	/**
	 * Additional variables (when parsing).
	 */
	
	private final HashMap<String, Object> additionalVariables = new HashMap<String, Object>();
	
	/**
	 * Creates a new DocsPage instance.
	 * 
	 * @param project The project this page belongs to.
	 * @param title The title of this page.
	 * @param language The language of this page.
	 * @param file The file (HTML or MarkDown) that represents the content of this page.
	 */
	
	public DocsPage(final DocsProject project, final File file) {
		final Map<String, Object> header = Utils.decodeFileHeader(Utils.separateFileHeader(file)[0]);
		
		this.project = project;
		this.title = header != null && header.containsKey(Constants.KEY_HEADER_TITLE) ? header.get(Constants.KEY_HEADER_TITLE).toString() : StringUtils.capitalize(FilenameUtils.removeExtension(file.getName()));
		this.language = header != null && header.containsKey(Constants.KEY_HEADER_LANGUAGE) ? header.get(Constants.KEY_HEADER_LANGUAGE).toString() : project.getDefaultLanguage();
		this.absolutePath = file.getPath();
		this.path = absolutePath.replace(project.getContentDirectory().getPath(), "").replace(project.getBuildDirectory().getPath(), "");
		this.relativeURL = getBuildDestination(project).getPath().replace(project.getBuildDirectory().getPath(), "").replace(File.separator, "/");
		
		if(header != null) {
			if(header.containsKey(Constants.KEY_HEADER_PREVIOUS)) {
				this.previous = header.get(Constants.KEY_HEADER_PREVIOUS).toString();
			}
			if(header.containsKey(Constants.KEY_HEADER_NEXT)) {
				this.next = header.get(Constants.KEY_HEADER_NEXT).toString();
			}
			this.header.putAll(header);
		}
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
	 * Checks if the page has a previous page.
	 * 
	 * @return Whether the page has a previous page.
	 */
	
	public final boolean hasPreviousPage() {
		return previous != null;
	}
	
	/**
	 * Gets the previous page if specified.
	 * 
	 * @return The previous page.
	 */
	
	public final String getPreviousPage() {
		return previous;
	}
	
	/**
	 * Sets the previous page.
	 * 
	 * @param previous The previous page.
	 */
	
	public final void setPreviousPage(final String previous) {
		this.previous = previous;
	}
	
	/**
	 * Checks if the page has a next page.
	 * 
	 * @return Whether the page has a next page.
	 */
	
	public final boolean hasNextPage() {
		return next != null;
	}
	
	/**
	 * Gets the next page if specified.
	 * 
	 * @return The next page.
	 */
	
	public final String getNextPage() {
		return next;
	}
	
	/**
	 * Sets the next page.
	 * 
	 * @param previous The next page.
	 */
	
	public final void setNextPage(final String next) {
		this.next = next;
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
	 * Adds a new additional variable.
	 * 
	 * @param variable The variable's name.
	 * @param value The variable's value.
	 */
	
	public final void addAdditionalVariable(final String variable, final Object value) {
		additionalVariables.put(variable, value);
	}
	
	/**
	 * Adds all specified additional variable.
	 * 
	 * @param additionalVariables The additional variables.
	 */
	
	public final void addAdditionalVariables(final Map<String, Object> additionalVariables) {
		this.additionalVariables.putAll(additionalVariables);
	}
	
	/**
	 * Removes an additional variable.
	 * 
	 * @param variable The variable's name.
	 */
	
	public final void removeAdditionalVariable(final String variable) {
		additionalVariables.remove(variable);
	}
	
	/**
	 * Clears all additional variables.
	 */
	
	public final void clearAdditionalVariables() {
		additionalVariables.clear();
	}
	
	/**
	 * Gets all additional variables.
	 * 
	 * @return A map containing all additional variables.
	 */
	
	public final Map<String, Object> getAdditionalVariables() {
		return additionalVariables;
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
	 * Gets the root relative URL.
	 * 
	 * @return The root relative URL.
	 */
	
	public final String getRootRelativeURL() {
		final int length = relativeURL.split("/").length;
		final StringBuilder url = new StringBuilder();
		for(int i = 2; i < length; i++) {
			url.append("../");
		}
		return url.toString();
	}
	
	/**
	 * Gets the page file's content (formatted with CommonMark).
	 * 
	 * @return The page file's content.
	 */
	
	public final String getContent() {
		final JtwigModel model = project.getTemplate().createModel(additionalVariables).with(Constants.VARIABLE_PAGE, this);
		
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
	
}