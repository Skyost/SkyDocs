package fr.skyost.skydocs.task.build;

import com.inet.lib.less.Less;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.DocsProject;
import fr.skyost.skydocs.DocsRunnable;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

/**
 * The task that allows to copy assets.
 */

public class CopyAssetsTask extends DocsRunnable<Boolean> {

	/**
	 * The project.
	 */

	private DocsProject project;

	/**
	 * Files that have already been copied and that should not be copied again.
	 */

	private Collection<File> alreadyCopied;

	/**
	 * Whether assets should be minified if possible.
	 */

	private boolean minify;

	/**
	 * Creates a new Task instance.
	 *
	 * @param project The project.
	 * @param alreadyCopied Files that have already been copied and that should not be copied again.
	 * @param minify Whether assets should be minified if possible.
	 */

	public CopyAssetsTask(final DocsProject project, final Collection<File> alreadyCopied, final boolean minify) {
		this(project, alreadyCopied, minify, System.out);
	}

	/**
	 * Creates a new Task instance.
	 *
	 * @param project The project.
	 * @param alreadyCopied Files that have already been copied and that should not be copied again.
	 * @param minify Whether assets should be minified if possible.
	 * @param out The output stream.
	 */

	public CopyAssetsTask(final DocsProject project, final Collection<File> alreadyCopied, final boolean minify, final PrintStream out) {
		super(out, null);

		this.project = project;
		this.alreadyCopied = alreadyCopied;
		this.minify = minify;
	}

	@Override
	public final Boolean execute() throws InterruptionException, IOException {
		output("Copying assets directory...");

		final File buildDirectory = project.getBuildDirectory();
		final File assetsDirectory = new File(project.getThemeDirectory(), Constants.FILE_ASSETS_DIRECTORY);
		if(assetsDirectory.exists() && assetsDirectory.isDirectory()) {
			exitIfInterrupted();
			copyAsset(assetsDirectory, new File(buildDirectory, Constants.FILE_ASSETS_DIRECTORY));
		}

		return true;
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
	 * Returns all files that have already been copied and that should not be copied again.
	 *
	 * @return All files that have already been copied and that should not be copied again.
	 */

	public final Collection<File> getAlreadyCopiedFileList() {
		return alreadyCopied;
	}

	/**
	 * Sets the files that have already been copied and that should not be copied again.
	 *
	 * @param alreadyCopied The files that have already been copied and that should not be copied again.
	 */

	public final void setAlreadyCopiedFileList(final Collection<File> alreadyCopied) {
		this.alreadyCopied = alreadyCopied;
	}

	/**
	 * Returns whether assets should be minified if possible.
	 *
	 * @return Whether assets should be minified if possible.
	 */

	public final boolean shouldMinify() {
		return minify;
	}

	/**
	 * Sets whether assets should be minified if possible.
	 *
	 * @param minify Whether assets should be minified if possible.
	 */

	public final void setShouldMinify(final boolean minify) {
		this.minify = minify;
	}

	/**
	 * Copies an asset (file or directory).
	 *
	 * @param directory The assets directory.
	 * @param destination The destination.
	 *
	 * @throws IOException If any exception occurs while copying assets.
	 */

	private void copyAsset(final File directory, File destination) throws IOException {
		if(directory.isFile()) {
			final String extension = FilenameUtils.getExtension(directory.getName());
			if(extension.equalsIgnoreCase("less") && project.hasLess()) {
				destination = new File(FilenameUtils.removeExtension(destination.getPath()) + ".css");
				Files.write(destination.toPath(), Less.compile(directory, minify).getBytes(StandardCharsets.UTF_8));
				return;
			}

			if(minify && (extension.equalsIgnoreCase("css") || extension.equalsIgnoreCase("js"))) {
				final InputStreamReader input = new InputStreamReader(new FileInputStream(directory), StandardCharsets.UTF_8);
				final OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(destination), StandardCharsets.UTF_8);

				if(extension.equalsIgnoreCase("css")) {
					final CssCompressor compressor = new CssCompressor(input);
					compressor.compress(output, -1);
				}

				else {
					final JavaScriptCompressor compressor = new JavaScriptCompressor(input, null);
					compressor.compress(output, -1, true, false, false, false);
				}

				input.close();
				output.close();
				return;
			}

			Files.copy(directory.toPath(), destination.toPath());
			return;
		}
		destination.mkdirs();
		for(final File file : directory.listFiles()) {
			copyAsset(file, new File(destination, file.getName()));
		}
	}

}