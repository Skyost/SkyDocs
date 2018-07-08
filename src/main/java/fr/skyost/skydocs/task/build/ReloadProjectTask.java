package fr.skyost.skydocs.task.build;

import fr.skyost.skydocs.DocsProject;
import fr.skyost.skydocs.DocsRunnable;
import fr.skyost.skydocs.command.BuildCommand;
import fr.skyost.skydocs.exception.LoadException;
import fr.skyost.skydocs.utils.Utils;

import java.io.File;
import java.io.PrintStream;
import java.util.Set;

/**
 * The task that allows to (re)load a project according to a provided directory.
 */

public class ReloadProjectTask extends DocsRunnable<Boolean> {

	/**
	 * The build command.
	 */

	private BuildCommand command;

	/**
	 * The directory.
	 */

	private File directory;

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The build command.
	 * @param directory The directory.
	 */

	public ReloadProjectTask(final BuildCommand command, final File directory) {
		this(command, directory, System.out);
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The build command.
	 * @param directory The directory.
	 * @param out The output stream.
	 */

	public ReloadProjectTask(final BuildCommand command, final File directory, final PrintStream out) {
		super(out, null);

		this.command = command;
		this.directory = directory;
	}

	@Override
	public final Boolean execute() throws LoadException {
		try {
			output("Loading project from directory \"" + directory.getName() + "\" and loading theme... ");

			final Utils.Pair<DocsProject, Set<String>> result = DocsProject.loadFromDirectory(directory);

			if(!result.b.isEmpty()) {
				blankLine();
				outputLine("These files are not going to be copied because a file with the same name will already be copied in the destination folder :");
				for(final String alreadyExists : result.b) {
					outputLine("* " + alreadyExists);
				}
				blankLine();
			}

			command.setProject(result.a);
			return true;
		}
		catch(final LoadException ex) {
			blankLine();
			outputLine("Cannot load the project from the specified directory !");
			throw ex;
		}
	}

	/**
	 * Returns the build command.
	 *
	 * @return The build command.
	 */

	public final BuildCommand getBuildCommand() {
		return command;
	}

	/**
	 * Sets the build command.
	 *
	 * @param command The build command.
	 */

	public final void setBuildCommand(final BuildCommand command) {
		this.command = command;
	}

	/**
	 * Returns the directory.
	 *
	 * @return The directory.
	 */

	public final File getDirectory() {
		return directory;
	}

	/**
	 * Sets the directory.
	 *
	 * @param directory The directory.
	 */

	public final void setDirectory(final File directory) {
		this.directory = directory;
	}

}