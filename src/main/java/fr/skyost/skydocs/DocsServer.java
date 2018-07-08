package fr.skyost.skydocs;

import fi.iki.elonen.NanoHTTPD;
import fr.skyost.skydocs.utils.Utils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

/**
 * The SkyDocs internal server.
 */

public class DocsServer extends NanoHTTPD {

	/**
	 * Last build time in millis.
	 */

	private long lastBuild = -1L;

	private DocsProject project;

	public DocsServer(final int port, final DocsProject project) {
		super(port);

		this.project = project;
	}

	@Override
	public final Response serve(final IHTTPSession session) {
		try {
			final String currentUri = session.getUri();
			if(currentUri.equals("/" + Constants.SERVE_LASTBUILD_URL) || currentUri.equals("/" + Constants.SERVE_LASTBUILD_URL + "/")) {
				return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, String.valueOf(lastBuild));
			}

			final File buildDirectory = project.getBuildDirectory();
			File file = new File(buildDirectory.getPath() + currentUri.replace("/", File.separator));
			if(file.isDirectory()) {
				if(!currentUri.endsWith("/")) {
					final Response response = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "Redirecting you...");
					response.addHeader("Location", currentUri + "/");
					return response;
				}
				file = new File(file, "index.html");
				if(!file.exists()) {
					final Utils.AutoLineBreakStringBuilder builder = new Utils.AutoLineBreakStringBuilder("<!DOCTYPE html>");
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
					return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, content);
				}
			}
			final FileInputStream fileInput = new FileInputStream(file);
			final BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
			return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.getMimeTypeForFile(file.getPath()), bufferedInput, -1);
		}
		catch(final NoSuchFileException ex) {
			return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "<html><head><title>404 Error</title></head><body>404 File not found." + Utils.AUTO_REFRESH_SCRIPT + "</body></html>");
		}
		catch(final FileNotFoundException ex) {
			return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "<html><head><title>404 Error</title></head><body>404 File not found (or inaccessible)." + Utils.AUTO_REFRESH_SCRIPT + "</body></html>");
		}
		catch(final Exception ex) {
			return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "<html><head><title>Error</title></head><body>" + ex.getClass().getName() + Utils.AUTO_REFRESH_SCRIPT + "</body></html>");
		}
	}

	public final DocsProject getProject() {
		return project;
	}

	public final void setProject(final DocsProject project) {
		this.project = project;
	}

	public final long getLastBuild() {
		return lastBuild;
	}

	public final void setLastBuild(final long lastBuild) {
		this.lastBuild = lastBuild;
	}

}