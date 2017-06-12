package fr.skyost.skydocs.utils;

import java.io.File;
import java.util.Arrays;

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
	
	private final File themeDirectory;
	private final JtwigModel model;
	private final JtwigFunction[] functions;
	
	public IncludeFileFunction(final File themeDirectory, final JtwigModel model, final JtwigFunction... functions) {
		this.themeDirectory = themeDirectory;
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
			final File file = new File(themeDirectory, fileName);
			if(!file.exists() || !file.isFile()) {
				return "Incorrect path given : " + themeDirectory.getPath() + File.separator + fileName;
			}
			final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(this).add(Arrays.asList(functions)).and().build();
			return JtwigTemplate.fileTemplate(file, configuration).render(model);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			return "Unable to parse " + themeDirectory.getPath() + File.separator + fileName + " (" + ex.getClass().getName() + ")";
		}
	}
	
}