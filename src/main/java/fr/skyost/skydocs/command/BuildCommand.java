package fr.skyost.skydocs.command;

import com.beust.jcommander.Parameter;
import fr.skyost.skydocs.DocsProject;
import fr.skyost.skydocs.task.build.ConvertFilesTask;
import fr.skyost.skydocs.task.build.CopyAssetsTask;
import fr.skyost.skydocs.task.build.CreateBuildDirectoryTask;
import fr.skyost.skydocs.task.build.ReloadProjectTask;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;

/**
 * "build" command.
 */

public class BuildCommand extends Command<BuildCommand.Arguments> {
	
	/**
	 * If you are in production mode.
	 */
	
	private final boolean prod;
	
	/**
	 * The current project.
	 */
	
	private DocsProject project;

	/**
	 * Task that allows to create the build directory.
	 */

	private final CreateBuildDirectoryTask createBuildDirectoryTask;

	/**
	 * Task that allows to copy & to convert files.
	 */

	private final ConvertFilesTask convertFilesTask;

	/**
	 * Task that allows to copy assets.
	 */

	private final CopyAssetsTask copyAssetsTask;

	/**
	 * Creates a new Command instance.
	 *
	 * @param prod Whether we are in production mode.
	 * @param args User arguments.
	 */

	public BuildCommand(final boolean prod, final String... args) {
		this(prod, System.out, args);
	}

	/**
	 * Creates a new Command instance.
	 *
	 * @param prod Whether we are in production mode.
	 * @param out The output stream.
	 * @param args User arguments.
	 */
	
	public BuildCommand(final boolean prod, final PrintStream out, final String... args) {
		super(out, null, args, new Arguments());

		this.prod = prod;

		this.createBuildDirectoryTask = new CreateBuildDirectoryTask(null, out);
		this.convertFilesTask = new ConvertFilesTask(null, prod, out);
		this.copyAssetsTask = new CopyAssetsTask(null, null, prod, out);

		this.setSubTasks(createBuildDirectoryTask, convertFilesTask, copyAssetsTask);
		reloadProject();
	}
	
	@Override
	public final Boolean execute() {
		if(project == null) {
			return null;
		}

		outputLine("Running build command...");

		if(createBuildDirectoryTask.run() == null) {
			return null;
		}

		final HashSet<File> copied = convertFilesTask.run();
		if(copied == null) {
			return null;
		}

		copyAssetsTask.setAlreadyCopiedFileList(copied);
		if(copyAssetsTask.run() == null) {
			return null;
		}

		outputLine("Finished ! You just have to put the content of \"" + project.getBuildDirectory().getPath() + "\" on your web server.");
		return true;
	}
	
	/**
	 * Checks if the command is in prod mode.
	 * 
	 * @return Whether the command is in prod mode.
	 */
	
	public final boolean isProdMode() {
		return prod;
	}
	
	/**
	 * Reloads the project (pages, menus, ...).
	 */
	
	public final void reloadProject() {
		new ReloadProjectTask(this, new File(this.getArguments().directory), this.getOutputStream()).run();
	}
	
	/**
	 * Gets the current project.
	 * 
	 * @return The current project.
	 */
	
	public final DocsProject getProject() {
		return project;
	}
	
	/**
	 * Sets the current project.
	 * 
	 * @param project The new project.
	 */
	
	public final void setProject(final DocsProject project) {
		this.project = project;

		createBuildDirectoryTask.setProject(project);
		convertFilesTask.setProject(project);
		copyAssetsTask.setProject(project);
	}

	/**
	 * Returns the task that allows to create the build directory.
	 *
	 * @return The task that allows to create the build directory.
	 */

	public CreateBuildDirectoryTask getCreateBuildDirectoryTask() {
		return createBuildDirectoryTask;
	}

	/**
	 * Returns the task that allows to copy & convert the files.
	 *
	 * @return The task that allows to copy & convert the files.
	 */

	public ConvertFilesTask getConvertFilesTask() {
		return convertFilesTask;
	}

	/**
	 * Returns the task that allows to copy assets.
	 *
	 * @return The task that allows to copy assets.
	 */

	public CopyAssetsTask getCopyAssetsTask() {
		return copyAssetsTask;
	}

	/**
	 * Command arguments.
	 */

	public static class Arguments {

		@Parameter(names = {"-directory", "-d"}, description = "Sets the current build directory.")
		public String directory = System.getProperty("user.dir");

	}
	
}