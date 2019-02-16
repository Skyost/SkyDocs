package fr.skyost.skydocs.command;

import com.beust.jcommander.Parameter;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.DocsServer;
import fr.skyost.skydocs.task.serve.FirstBuildTask;
import fr.skyost.skydocs.task.serve.NewBuildTask;
import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryWatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * "serve" command.
 */

public class ServeCommand extends Command<ServeCommand.Arguments> {

	/**
	 * The docs server.
	 */

	private DocsServer server;

	/**
	 * The build command.
	 */

	private final BuildCommand command;

	/**
	 * The task that allows to run the first build.
	 */

	private final NewBuildTask newBuildTask;

	/**
	 * The task that allows to run a new build.
	 */

	private final FirstBuildTask firstBuildTask;

	/**
	 * The current directory watcher.
	 */

	private DirectoryWatcher watcher;

	/**
	 * Creates a new Command instance.
	 *
	 * @param args User arguments.
	 */

	public ServeCommand(final String... args) {
		this(System.out, System.in, args);
	}

	/**
	 * Creates a new Command instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 * @param args User arguments.
	 */
	
	public ServeCommand(final PrintStream out, final InputStream in, final String... args) {
		super(out, in, args, new Arguments());

		final Arguments arguments = this.getArguments();
		command  = new BuildCommand(false, null, arguments.directory == null ? null : new String[]{"-directory", arguments.directory});

		newBuildTask = new NewBuildTask(command, true, out);
		firstBuildTask = new FirstBuildTask(this, arguments.port, out);

		this.setSubTasks(command, newBuildTask, firstBuildTask);
	}
	
	@Override
	public final Boolean execute() throws IOException {
		final Arguments arguments = this.getArguments();
		blankLine();

		if(!arguments.manualRebuild || this.getScanner() == null) {
			newBuildTask.run();
			server = firstBuildTask.run(false);
			registerFileListener(server);
			return null;
		}

		boolean firstBuild = true;
		String line = "";
		while(line == null || line.isEmpty()) {
			final Long buildTime = newBuildTask.run();
			if(buildTime == null) {
				return null;
			}

			if(server != null) {
				server.setLastBuild(buildTime);
			}

			if(firstBuild) {
				server = firstBuildTask.run(false);
				registerFileListener(server);
			}
			firstBuild = false;

			outputLine(Constants.SERVE_MANUAL_REBUILD);
			blankLine();

			if(isInterrupted()) {
				break;
			}

			line = inputLine();
		}

		return true;
	}
	
	@Override
	public final void interrupt() {
		if(server != null) {
			try {
				server.stop();
			}
			catch(final Exception ex) {
				ex.printStackTrace(this.getOutputStream() == null ? System.err : this.getOutputStream());
			}
		}

		if(watcher != null) {
			try {
				watcher.close();
			}
			catch(Exception ex) {
				ex.printStackTrace(this.getOutputStream() == null ? System.err : this.getOutputStream());
			}
		}

		super.interrupt();
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
	 * Returns the task that allows to run the first build.
	 *
	 * @return The task that allows to run the first build.
	 */

	public final FirstBuildTask getFirstBuildTask() {
		return firstBuildTask;
	}

	/**
	 * Returns the task that allows to run a new build.
	 *
	 * @return The task that allows to run a new build.
	 */

	public final NewBuildTask getNewBuildTask() {
		return newBuildTask;
	}

	/**
	 * Returns the directory watcher.
	 *
	 * @return The directory watcher.
	 */

	public final DirectoryWatcher getWatcher() {
		return watcher;
	}

	/**
	 * Creates and registers a file listener with the help of the specified build command.
	 *
	 * @param server The current docs server.
	 *
	 * @throws IOException If any I/O exception occurs.
	 */

	private void registerFileListener(final DocsServer server) throws IOException {
		watcher = DirectoryWatcher
				.builder()
				.path(command.getProject().getDirectory().toPath())
				.listener(event -> {
					final File file = event.path().toFile();
					rebuildIfNeeded(server, event.eventType() != DirectoryChangeEvent.EventType.MODIFY || server.getProject().shouldReloadProject(file), file);
				})
				.build();
		watcher.watchAsync();
	}

	/**
	 * Rebuilds the project if needed.
	 *
	 * @param server The docs server.
	 * @param reloadProject Whether the project should be reloaded.
	 * @param file The file that has changed.
	 */

	private synchronized void rebuildIfNeeded(final DocsServer server, final boolean reloadProject, final File file) {
		final String path = file.getPath().replace(command.getProject().getDirectory().getPath(), "").substring(1);
		boolean reBuild = false;
		for(final String toRebuild : Constants.SERVE_REBUILD_PREFIX) {
			if(path.startsWith(toRebuild)) {
				reBuild = true;
			}
		}
		if(!reBuild) {
			return;
		}

		final Long buildTime = new NewBuildTask(command, reloadProject, this.getOutputStream()).run();
		if(buildTime != null) {
			server.setLastBuild(buildTime);
		}

		outputLine(this.getArguments().manualRebuild ? Constants.SERVE_MANUAL_REBUILD : Constants.SERVE_AUTO_REBUILD);
		blankLine();
	}

	/**
	 * Command arguments.
	 */

	public static class Arguments {

		@Parameter(names = {"-directory", "-d"}, description = "Sets the current serve directory.")
		public String directory;

		@Parameter(names = {"-port", "-p"}, description = "Sets the server port.")
		public int port =  Constants.DEFAULT_PORT;

		@Parameter(names = {"-manualRebuild", "-mr"}, description = "Toggles the manual rebuild.")
		public boolean manualRebuild = true;

	}
	
}