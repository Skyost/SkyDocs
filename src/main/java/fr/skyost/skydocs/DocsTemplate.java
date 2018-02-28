package fr.skyost.skydocs;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import fr.skyost.skydocs.exceptions.InvalidTemplateException;
import fr.skyost.skydocs.utils.IncludeFileFunction;
import fr.skyost.skydocs.utils.RangeFunction;
import org.apache.commons.io.FilenameUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a theme / template.
 */

public class DocsTemplate {
	
	/**
	 * The range function.
	 */
	
	public static final RangeFunction RANGE_FUNCTION = new RangeFunction();
	
	/**
	 * The HTML compressor.
	 */
	
	public static final HtmlCompressor HTML_COMPRESSOR = new HtmlCompressor();
	
	static {
		HTML_COMPRESSOR.setRemoveHttpProtocol(false);
		HTML_COMPRESSOR.setRemoveHttpsProtocol(false);
		HTML_COMPRESSOR.setPreserveLineBreaks(true);
	}
	
	/**
	 * Theme directory of the project this template belongs to.
	 */
	
	private final File themeDirectory;
	
	/**
	 * Variables to put by default in the template.
	 */
	
	private final HashMap<String, Object> variables = new HashMap<>();
	
	/**
	 * The project this template belongs to.
	 */
	
	private final DocsProject project;
	
	/**
	 * Cached partially parsed page.html.
	 */
	
	private String template;
	
	/**
	 * Creates a new DocsTemplate instance.
	 * 
	 * @param variables The variables to put by default in the template.
	 * @param project The project this template belongs to.
	 * 
	 * @throws InvalidTemplateException If an error occurs while loading the template from the theme directory.
	 * @throws IOException If an exception occurs while reading the template.
	 */
	
	public DocsTemplate(final Map<String, Object> variables, final DocsProject project) throws InvalidTemplateException, IOException {
		if(variables != null) {
			this.variables.putAll(variables);
		}
		this.variables.put(Constants.VARIABLE_PROJECT, project);
		this.project = project;
		this.themeDirectory = project.getThemeDirectory();
		loadFromTemplateDirectory();
	}
	
	/**
	 * Gets the default variables of this template.
	 * 
	 * @return The default variables of this template.
	 */
	
	public final Map<String, Object> getVariables() {
		return variables;
	}
	
	/**
	 * Sets the default variables of this template.
	 * 
	 * @param variables The default variables to put.
	 */
	
	public final void putVariables(final Map<String, Object> variables) {
		this.variables.putAll(variables);
	}

	/**
	 * Removes a variable of this template.
	 *
	 * @param variable The variable.
	 */

	public final void removeVariable(final String variable) {
		variables.remove(variable);
	}

	/**
	 * Clears all variables of this template.
	 */

	public final void clearVariables() {
		variables.clear();
	}
	
	/**
	 * Gets the project this template belongs to.
	 * 
	 * @return The project this template belongs to.
	 */
	
	public final DocsProject getProject() {
		return project;
	}
	
	/**
	 * Loads this template data.
	 * 
	 * @throws InvalidTemplateException If an error occurs while loading the template from the theme directory.
	 * @throws IOException If an exception occurs while reading the template.
	 */
	
	public final void loadFromTemplateDirectory() throws InvalidTemplateException, IOException {
		final File pageTemplate = new File(themeDirectory, Constants.FILE_THEME_PAGE_FILE);
		if(!pageTemplate.exists() || !pageTemplate.isFile()) {
			throw new InvalidTemplateException("\"" + Constants.FILE_THEME_PAGE_FILE + "\" not found.");
		}
		
		final JtwigModel model = createModel();
		final IncludeFileFunction includeFile = new IncludeFileFunction(themeDirectory, model, false);
		template = includeFile.renderIncludeFile(pageTemplate);
	}
	
	/**
	 * Applies the template to a file.
	 * 
	 * @param file The file.
	 * @param compress Whether the file should be compressed.
	 * 
	 * @throws IOException If an exception occurs while saving the file.
	 */
	
	public final void applyTemplate(final File file, final boolean compress) throws IOException {
		applyTemplate(file, compress, null, null);
	}
	
	/**
	 * Applies the template to a file.
	 * 
	 * @param file The file.
	 * @param compress Whether the file should be compressed.
	 * @param page If the file is page.
	 * @param otherVariables Other variables to put.
	 * 
	 * @throws IOException If an exception occurs while saving the file.
	 */
	
	public final void applyTemplate(final File file, final boolean compress, DocsPage page, final Map<String, Object> otherVariables) throws IOException {
		if(page == null) {
			page = new DocsPage(project, file);
		}
		
		final JtwigModel model = createModel(otherVariables).with(Constants.VARIABLE_PAGE, page);
		final IncludeFileFunction includeFile = new IncludeFileFunction(themeDirectory, model, RANGE_FUNCTION);
		final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(includeFile).add(RANGE_FUNCTION).and().build();
		
		if(otherVariables != null) {
			page.addAdditionalVariables(otherVariables);
		}
		
		String content = JtwigTemplate.inlineTemplate(template, configuration).render(model);
		if(compress && FilenameUtils.getExtension(file.getPath()).equalsIgnoreCase("html")) {
			content = HTML_COMPRESSOR.compress(content);
		}
		
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Creates the corresponding model for this template.
	 * 
	 * @return The corresponding JTwig model.
	 */
	
	public final JtwigModel createModel() {
		return createModel(null);
	}
	
	/**
	 * Creates the corresponding model for this template with some variables.
	 * 
	 * @param otherVariables The variables.
	 * 
	 * @return The corresponding JTwig model.
	 */
	
	public final JtwigModel createModel(final Map<String, Object> otherVariables) {
		final HashMap<String, Object> variables = new HashMap<>(this.variables);
		if(otherVariables != null) {
			variables.putAll(otherVariables);
		}
		return JtwigModel.newModel(variables)
				.with(Constants.VARIABLE_GENERATOR_NAME, Constants.APP_NAME)
				.with(Constants.VARIABLE_GENERATOR_VERSION, Constants.APP_VERSION)
				.with(Constants.VARIABLE_GENERATOR_WEBSITE, Constants.APP_WEBSITE);
	}
	
}