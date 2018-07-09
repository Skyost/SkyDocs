package fr.skyost.skydocs.command;

import com.beust.jcommander.Parameter;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.exception.ProjectAlreadyExistsException;
import fr.skyost.skydocs.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * "new" command.
 */

public class NewCommand extends Command<NewCommand.Arguments> {

	/**
	 * Creates a new Command instance.
	 *
	 * @param args User arguments.
	 */

	public NewCommand(final String... args) {
		this(System.out, System.in, args);
	}

	/**
	 * Creates a new Command instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 * @param args User arguments.
	 */

	public NewCommand(final PrintStream out, final InputStream in, final String... args) {
		super(out, in, args, new Arguments());
	}
	
	@Override
	public final Boolean execute() throws ProjectAlreadyExistsException, InterruptionException, IOException, URISyntaxException {
		final File directory = new File(this.getArguments().directory);

		if(new File(directory, Constants.FILE_PROJECT_DATA).exists()) {
			throw new ProjectAlreadyExistsException("A project already exists in that location !");
		}

		output("Creating a new project in the directory \"" + directory + "\"...");
		Utils.extract(Constants.RESOURCE_NEW_PROJECT_PATH, Constants.RESOURCE_NEW_PROJECT_DIRECTORY, directory);

		final File jar = Utils.getJARFile();
		if(jar == null) {
			throw new NullPointerException("Failed to locate JAR !");
		}

		final String jarPath = jar.getPath();
		final String prefix = com.google.common.io.Files.getFileExtension(jarPath).equalsIgnoreCase("jar") ? "java -jar " : "";

		for(final String extension : new String[]{"bat", "sh", "command"}) {
			exitIfInterrupted();

			createFile(jarPath, new File(directory, "build." + extension), prefix + "\"%s\" build");
			createFile(jarPath, new File(directory, "serve." + extension), prefix + "\"%s\" serve");
		}

		return true;
	}
	
	/**
	 * Creates a file, content will be formatted with JAR's file location.
	 *
	 * @param jarPath The JAR path.
	 * @param file The file.
	 * @param content Content of the file.
	 * 
	 * @throws IOException If an exception occurs while saving the file.
	 */
	
	private void createFile(final String jarPath, final File file, final String content) throws IOException {
		Files.write(file.toPath(), String.format(content, jarPath).getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Command arguments.
	 */

	public static class Arguments {

		@Parameter(names = {"-directory", "-d"}, description = "Sets the new project directory.")
		public String directory = System.getProperty("user.dir");

	}
	
}