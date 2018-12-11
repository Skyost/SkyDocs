package fr.skyost.skydocs.task.build;

import com.google.common.base.Ascii;
import fr.skyost.skydocs.*;
import fr.skyost.skydocs.utils.Utils;
import org.jtwig.JtwigTemplate;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The task that allows to create the build directory.
 */

public class ConvertFilesTask extends DocsRunnable<HashSet<File>> {

	/**
	 * The project.
	 */

	private DocsProject project;

	/**
	 * Whether pages should be compressed.
	 */

	private boolean compressPages;

	/**
	 * Creates a new Task instance.
	 *
	 * @param project The project.
	 * @param compressPages Whether pages should be compressed.
	 */

	public ConvertFilesTask(final DocsProject project, final boolean compressPages) {
		this(project, compressPages, System.out);
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param project The project.
	 * @param compressPages Whether pages should be compressed.
	 * @param out The output stream.
	 */

	public ConvertFilesTask(final DocsProject project, final boolean compressPages, final PrintStream out) {
		super(out, null);

		this.project = project;
		this.compressPages = compressPages;
	}

	@Override
	public final HashSet<File> execute() throws InterruptionException, IOException, URISyntaxException {
		output("Copying and converting files...");

		final HashSet<File> copied = new HashSet<>();

		final DocsTemplate template = project.getTemplate();
		final boolean lunr = project.hasLunrSearch();
		final StringBuilder lunrContent = new StringBuilder();

		for(final DocsPage page : project.getPages()) {
			exitIfInterrupted();

			final File file = page.getFile();
			if(!file.exists() || !file.isFile()) {
				continue;
			}

			final File destination = page.getBuildDestination();
			if(!destination.getParentFile().exists()) {
				destination.getParentFile().mkdirs();
			}

			if(lunr) {
				String content = Utils.stripHTML(page.getContent());
				if(content.length() >= 140) {
					content = Ascii.truncate(content, 140, "...");
				}
				lunrContent.append("'" + page.getPageRelativeURL().replace('/', '-') + "': {" + "title: '" + Utils.stripHTML(page.getTitle()).replace("'", "\\'") + "', " + "content: '" + content.replace("'", "\\'") + "', " + "url: '" + page.getPageRelativeURL().substring(1) + "'" + "}, ");
			}

			template.applyTemplate(destination, compressPages, page, null);
			copied.add(file);
		}

		final File buildDirectory = project.getBuildDirectory();
		if(lunr && lunrContent.length() > 0) {
			final String lunrContentString = lunrContent.toString();
			Utils.extract(Constants.RESOURCE_SEARCH_PAGE_PATH, Constants.RESOURCE_SEARCH_PAGE_FILE, buildDirectory);

			final HashMap<String, Object> pageVariables = new HashMap<>();
			pageVariables.put(Constants.VARIABLE_LUNR_DATA, "const PAGES = {" + (lunrContentString.length() > 0 ? lunrContentString.substring(0, lunrContentString.length() - 2) : "") + "};");

			template.applyTemplate(new File(buildDirectory, Constants.RESOURCE_SEARCH_PAGE_FILE), compressPages, null, pageVariables);
		}

		final File contentDirectory = Utils.createFileIfNotExist(project.getContentDirectory());
		for(final File content : contentDirectory.listFiles()) {
			exitIfInterrupted();
			copyFile(copied, content, buildDirectory);
		}
		Utils.extract(Constants.RESOURCE_REDIRECT_LANGUAGE_PATH, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE, buildDirectory);

		Files.write(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE).toPath(), JtwigTemplate.fileTemplate(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE)).render(template.createModel().with(Constants.VARIABLE_REDIRECTION_URL, project.getDefaultLanguage() + "/")).getBytes(StandardCharsets.UTF_8));

		return copied;
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

	/**
	 * Returns whether pages should be compressed.
	 *
	 * @return Whether pages should be compressed.
	 */

	public final boolean shouldCompressPages() {
		return compressPages;
	}

	/**
	 * Sets whether pages should be compressed.
	 *
	 * @param compressPages Whether pages should be compressed.
	 */

	public final void setShouldCompressPages(final boolean compressPages) {
		this.compressPages = compressPages;
	}

	/**
	 * Copy a file or a directory.
	 *
	 * @param copied If a file to copy is already in this list, it will not be copied another time.
	 * @param file The file to copy.
	 * @param destination The destination.
	 *
	 * @throws IOException If any exception occurs while copying a file.
	 */

	private void copyFile(final HashSet<File> copied, final File file, File destination) throws IOException {
		if(copied.contains(file) || (file.isFile() && com.google.common.io.Files.getFileExtension(file.getName()).equalsIgnoreCase("md"))) {
			return;
		}
		if(file.isFile()) {
			try {
				destination = new File(destination, file.getName());
				if(!destination.getParentFile().exists()) {
					destination.getParentFile().mkdirs();
				}
				Files.copy(file.toPath(), destination.toPath());
			}
			catch(final FileAlreadyExistsException ex) {
				blankLine();
				outputLine("The file \"" + file.getPath() + "\" will not be copied because it already exists : \"" + destination.getPath() + "\".");
			}
			return;
		}
		destination.mkdirs();
		for(final File child : file.listFiles()) {
			copyFile(copied, child, new File(destination.getPath() + File.separator + file.getName()));
		}
	}

}