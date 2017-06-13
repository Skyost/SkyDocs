package fr.skyost.skydocs.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.JtwigFunction;
import org.jtwig.functions.SimpleJtwigFunction;

import fr.skyost.skydocs.Constants;

/**
 * The includeFile(file) function.
 */

public class IncludeFileFunction extends SimpleJtwigFunction {
	
	/**
	 * The directory (where you look up for files).
	 */
	
	private final File directory;
	
	/**
	 * The current jtwig model.
	 */
	
	private JtwigModel model;
	
	/**
	 * If the function should entirely render the file.
	 */
	
	private final boolean render;
	
	/**
	 * Functions to use for parsing files.
	 */
	
	private final JtwigFunction[] functions;
	
	/**
	 * Creates a new IncludeFileFunction instance.
	 * 
	 * @param directory Where to find files.
	 * @param model The jtwig model.
	 * @param functions The functions (used to render).
	 */
	
	public IncludeFileFunction(final File directory, final JtwigModel model, final JtwigFunction... functions) {
		this(directory, model, true, functions);
	}
	
	/**
	 * Creates a new IncludeFileFunction instance.
	 * 
	 * @param directory Where to find files.
	 * @param model The jtwig model.
	 * @param render If the file should be entirely rendered.
	 * @param functions The functions (used to render).
	 */
	
	public IncludeFileFunction(final File directory, final JtwigModel model, final boolean render, final JtwigFunction... functions) {
		this.directory = directory;
		this.model = model;
		this.render = render;
		this.functions = functions == null ? new JtwigFunction[0] : functions;
	}

	@Override
	public final String name() {
		return Constants.FUNCTION_INCLUDE_FILE;
	}
	
	@Override
	public final Object execute(final FunctionRequest functionRequest) {
		if(functionRequest.getNumberOfArguments() == 0) {
			return "You must specify a file in " + Constants.FUNCTION_INCLUDE_FILE + ".";
		}
		final String fileName = functionRequest.getArguments().get(0).toString();
		try {
			final File file = new File(directory.getPath() + File.separator + fileName);
			if(!file.exists() || !file.isFile()) {
				return "Incorrect path given : " + directory.getPath() + File.separator + fileName;
			}
			if(!render) {
				return renderIncludeFile(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
			}
			final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(this).add(Arrays.asList(functions)).and().build();
			return JtwigTemplate.fileTemplate(file, configuration).render(model);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			return "Unable to parse " + directory.getPath() + File.separator + fileName + " (" + ex.getClass().getName() + ")";
		}
	}
	
	/**
	 * Gets the current model.
	 * 
	 * @return The current model.
	 */
	
	public final JtwigModel getModel() {
		return model;
	}
	
	/**
	 * Sets the current model.
	 * 
	 * @param model The new model.
	 */
	
	public final void setModel(final JtwigModel model) {
		this.model = model;
	}
	
	/**
	 * Render a String but processing only include file functions.
	 * 
	 * @param toRender String to render.
	 * 
	 * @return Rendered String.
	 */
	
	public final String renderIncludeFile(String toRender) {
		final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(this).add(Arrays.asList(functions)).and().build();
		final Matcher matcher = Pattern.compile("\\{\\{[ ]{0,}" + Constants.FUNCTION_INCLUDE_FILE + "\\(\".*?\"\\)[ ]{0,}\\}\\}").matcher(toRender);
		while(matcher.find()) {
			final String matching = matcher.group();
			toRender = toRender.replace(matching, JtwigTemplate.inlineTemplate(matching, configuration).render(model));
		}
		return toRender;
	}
	
}