package fr.skyost.skydocs.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

import com.google.common.base.Ascii;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.DocsPage;
import fr.skyost.skydocs.DocsProject;
import fr.skyost.skydocs.DocsTemplate;
import fr.skyost.skydocs.utils.IncludeFileFunction;
import fr.skyost.skydocs.utils.Utils;

/**
 * "build" command.
 */

public class BuildCommand extends Command {
	
	
	/**
	 * First time point.
	 */
	
	private long firstTime = 0L;
	
	/**
	 * Second time point.
	 */
	
	private long secondTime = 0L;
	
	/**
	 * The project's build directory.
	 */
	
	private File buildDirectory;
	
	public BuildCommand(final String... args) {
		super(args);
	}
	
	@Override
	public final void run() {
		try {
			final String[] args = this.getArguments();
			final File directory = new File(args.length > 0 && args[0].length() > 0 ? args[0] : System.getProperty("user.dir"));
			
			System.out.print("Loading project from directory \"" + directory.getPath() + "\"...");
			firstTime();
			
			final DocsProject project = DocsProject.loadFromDirectory(directory);
			setCurrentBuildDirectory(project);
			if(buildDirectory.exists()) {
				Utils.deleteDirectory(buildDirectory);
			}
			buildDirectory.mkdirs();
			
			final File themeDirectory = project.getThemeDirectory();
			if(!themeDirectory.exists() || !themeDirectory.isDirectory()) {
				themeDirectory.mkdir();
				Utils.extract(Constants.RESOURCE_DEFAULT_THEME_PATH, Constants.RESOURCE_DEFAULT_THEME_DIRECTORY, themeDirectory);
			}
			final DocsTemplate template = new DocsTemplate(project.getProjectVariables(), project);
			
			secondTime();
			System.out.println(" Done in " + timeElapsed() + " seconds !");
			
			System.out.print("Copying and converting files...");
			firstTime();
			
			final JtwigModel model = JtwigModel.newModel(project.getProjectVariables());
			model.with(Constants.VARIABLE_PROJECT, project);
			
			final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder.configuration().functions().add(new IncludeFileFunction(project.getThemeDirectory(), model, DocsTemplate.RANGE_FUNCTION)).add(DocsTemplate.RANGE_FUNCTION).and().build();
			
			final List<Extension> extensions = Arrays.asList(TablesExtension.create(), StrikethroughExtension.create(), HeadingAnchorExtension.create());
			final Parser parser = Parser.builder().extensions(extensions).build();
			final HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
			
			final HashSet<File> copied = new HashSet<File>();
			
			final boolean lunr = project.getProjectVariables().containsKey(Constants.KEY_PROJECT_LUNR_SEARCH) && Boolean.valueOf(project.getProjectVariables().get(Constants.KEY_PROJECT_LUNR_SEARCH).toString());
			final StringBuilder lunrContent = new StringBuilder();
			
			for(final DocsPage page : project.getPages()) {
				final File file = page.getFile();
				if(!file.exists() || !file.isFile()) {
					continue;
				}
				
				final File destination = page.getBuildDestination(project);
				if(!destination.getParentFile().exists()) {
					destination.getParentFile().mkdirs();
				}
				
				model.with(Constants.VARIABLE_PAGE, page);
				
				final String[] parts = Utils.separateFileHeader(file);
				final String header = parts[0] != null ? Constants.HEADER_MARK + Utils.LINE_SEPARATOR + parts[0] + Utils.LINE_SEPARATOR + Constants.HEADER_MARK + Utils.LINE_SEPARATOR : "";
				final String content = renderer.render(parser.parse(JtwigTemplate.inlineTemplate(parts[1], configuration).render(model)));
				
				if(lunr) {
					String contentNoHTML = Utils.stripHTML(content);
					if(contentNoHTML.length() >= 140) {
						contentNoHTML = Ascii.truncate(contentNoHTML, 140, "...");
					}
					final String title = parts[0] == null && Utils.decodeFileHeader(parts[0]).containsKey(Constants.KEY_HEADER_TITLE) ? file.getName().replace(".html", "") : Utils.decodeFileHeader(parts[0]).get(Constants.KEY_HEADER_TITLE).toString();
					lunrContent.append("'" + title.toLowerCase().replace(".", "-").replace("'", "\\'") + "': {" + "title: '" + Utils.stripHTML(title).replace("'", "\\'") + "', " + "content: '" + contentNoHTML.replace("'", "\\'") + "', " + "url: '" + page.getPageRelativeURL().substring(1) + "'" + "}, ");
				}
				
				Files.write(destination.toPath(), (header + content).getBytes(StandardCharsets.UTF_8));
				template.applyTemplate(project, destination);
				copied.add(file);
			}
			
			if(lunr && lunrContent.length() != 0) {
				final String lunrContentString = lunrContent.toString();
				model.with(Constants.VARIABLE_LUNR_DATA, "var pages = {" + (lunrContentString.length() > 0 ? lunrContentString.substring(0, lunrContentString.length() - 2) : "") + "};");
				
				final File searchFile = new File(buildDirectory, Constants.RESOURCE_SEARCH_PAGE_FILE);
				Utils.extract(Constants.RESOURCE_SEARCH_PAGE_PATH, Constants.RESOURCE_SEARCH_PAGE_FILE, buildDirectory);
				model.with(Constants.VARIABLE_PAGE, DocsPage.createFromFile(project, searchFile));
				
				Files.write(searchFile.toPath(), JtwigTemplate.fileTemplate(searchFile, configuration).render(model).getBytes(StandardCharsets.UTF_8));
				template.applyTemplate(project, searchFile);
			}
			
			final File contentDirectory = Utils.createFileIfNotExist(project.getContentDirectory());
			for(final File content : contentDirectory.listFiles()) {
				copy(copied, content, buildDirectory);
			}
			Utils.extract(Constants.RESOURCE_REDIRECT_LANGUAGE_PATH, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE, buildDirectory);
			
			Files.write(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE).toPath(), JtwigTemplate.fileTemplate(new File(buildDirectory, Constants.RESOURCE_REDIRECT_LANGUAGE_FILE)).render(JtwigModel.newModel().with(Constants.VARIABLE_REDIRECTION_URL, project.getDefaultLanguage() + "/")).getBytes(StandardCharsets.UTF_8));
			
			secondTime();
			System.out.println(" Done in " + timeElapsed() + " seconds !");
			
			final File assetsDirectory = new File(themeDirectory, Constants.FILE_ASSETS_DIRECTORY);
			if(assetsDirectory.exists() && assetsDirectory.isDirectory()) {
				System.out.print("Copying assets directory...");
				firstTime();
				
				Utils.copyDirectory(assetsDirectory, new File(buildDirectory, Constants.FILE_ASSETS_DIRECTORY));
				
				secondTime();
				System.out.println(" Done in " + timeElapsed() + " seconds !");
			}
			
			System.out.println("Done ! You just have to put the content of \"" + buildDirectory.getPath() + "\" on your web server.");
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		super.run();
	}
	
	/**
	 * Registers the first time point.
	 */
	
	public final void firstTime() {
		firstTime = System.currentTimeMillis();
	}
	
	/**
	 * Registers the second time point.
	 */
	
	public final void secondTime() {
		secondTime = System.currentTimeMillis();
	}
	
	/**
	 * Returns the second elapsed between the two time points.
	 */
	
	public final float timeElapsed() {
		return (float)((secondTime - firstTime) / 1000f);
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
	 * @throws IOException If an exception occurs while copying the file.
	 */
	
	public final void copy(final HashSet<File> copied, final File file, final File destination) throws IOException {
		if(file.isFile()) {
			Files.copy(file.toPath(), destination.toPath());
			return;
		}
		destination.mkdirs();
		for(final File child : file.listFiles()) {
			if(copied.contains(child) || (child.isFile() && child.getName().toLowerCase().endsWith(".md"))) {
				continue;
			}
			copy(copied, child, new File(destination.getPath() + File.separator + file.getName()));
		}
	}
	
}