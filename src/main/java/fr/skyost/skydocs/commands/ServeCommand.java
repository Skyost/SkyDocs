package fr.skyost.skydocs.commands;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.utils.Utils;

/**
 * "serve" command.
 */

public class ServeCommand extends Command {
	
	/**
	 * The project's build directory.
	 */
	
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
			final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
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
			System.out.println();
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
				final String currentUri = session.getUri();
				File file = new File(buildDirectory.getPath() + currentUri.replace("/", File.separator));
				if(file.isDirectory()) {
					if(!currentUri.endsWith("/")) {
						final Response response = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "Redirecting you...");
						response.addHeader("Location", currentUri + "/");
						return response;
					}
					file = new File(file, "index.html");
					if(!file.exists()) {
						final StringBuilder builder = new StringBuilder("<!DOCTYPE html><html>");
						builder.append("<head><title>" + currentUri + "</title></head>");
						builder.append("<body><h1>" + currentUri + "</h1><hr/><ul>");
						builder.append("<li><a href=\"../\">..</a></li>");
						for(final File child : file.getParentFile().listFiles()) {
							String path = child.getPath().replace(buildDirectory.getPath(), "").replace(File.separator, "/");
							if(child.isDirectory()) {
								path += "/";
							}
							builder.append("<li><a href=\"" + path + "\">" + child.getName() + "</a></li>");
						}
						builder.append("</ul><hr/>");
						builder.append("<p style=\"text-align:right;\"><a href=\"" + Constants.APP_WEBSITE + "\">" + Constants.APP_NAME + " " + Constants.APP_VERSION + "</a></p>");
						builder.append("</body></html>");
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