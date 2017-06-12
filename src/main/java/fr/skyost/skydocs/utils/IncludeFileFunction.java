package fr.skyost.skydocs.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

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
	 * The current cache.
	 */
	
	private static final HashMap<String, String> CACHE = new HashMap<String, String>();
	
	/**
	 * The directory (where you look up for files).
	 */
	
	private final File directory;
	
	/**
	 * The current jtwig model.
	 */
	
	private JtwigModel model;
	
	/**
	 * Functions to use for parsing files.
	 */
	
	private final JtwigFunction[] functions;
	
	public IncludeFileFunction(final File directory, final JtwigModel model, final JtwigFunction... functions) {
		this.directory = directory;
		this.model = model;
		this.functions = functions;
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
			final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(this).add(Arrays.asList(functions)).and().build();
			if(CACHE.containsKey(fileName)) {
				return JtwigTemplate.inlineTemplate(CACHE.get(fileName), configuration).render(model);
			}
			final File file = new File(directory.getPath() + File.separator + fileName);
			if(!file.exists() || !file.isFile()) {
				return "Incorrect path given : " + directory.getPath() + File.separator + fileName;
			}
			final String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
			CACHE.put(fileName, content);
			return JtwigTemplate.inlineTemplate(content, configuration).render(model);
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
	 * Gets the cached content for the specified file name.
	 * 
	 * @param fileName The file name.
	 * 
	 * @return The cached content.
	 */
	
	public final String getCacheContent(final String fileName) {
		return CACHE.get(fileName);
	}
	
	/**
	 * Puts some content in the cache for the specified file name.
	 * 
	 * @param fileName The file name.
	 * @param content The content.
	 */
	
	public final void putCacheContent(final String fileName, final String content) {
		CACHE.put(fileName, content);
	}
	
	/**
	 * Clears the current cache.
	 */
	
	public final void clearCache() {
		CACHE.clear();
	}
	
}