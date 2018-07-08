package fr.skyost.skydocs.task.build;

import fr.skyost.skydocs.DocsProject;
import fr.skyost.skydocs.DocsRunnable;
import fr.skyost.skydocs.utils.Utils;

import java.io.File;
import java.io.PrintStream;

/**
 * The task that allows to create the build directory.
 */

public class CreateBuildDirectoryTask extends DocsRunnable<Boolean> {

	/**
	 * The project.
	 */

	private DocsProject project;

	/**
	 * Creates a new Task instance.
	 *
	 * @param project The project.
	 */

	public CreateBuildDirectoryTask(final DocsProject project) {
		this(project, System.out);
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param project The project.
	 * @param out The output stream.
	 */

	public CreateBuildDirectoryTask(final DocsProject project, final PrintStream out) {
		super(out, null);

		this.project = project;
	}

	@Override
	public final Boolean execute() {
		output("Creating build directory...");

		final File buildDirectory = project.getBuildDirectory();
		if(buildDirectory.exists() && buildDirectory.isDirectory()) {
			Utils.deleteDirectory(buildDirectory);
		}
		return buildDirectory.mkdirs();
	}

	/**
	 * Returns the project.
	 *
	 * @return The project.
	 */

	public final DocsProject getProject() {
		return project;
	}

	/**
	 * Sets the project.
	 *
	 * @param project The project.
	 */

	public final void setProject(final DocsProject project) {
		this.project = project;
	}

}