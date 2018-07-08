package fr.skyost.skydocs.task.serve;

import fr.skyost.skydocs.DocsRunnable;
import fr.skyost.skydocs.command.BuildCommand;

import java.io.PrintStream;

/**
 * The task that allows to run a new build.
 */

public class NewBuildTask extends DocsRunnable<Long> {

	/**
	 * The build command.
	 */

	private BuildCommand command;

	/**
	 * Whether the project should be reloaded.
	 */

	private boolean reloadProject;

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The build command.
	 * @param reloadProject Whether the project should be reloaded.
	 */

	public NewBuildTask(final BuildCommand command, final boolean reloadProject) {
		this(command, reloadProject, System.out);
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The build command.
	 * @param reloadProject Whether the project should be reloaded.
	 * @param out The output stream.
	 */

	public NewBuildTask(final BuildCommand command, final boolean reloadProject, final PrintStream out) {
		super(out, null);

		this.command = command;
		this.reloadProject = reloadProject;
	}

	@Override
	public final Long execute() {
		output("Running build command... ");

		if(!command.isInterrupted()) {
			command.interrupt();
		}

		if(reloadProject) {
			command.reloadProject();
		}

		command.run(false);
		return System.currentTimeMillis();
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
	 * Returns whether the project should be reloaded.
	 *
	 * @return Whether the project should be reloaded.
	 */

	public final boolean shouldReloadProject() {
		return reloadProject;
	}

	/**
	 * Sets whether the project should be reloaded.
	 *
	 * @param reloadProject Whether the project should be reloaded.
	 */

	public final void setShouldReloadProject(final boolean reloadProject) {
		this.reloadProject = reloadProject;
	}

}