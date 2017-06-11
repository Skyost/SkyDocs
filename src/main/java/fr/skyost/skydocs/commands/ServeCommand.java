package fr.skyost.skydocs.commands;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.utils.Utils;

/**
 * "serve" command.
 */

public class ServeCommand extends Command {
	
	private File buildDirectory;
	
	public ServeCommand(final String... args) {
		super(args);
	}
	
	@Override
	public final void run() {
		try {
			final String[] args = this.getArguments();
			final int port = args.length >= 2 && Utils.parseInt(args[1]) != null ? Utils.parseInt(args[1]) : Constants.DEFAULT_PORT;
			final InternalServer server = new InternalServer(port);
			
			boolean firstBuild = true;
			final Scanner scanner = new Scanner(System.in);
			String line = "";
			while(line.equals("")) {
				System.out.println("Running build command...");
				final BuildCommand command = new BuildCommand(args);
				command.run();
				buildDirectory = command.getCurrentBuildDirectory();
				System.out.println("Done building documentation !");
				
				if(firstBuild) {
					System.out.println("You can point your browser to http://localhost:" + port + "...");
					if(Desktop.isDesktopSupported()) {
						Desktop.getDesktop().browse(new URL("http://localhost:" + port).toURI());
					}
					server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
				}
				firstBuild = false;
				
				System.out.println("Enter nothing to rebuild the website or enter something to stop the server :");
				line = scanner.nextLine();
			}
			scanner.close();
			server.stop();
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		super.run();
	}
	
	/**
	 * The SkyDocs internal server.
	 */
	
	private class InternalServer extends NanoHTTPD {
		
		public InternalServer(final int port) {
			super(port);
		}
		
		@Override
		public final Response serve(final IHTTPSession session) {
			try {
				File file = new File(buildDirectory.getPath() + session.getUri().replace("/", File.separator));
				if(file.isDirectory()) {
					file = new File(file, "index.html");
					if(!file.exists()) {
						final StringBuilder builder = new StringBuilder("<html><body><ul>");
						final File parent = file.getParentFile();
						builder.append("<li><a href=\"" + parent.getPath().replace(buildDirectory.getPath(), "").replace(parent.getName(), "") + "\">..</a></li>");
						for(final File child : parent.listFiles()) {
							builder.append("<li><a href=\"" + child.getPath().replace(buildDirectory.getPath(), "") + "\">" + child.getName() + "</a></li>");
						}
						builder.append("</ul></body></html>");
						return newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_HTML, builder.toString());
					}
				}
				final FileInputStream fileInput = new FileInputStream(file);
				final BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
				return newFixedLengthResponse(Status.OK, NanoHTTPD.getMimeTypeForFile(file.getPath()), bufferedInput, -1);
			}
			catch(final FileNotFoundException ex) {
				return newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "404 File not found.");
			}
			catch(final Exception ex) {
				return newFixedLengthResponse(Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, ex.getClass().getName());
			}
		}
		
	}
	
}