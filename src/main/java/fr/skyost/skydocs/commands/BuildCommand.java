package fr.skyost.skydocs.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import com.google.common.base.Ascii;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.DocsPage;
import fr.skyost.skydocs.DocsProject;
import fr.skyost.skydocs.DocsTemplate;
import fr.skyost.skydocs.exceptions.LoadException;
import fr.skyost.skydocs.utils.Utils;

/**
 * "build" command.
 */

public class BuildCommand extends Command {
	
	/**
	 * If you are in production mode.
	 */
	
	private final boolean prod;
	
	/**
	 * The project's directory path.
	 */
	
	private final String directoryPath;
	
	/**
	 * The project's build directory.
	 * 
	 * @throws LoadException If the project can't be loaded from the specified directory.
	 */
	
	private File buildDirectory;
	
	/**
	 * The current project.
	 */
	
	private DocsProject project;
	
	public BuildCommand(final boolean prod, final String... args) throws LoadException {
		super(args);
		
		this.prod = prod;
		directoryPath = args.length > 0 && args[0].length() > 0 ? args[0] : System.getProperty("user.dir");
		reloadProject();
	}
	
	@Override
	public final void run() {
		super.run();
		
		try {
			output("Creating build directory...");
			firstTime();
			
			if(buildDirectory.exists() && buildDirectory.isDirectory()) {
				Utils.deleteDirectory(buildDirectory);
			}
			buildDirectory.mkdirs();
			
			secondTime();
			printTimeElapsed();
			
			exitIfInterrupted();
			
			output("Copying and converting files...");
			firstTime();
			
			final HashSet<File> copied = new HashSet<File>();
			
			final DocsTemplate template = project.getTemplate();
			final boolean lunr = project.getLunrSearch();
			final StringBuilder lunrContent = new StringBuilder();
			
			for(final DocsPage page : project.getPages()) {
				exitIfInterrupted();
				
				final File file = page.getFile();
				if(!file.exists() || !file.isFile()) {
					continue;
				}
				
				final File destination = page.getBuildDestination(project);
				if(!destination.getParentFile().exists()) {
					destination.getParentFile().mkdirs();
				}
				
				if(lunr) {
					final String title = page.getTitle();
					String content = Utils.stripHTML(page.getContent());
					if(content.length() >= 140) {
						content = Ascii.truncate(content, 140, "...");
					}
					lunrContent.append("'" + title.toLowerCase().replace(".", "-").replace("'", "\\'") + "': {" + "title: '" + Utils.stripHTML(title).replace("'", "\\'") + "', " + "content: '" + content.replace("'", "\\'") + "', " + "url: '" + page.getPageRelativeURL().substring(1) + "'" + "}, ");
				}
				
				template.applyTemplate(destination, page, null);
				copied.add(file);
			}
			
			if(lunr && lunrContent.length() != 0) {
				final String lunrContentString = lunrContent.toString();
				Utils.extract(Constants.RESOURCE_SEARCH_PAGE_PATH, Constants.RESOURCE_SEARCH_PAGE_FILE, buildDirectory);
				
				final HashMap<String, Object> pageVariables = new HashMap<String, Object>();
				pageVariables.put(Constants.VARIABLE_LUNR_DATA, "var pages = {" + (lunrContentString.length() > 0 ? lunrContentString.substring(0, lunrContentString.length() - 2) : "") + "};");
				
				template.applyTemplate(new File(buildDirectory, Constants.RESOURCE_SEARCH_PAGE_FILE), null, pageVariables);
			}
			
			final File contentDirectory = Utils.createFileIfNotExist(project.getContentDirectory());
			for(final File content : contentDirectory.listFiles()) {
				copy(copied, content, buildDirectory);
			}
			Utils.extract(Constants.RESOURCE_REDIRECT_LANGUAGE_PATH, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE, buildDirectory);
			
			Files.write(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE).toPath(), JtwigTemplate.fileTemplate(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE)).render(JtwigModel.newModel().with(Constants.VARIABLE_REDIRECTION_URL, project.getDefaultLanguage() + "/")).getBytes(StandardCharsets.UTF_8));
			
			secondTime();
			printTimeElapsed();
			
			final File assetsDirectory = new File(project.getThemeDirectory(), Constants.FILE_ASSETS_DIRECTORY);
			if(assetsDirectory.exists() && assetsDirectory.isDirectory()) {
				output("Copying assets directory...");
				firstTime();
				
				copyAssets(assetsDirectory, new File(buildDirectory, Constants.FILE_ASSETS_DIRECTORY));
				
				secondTime();
				printTimeElapsed();
			}
			
			output("Done ! You just have to put the content of \"" + buildDirectory.getPath() + "\" on your web server.");
		}
		catch(final Exception ex) {
			printStackTrace(ex);
		}
		
		exitIfNeeded();
	}
	
	@Override
	public final boolean isInterruptible() {
		return true;
	}
	
	/**
	 * Checks if the command is in prod mode.
	 * 
	 * @return Whether the command is in prod mode.
	 */
	
	public final boolean isProdMode() {
		return prod;
	}
	
	/**
	 * Reloads the project (pages, menus, ...).
	 * 
	 * @throws LoadException If the project can't be (re)loaded.
	 */
	
	public final void reloadProject() throws LoadException {
		output("Loading project from directory \"" + directoryPath + "\" and loading theme... ");
		firstTime();
		
		project = DocsProject.loadFromDirectory(new File(directoryPath));
		setCurrentBuildDirectory(project);
		
		secondTime();
		printTimeElapsed();
	}
	
	/**
	 * Gets the current project.
	 * 
	 * @return The current project.
	 */
	
	public final DocsProject getProject() {
		return project;
	}
	
	/**
	 * Sets the current project.
	 * 
	 * @return project The new project.
	 */
	
	public final void setProject(final DocsProject project) {
		this.project = project;
	}
	
	/**
	 * Gets the current build directory.
	 * 
	 * @return The current build directory.
	 */
	
	public final File getCurrentBuildDirectory() {
		return buildDirectory;
	}
	
	/**
	 * Change the current build directory according to the specified project.
	 * 
	 * @param project The project.
	 */
	
	public final void setCurrentBuildDirectory(final DocsProject project) {
		buildDirectory = new File(project.getDirectory(), Constants.FILE_BUILD_DIRECTORY);
	}
	
	/**
	 * Copy a file or a directory.
	 * 
	 * @param copied If a file to copy is already in this list, it will not be copied another time.
	 * @param file The file to copy.
	 * @param destination The destination.
	 * 
	 * @throws IOException If any exception occurs while copying a file.
	 * @throws InterruptionException If the operation should be aborted.
	 */
	
	public final void copy(final HashSet<File> copied, final File file, File destination) throws IOException, InterruptionException {
		exitIfInterrupted();
		
		if(copied.contains(file) || (file.isFile() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("md"))) {
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
			copy(copied, child, new File(destination.getPath() + File.separator + file.getName()));
		}
	}
	
	/**
	 * Copies needed assets.
	 * 
	 * @param directory The assets directory.
	 * @param destination The destination.
	 * 
	 * @throws IOException If any exception occurs while copying assets.
	 * @throws InterruptionException If the operation should be aborted.
	 */
	
	public final void copyAssets(final File directory, final File destination) throws IOException, InterruptionException {
		exitIfInterrupted();
		
		if(directory.isFile()) {
			final String extension = FilenameUtils.getExtension(directory.getName());
			if(prod && (extension.equalsIgnoreCase("css") || extension.equalsIgnoreCase("js"))) {
				try {
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
				catch(final Exception ex) {
					printStackTrace(ex);
					outputLine("Failed to minify \"" + directory.getPath() + "\" !");
				}
			}
			Files.copy(directory.toPath(), destination.toPath());
			return;
		}
		destination.mkdirs();
		for(final File file : directory.listFiles()) {
			copyAssets(file, new File(destination, file.getName()));
		}
	}
	
}