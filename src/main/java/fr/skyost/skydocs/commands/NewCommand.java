package fr.skyost.skydocs.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.utils.Utils;

/**
 * "new" command.
 */

public class NewCommand extends Command {
	
	public NewCommand(final String... args) {
		super(args);
	}
	
	@Override
	public final void run() {
		try {
			final String[] args = this.getArguments();
			final File directory = args.length > 0 && args[0].length() > 0 ? new File(args[0]) : Utils.getParentFolder();
			System.out.print("Creating a new project in the directory \"" + directory + "\"...");
			Utils.extract(Constants.RESOURCE_NEW_PROJECT_PATH, Constants.RESOURCE_NEW_PROJECT_DIRECTORY, directory);
			for(final String extension : new String[]{"bat", "sh", "command"}) {
				createFile(new File(directory, "build." + extension), "java -jar \"%s\" build \"%s\"");
				createFile(new File(directory, "serve." + extension), "java -jar \"%s\" serve \"%s\"");
			}
			System.out.println(" OK !");
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		super.run();
	}
	
	/**
	 * Creates a file with two String.format(...) parameters : first is the JAR file path, second is the specified file directory path.
	 * 
	 * @param file The file.
	 * @param content Content of the file.
	 * 
	 * @throws IOException If an exception occurs while saving the file.
	 */
	
	private final void createFile(final File file, final String content) throws IOException {
		Files.write(file.toPath(), String.format(content, Utils.getJARFile().getPath(), file.getParentFile().getPath()).getBytes(StandardCharsets.UTF_8));
	}
	
}