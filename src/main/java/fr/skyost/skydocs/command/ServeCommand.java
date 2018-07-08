package fr.skyost.skydocs.command;

import com.beust.jcommander.Parameter;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.DocsServer;
import fr.skyost.skydocs.task.serve.FirstBuildTask;
import fr.skyost.skydocs.task.serve.NewBuildTask;
import org.apache.commons.io.monitor.FileAlterationMonitor;

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
	 * The task that allows to run the first build.
	 */

	private NewBuildTask newBuildTask;

	/**
	 * The task that allows to run a new build.
	 */

	private FirstBuildTask firstBuildTask;

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
		final BuildCommand command = new BuildCommand(false, null, arguments.directory == null ? null : new String[]{"-directory", arguments.directory});

		newBuildTask = new NewBuildTask(command, false, out);
		firstBuildTask = new FirstBuildTask(command, arguments.port, out);

		this.setSubTasks(command, newBuildTask, firstBuildTask);
	}
	
	@Override
	public final Boolean execute() {
		final Arguments arguments = this.getArguments();
		blankLine();

		if(!arguments.manualRebuild || this.getScanner() == null) {
			newBuildTask.run();
			server = firstBuildTask.run(false);
			return null;
		}

		boolean firstBuild = true;
		String line = "";
		while(line == null || line.isEmpty()) {
			newBuildTask.setShouldReloadProject(!firstBuild);

			final Long buildTime = newBuildTask.run();
			if(buildTime == null) {
				return null;
			}

			if(server != null) {
				server.setLastBuild(buildTime);
			}

			if(firstBuild) {
				server = firstBuildTask.run();
			}
			firstBuild = false;

			outputLine("Enter nothing to rebuild the website or enter something to stop the server (auto & manual rebuild are enabled) :");
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
		try {
			server.stop();

			final FileAlterationMonitor monitor = firstBuildTask.getMonitor();
			monitor.removeObserver(monitor.getObservers().iterator().next());
			monitor.stop();
		}
		catch(final Exception ex) {
			ex.printStackTrace(this.getOutputStream());
		}

		super.interrupt();
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