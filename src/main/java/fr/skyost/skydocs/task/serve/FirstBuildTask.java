package fr.skyost.skydocs.task.serve;

import fi.iki.elonen.NanoHTTPD;
import fr.skyost.skydocs.DocsRunnable;
import fr.skyost.skydocs.DocsServer;
import fr.skyost.skydocs.command.ServeCommand;

import java.awt.*;
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

	private ServeCommand command;

	/**
	 * The server port.
	 */

	private int port;

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The serve command.
	 * @param port The server port.
	 */

	public FirstBuildTask(final ServeCommand command, final int port) {
		this(command, port, System.out);
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param command The serve command.
	 * @param port The server port.
	 * @param out The output stream.
	 */

	public FirstBuildTask(final ServeCommand command, final int port, final PrintStream out) {
		super(out, null);

		this.command = command;
		this.port = port;
	}

	@Override
	public final DocsServer execute() throws Exception {
		try {
			outputLine("You can point your browser to http://localhost:" + port + ".");
			blankLine();

			final DocsServer server = new DocsServer(port, command.getBuildCommand().getProject());
			server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

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
	 * Returns the serve command.
	 *
	 * @return The serve command.
	 */

	public final ServeCommand getServeCommand() {
		return command;
	}

	/**
	 * Sets the serve command.
	 *
	 * @param command The serve command.
	 */

	public final void setServeCommand(final ServeCommand command) {
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

}