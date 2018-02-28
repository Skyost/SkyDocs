package fr.skyost.skydocs.commands;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.exceptions.ProjectAlreadyExistsException;
import fr.skyost.skydocs.utils.Utils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * "new" command.
 */

public class NewCommand extends Command {
	
	public NewCommand(final String... args) {
		super(args);
	}
	
	@Override
	public final void run() {
		super.run();
		try {
			final String[] args = this.getArguments();
			final File directory = new File(args.length > 0 && args[0].length() > 0 ? args[0] : System.getProperty("user.dir"));
			
			if(new File(directory, Constants.FILE_PROJECT_DATA).exists()) {
				throw new ProjectAlreadyExistsException("A project already exists in that location !");
			}
			
			firstTime();
			
			output("Creating a new project in the directory \"" + directory + "\"...");
			Utils.extract(Constants.RESOURCE_NEW_PROJECT_PATH, Constants.RESOURCE_NEW_PROJECT_DIRECTORY, directory);

			final String jarPath = Utils.getJARFile().getPath();
			final String prefix = FilenameUtils.getExtension(jarPath).equalsIgnoreCase("jar") ? "java -jar " : "";
			
			for(final String extension : new String[]{"bat", "sh", "command"}) {
				exitIfInterrupted();
				
				createFile(jarPath, new File(directory, "build." + extension), prefix + "\"%s\" build");
				createFile(jarPath, new File(directory, "serve." + extension), prefix + "\"%s\" serve");
			}
			
			secondTime();
			printTimeElapsed();
		}
		catch(final Exception ex) {
			printStackTrace(ex);
			broadcastCommandError(ex);
		}
		exitIfNeeded();
	}
	
	@Override
	public final boolean isInterruptible() {
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
	
}