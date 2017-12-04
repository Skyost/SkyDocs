package fr.skyost.skydocs.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FilenameUtils;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.exceptions.ProjectAlreadyExistsException;
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
			
			final boolean isJAR = FilenameUtils.getExtension(Utils.getJARFile().getPath()).equalsIgnoreCase("jar");
			
			for(final String extension : new String[]{"bat", "sh", "command"}) {
				exitIfInterrupted();
				
				createFile(new File(directory, "build." + extension), (isJAR ? "java -jar " : "") + "\"%s\" build");
				createFile(new File(directory, "serve." + extension), (isJAR ? "java -jar " : "") + "\"%s\" serve");
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
	 * @param file The file.
	 * @param content Content of the file.
	 * 
	 * @throws IOException If an exception occurs while saving the file.
	 */
	
	private final void createFile(final File file, final String content) throws IOException {
		Files.write(file.toPath(), String.format(content, Utils.getJARFile().getPath()).getBytes(StandardCharsets.UTF_8));
	}
	
}