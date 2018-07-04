package fr.skyost.skydocs.command;

import com.beust.jcommander.Parameter;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.DocsProject;
import fr.skyost.skydocs.exception.LoadException;
import fr.skyost.skydocs.utils.Utils;
import fr.skyost.skydocs.utils.Utils.AutoLineBreakStringBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.BindException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

/**
 * "serve" command.
 */

public class ServeCommand extends Command {
	
	/**
	 * The command arguments.
	 */

	private Arguments arguments;

	/**
	 * Current build directory.
	 */

	private File buildDirectory;
	
	/**
	 * Last build time in millis.
	 */
	
	private long lastBuild;
	
	/**
	 * The current file monitor.
	 */
	
	private final FileAlterationMonitor monitor = new FileAlterationMonitor(Constants.SERVE_FILE_POLLING_INTERVAL);
	
	/**
	 * The nanohttpd server.
	 */
	
	private final InternalServer server;
	
	public ServeCommand(final String... args) {
		super(args);

		this.server = new InternalServer(arguments.port);
	}
	
	@Override
	public final void run() {
		super.run();
		try {
			final BuildCommand command = new BuildCommand(false, this.getOut(), arguments.directory == null ? null : new String[]{"-directory", arguments.directory});
			command.setOutputing(false);
			blankLine();
			
			if(!arguments.manualRebuild) {
				newBuild(command, true);
				firstBuild(command);
				return;
			}
			
			boolean firstBuild = true;
			final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
			String line = "";
			while(line.equals("")) {
				newBuild(command, firstBuild);
				if(firstBuild) {
					firstBuild(command);
				}
				firstBuild = false;
				outputLine("Enter nothing to rebuild the website or enter something to stop the server (auto rebuild is enabled) :");
				blankLine();
				
				if(isInterrupted()) {
					break;
				}
				
				line = scanner.hasNextLine() && !isInterrupted() ? scanner.nextLine() : " ";
			}
			scanner.close();
		}
		catch(final BindException bindException) {
			printStackTrace(bindException);
			outputLine("A binding error occurred. Maybe the port " + arguments.port + " is already in use ?");
			broadcastCommandError(bindException);
		}
		catch(final Exception exception) {
			printStackTrace(exception);
			broadcastCommandError(exception);
		}
		exitIfNeeded();
	}
	
	@Override
	public final boolean isInterruptible() {
		return true;
	}
	
	@Override
	public final void interrupt() {
		try {
			server.stop();
			monitor.removeObserver(monitor.getObservers().iterator().next());
			monitor.stop();
		}
		catch(final Exception ex) {
			printStackTrace(ex);
			broadcastCommandError(ex);
		}
		super.interrupt();
	}

	@Override
	public final Arguments getArguments() {
		if(arguments == null) {
			arguments = new Arguments();
		}

		return arguments;
	}
	
	/**
	 * Triggers first build events.
	 * 
	 * @param command The current build command.
	 * 
	 * @throws Exception If any exception occurs.
	 */
	
	private void firstBuild(final BuildCommand command) throws Exception {
		registerFileListener(command);
		buildDirectory = command.getCurrentBuildDirectory();
		outputLine("You can point your browser to http://localhost:" + arguments.port + ".");
		blankLine();
		server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop().browse(new URL("http://localhost:" + arguments.port).toURI());
		}
	}
	
	/**
	 * Triggers a new build.
	 * 
	 * @param command The build command.
	 * @param firstBuild If this is the first build (if not, the command will reload the project).
	 * 
	 * @throws LoadException If an error occurs while loading the project.
	 */
	
	private void newBuild(final BuildCommand command, final boolean firstBuild) throws LoadException {
		output("Running build command...");
		firstTime();
		
		if(!command.isInterrupted()) {
			command.interrupt();
		}
		if(!firstBuild) {
			command.reloadProject();
		}
		if(isInterrupted()) {
			return;
		}
		command.run();
		if(isInterrupted()) {
			return;
		}
		lastBuild = System.currentTimeMillis();

		secondTime();
		printTimeElapsed();
	}
	
	/**
	 * Creates and registers a file listener with the help of the specified build command.
	 * 
	 * @param command The build command.
	 * 
	 * @throws Exception If any exception occurs.
	 */
	
	private void registerFileListener(final BuildCommand command) throws Exception {
		final DocsProject project = command.getProject();
		
		final FileAlterationObserver observer = new FileAlterationObserver(project.getDirectory());
		observer.addListener(new FileAlterationListenerAdaptor() {
			
			@Override
			public final void onDirectoryChange(final File directory) {
				rebuildIfNeeded(command, directory);
			}
			
			@Override
			public final void onFileChange(final File file) {
				rebuildIfNeeded(command, file);
			}
			
		});
		monitor.addObserver(observer);
		monitor.start();
	}
	
	/**
	 * Rebuilds the project if needed.
	 * 
	 * @param command The build command (we need it to build the project).
	 * @param file The file that has changed.
	 */
	
	private void rebuildIfNeeded(final BuildCommand command, final File file) {
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
		try {
			newBuild(command, false);
			outputLine("Enter nothing to rebuild the website or enter something to stop the server (auto rebuild is enabled) :");
			blankLine();
		}
		catch(final Exception ex) {
			printStackTrace(ex);
			outputLine("Unable to build the project !");
			broadcastCommandError(ex);
		}
	}
	
	/**
	 * The SkyDocs internal server.
	 */
	
	private class InternalServer extends NanoHTTPD {
		
		private InternalServer(final int port) {
			super(port);
		}
		
		@Override
		public final Response serve(final IHTTPSession session) {
			try {
				final String currentUri = session.getUri();
				if(currentUri.equals("/" + Constants.SERVE_LASTBUILD_URL) || currentUri.equals("/" + Constants.SERVE_LASTBUILD_URL + "/")) {
					return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, String.valueOf(lastBuild));
				}
				File file = new File(buildDirectory.getPath() + currentUri.replace("/", File.separator));
				if(file.isDirectory()) {
					if(!currentUri.endsWith("/")) {
						final Response response = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "Redirecting you...");
						response.addHeader("Location", currentUri + "/");
						return response;
					}
					file = new File(file, "index.html");
					if(!file.exists()) {
						final AutoLineBreakStringBuilder builder = new AutoLineBreakStringBuilder("<!DOCTYPE html>");
						builder.append("<html>");
						builder.append("	<head>");
						builder.append("		<title>" + currentUri + "</title>");
						builder.append("	</head>");
						builder.append("	<body>");
						builder.append("		<h1>" + currentUri + "</h1>");
						builder.append("		<hr/>");
						builder.append("		<ul>");
						builder.append("			<li><a href=\"../\">..</a></li>");
						for(final File child : file.getParentFile().listFiles()) {
							String path = child.getPath().replace(buildDirectory.getPath(), "").replace(File.separator, "/");
							if(child.isDirectory()) {
								path += "/";
							}
							builder.append("			<li><a href=\"" + path + "\">" + child.getName() + "</a></li>");
						}
						builder.append("		</ul>");
						builder.append("		<hr/>");
						builder.append("		<p style=\"text-align:right;\"><a href=\"" + Constants.APP_WEBSITE + "\">" + Constants.APP_NAME + " " + Constants.APP_VERSION + "</a></p>");
						builder.append(Utils.AUTO_REFRESH_SCRIPT);
						builder.append("	</body>");
						builder.append("</html>");
						return newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_HTML, builder.toString());
					}
				}
				if(FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("html")) {
					String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
					if(content.contains("<body>") && content.contains("</body>")) {
						content = content.replace("</body>", Utils.AUTO_REFRESH_SCRIPT + "</body>");
						return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, content);
					}
				}
				final FileInputStream fileInput = new FileInputStream(file);
				final BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
				return newFixedLengthResponse(Status.OK, NanoHTTPD.getMimeTypeForFile(file.getPath()), bufferedInput, -1);
			}
			catch(final NoSuchFileException ex) {
				return newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "<html><head><title>404 Error</title></head><body>404 File not found." + Utils.AUTO_REFRESH_SCRIPT + "</body></html>");
			}
			catch(final FileNotFoundException ex) {
				return newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "<html><head><title>404 Error</title></head><body>404 File not found (or inaccessible)." + Utils.AUTO_REFRESH_SCRIPT + "</body></html>");
			}
			catch(final Exception ex) {
				return newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "<html><head><title>Error</title></head><body>" + ex.getClass().getName() + Utils.AUTO_REFRESH_SCRIPT + "</body></html>");
			}
		}
		
	}

	/**
	 * Command arguments.
	 */

	public class Arguments {

		@Parameter(names = {"-directory", "-d"}, description = "Sets the current serve directory.")
		public String directory;

		@Parameter(names = {"-port", "-p"}, description = "Sets the server port.")
		public int port =  Constants.DEFAULT_PORT;

		@Parameter(names = {"-manualRebuild", "-mr"}, description = "Toggles the manual rebuild.")
		public boolean manualRebuild = true;

	}
	
}