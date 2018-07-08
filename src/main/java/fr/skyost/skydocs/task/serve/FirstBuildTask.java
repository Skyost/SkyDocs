package fr.skyost.skydocs.task.serve;

import fi.iki.elonen.NanoHTTPD;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.DocsRunnable;
import fr.skyost.skydocs.DocsServer;
import fr.skyost.skydocs.command.BuildCommand;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.net.BindException;
import java.net.URL;

/**
 * The task that allows to run the first build.
 */

public class FirstBuildTask extends DocsRunnable<DocsServer> {

	/**
	 * The build command.
	 */

	private BuildCommand command;

	/**
	 * The server port.
	 */

	private int port;

	/**
	 * The current file monitor.
	 */

	private FileAlterationMonitor monitor;

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The build command.
	 * @param port The server port.
	 */

	public FirstBuildTask(final BuildCommand command, final int port) {
		this(command, port, System.out);
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The build command.
	 * @param port The server port.
	 * @param out The output stream.
	 */

	public FirstBuildTask(final BuildCommand command, final int port, final PrintStream out) {
		this(command, port, out, new FileAlterationMonitor(Constants.SERVE_FILE_POLLING_INTERVAL));
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The build command.
	 * @param port The server port.
	 * @param out The output stream.
	 * @param monitor The file monitor.
	 */

	public FirstBuildTask(final BuildCommand command, final int port, final PrintStream out, final FileAlterationMonitor monitor) {
		super(out, null);

		this.command = command;
		this.port = port;
		this.monitor = monitor;
	}

	@Override
	public final DocsServer execute() throws Exception {
		try {
			outputLine("You can point your browser to http://localhost:" + port + ".");
			blankLine();

			final DocsServer server = new DocsServer(port, command.getProject());
			server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

			registerFileListener(server);
			if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URL("http://localhost:" + port).toURI());
			}

			return server;
		}
		catch(final BindException ex) {
			outputLine("A binding error occurred. Maybe the port " + port + " is already in use ?");
			throw new Exception(ex);
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
	 * Returns the port.
	 *
	 * @return The port.
	 */

	public final int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port The port.
	 */

	public final void setPort(final int port) {
		this.port = port;
	}

	/**
	 * Returns the monitor.
	 *
	 * @return The monitor.
	 */

	public final FileAlterationMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Sets the monitor.
	 *
	 * @param monitor The monitor.
	 */

	public final void setMonitor(final FileAlterationMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Creates and registers a file listener with the help of the specified build command.
	 *
	 * @param server The current docs server.
	 *
	 * @throws Exception If any exception occurs.
	 */

	private void registerFileListener(final DocsServer server) throws Exception {
		final FileAlterationObserver observer = new FileAlterationObserver(command.getProject().getDirectory());
		observer.addListener(new FileAlterationListenerAdaptor() {

			@Override
			public final void onDirectoryChange(final File directory) {
				rebuildIfNeeded(command, server, directory);
			}

			@Override
			public final void onFileChange(final File file) {
				rebuildIfNeeded(command, server, file);
			}

		});
		monitor.addObserver(observer);
		monitor.start();
	}

	/**
	 * Rebuilds the project if needed.
	 *
	 * @param command The build command (we need it to build the project).
	 * @param server The docs server.
	 * @param file The file that has changed.
	 */

	private void rebuildIfNeeded(final BuildCommand command, final DocsServer server, final File file) {
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

		final Long buildTime = new NewBuildTask(command, false).run();
		if(buildTime == null) {
			return;
		}

		server.setLastBuild(buildTime);
		blankLine();
		// TODO Show message "auto rebuild enabled"
	}

}