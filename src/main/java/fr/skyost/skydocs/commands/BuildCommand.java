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
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
		try {
			output("Creating build directory and loading theme...");
			firstTime();
			
			if(buildDirectory.exists()) {
				Utils.deleteDirectory(buildDirectory);
			}
			buildDirectory.mkdirs();
			
			final File themeDirectory = project.getThemeDirectory();
			if(!themeDirectory.exists() || !themeDirectory.isDirectory()) {
				themeDirectory.mkdir();
				Utils.extract(Constants.RESOURCE_DEFAULT_THEME_PATH, Constants.RESOURCE_DEFAULT_THEME_DIRECTORY, themeDirectory);
			}
			final DocsTemplate template = new DocsTemplate(null, project);
			
			secondTime();
			printTimeElapsed();
			
			output("Copying and converting files...");
			firstTime();
			
			final HashSet<File> copied = new HashSet<File>();
			
			final boolean lunr = project.getLunrSearch();
			final StringBuilder lunrContent = new StringBuilder();
			
			for(final DocsPage page : project.getPages()) {
				final File file = page.getFile();
				copied.add(file);
				if(!file.exists() || !file.isFile()) {
					continue;
				}
				
				final File destination = page.getBuildDestination(project);
				if(!destination.getParentFile().exists()) {
					destination.getParentFile().mkdirs();
				}
				
				if(lunr) {
					String contentNoHTML = Utils.stripHTML(page.getContent());
					if(contentNoHTML.length() >= 140) {
						contentNoHTML = Ascii.truncate(contentNoHTML, 140, "...");
					}
					final Map<String, Object> header = page.getHeader();
					final String title = header != null && header.containsKey(Constants.KEY_HEADER_TITLE) ? header.get(Constants.KEY_HEADER_TITLE).toString() : StringUtils.capitalize(FilenameUtils.removeExtension(file.getName()));
					lunrContent.append("'" + title.toLowerCase().replace(".", "-").replace("'", "\\'") + "': {" + "title: '" + Utils.stripHTML(title).replace("'", "\\'") + "', " + "content: '" + contentNoHTML.replace("'", "\\'") + "', " + "url: '" + page.getPageRelativeURL().substring(1) + "'" + "}, ");
				}
				
				template.applyTemplate(project, destination, page, null);
			}
			
			if(lunr && lunrContent.length() != 0) {
				final JtwigModel model = project.getTemplate().createModel();
				
				final String lunrContentString = lunrContent.toString();
				model.with(Constants.VARIABLE_LUNR_DATA, "var pages = {" + (lunrContentString.length() > 0 ? lunrContentString.substring(0, lunrContentString.length() - 2) : "") + "};");
				
				final File searchFile = new File(buildDirectory, Constants.RESOURCE_SEARCH_PAGE_FILE);
				Utils.extract(Constants.RESOURCE_SEARCH_PAGE_PATH, Constants.RESOURCE_SEARCH_PAGE_FILE, buildDirectory);
				model.with(Constants.VARIABLE_PAGE, DocsPage.createFromFile(project, searchFile));
				
				Files.write(searchFile.toPath(), JtwigTemplate.fileTemplate(searchFile).render(model).getBytes(StandardCharsets.UTF_8));
				template.applyTemplate(project, searchFile);
			}
			
			final File contentDirectory = Utils.createFileIfNotExist(project.getContentDirectory());
			for(final File content : contentDirectory.listFiles()) {
				copy(copied, content, buildDirectory);
			}
			Utils.extract(Constants.RESOURCE_REDIRECT_LANGUAGE_PATH, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE, buildDirectory);
			
			Files.write(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE).toPath(), JtwigTemplate.fileTemplate(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE)).render(JtwigModel.newModel().with(Constants.VARIABLE_REDIRECTION_URL, project.getDefaultLanguage() + "/")).getBytes(StandardCharsets.UTF_8));
			
			secondTime();
			printTimeElapsed();
			
			final File assetsDirectory = new File(themeDirectory, Constants.FILE_ASSETS_DIRECTORY);
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
		super.run();
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
		output("Loading project from directory \"" + directoryPath + "\"... ");
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
	 */
	
	public final void copy(final HashSet<File> copied, final File file, File destination) throws IOException {
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
	
	public final void copyAssets(final File directory, final File destination) throws IOException {
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