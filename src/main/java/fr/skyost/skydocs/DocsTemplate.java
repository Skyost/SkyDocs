package fr.skyost.skydocs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import fr.skyost.skydocs.exceptions.InvalidTemplateException;
import fr.skyost.skydocs.utils.IncludeFileFunction;
import fr.skyost.skydocs.utils.RangeFunction;

/**
 * Represents a theme / template.
 */

public class DocsTemplate {
	
	/**
	 * Theme directory of the project this template belongs to.
	 */
	
	private final File themeDirectory;
	
	/**
	 * Variables to put by default in the template.
	 */
	
	private final HashMap<String, Object> variables = new HashMap<String, Object>();
	
	/**
	 * Cached parsed page.html.
	 */
	
	private String template;
	
	/**
	 * The range function.
	 */
	
	private final RangeFunction rangeFunction = new RangeFunction();
	
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
		this.themeDirectory = project.getThemeDirectory();
		loadFromTemplateDirectory(project);
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
	 * Loads this template data from a project's theme directory.
	 * 
	 * @param project The project.
	 * 
	 * @throws InvalidTemplateException If an error occurs while loading the template from the theme directory.
	 * @throws IOException If an exception occurs while reading the template.
	 */
	
	public final void loadFromTemplateDirectory(final DocsProject project) throws InvalidTemplateException, IOException {
		final File pageTemplate = new File(themeDirectory, Constants.FILE_THEME_PAGE_FILE);
		if(!pageTemplate.exists() || !pageTemplate.isFile()) {
			throw new InvalidTemplateException("\"" + Constants.FILE_THEME_PAGE_FILE + "\" not found.");
		}
		
		/*final JtwigModel model = JtwigModel.newModel(project.getProjectVariables());
		model.with(Constants.VARIABLE_PROJECT, project);
		model.with(Constants.VARIABLE_PAGE, DocsPage.createBlankParsablePage());
		final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(new IncludeFileFunction(model)).and().build();
		
		template = JtwigTemplate.fileTemplate(pageTemplate, configuration).render(model);*/
		template = new String(Files.readAllBytes(pageTemplate.toPath()), StandardCharsets.UTF_8);
	}
	
	/**
	 * Applies the template to a file.
	 * 
	 * @param project The project this template belongs to.
	 * @param file The file.
	 * 
	 * @throws IOException If an exception occurs while saving the file.
	 */
	
	public final void applyTemplate(final DocsProject project, final File file) throws IOException {
		applyTemplate(project, file, null, null);
	}
	
	/**
	 * Applies the template to a file.
	 * 
	 * @param project The project this template belongs to.
	 * @param file The file.
	 * @param page If the file is page.
	 * @param otherVariables Other variables to put.
	 * 
	 * @throws IOException If an exception occurs while saving the file.
	 */
	
	public final void applyTemplate(final DocsProject project, final File file, DocsPage page, final Map<String, Object> otherVariables) throws IOException {
		if(page == null) {
			page = DocsPage.createFromFile(project, file);
		}
		
		final HashMap<String, Object> variables = new HashMap<String, Object>(this.variables);
		if(otherVariables != null) {
			variables.putAll(otherVariables);
		}
		
		variables.put(Constants.VARIABLE_PROJECT, project);
		variables.put(Constants.VARIABLE_PAGE, page);
		
		final JtwigModel model = JtwigModel.newModel(variables);
		final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(new IncludeFileFunction(themeDirectory, model, rangeFunction)).add(rangeFunction).and().build();
		
		Files.write(file.toPath(), JtwigTemplate.inlineTemplate(template, configuration).render(model).getBytes(StandardCharsets.UTF_8));
	}
	
}