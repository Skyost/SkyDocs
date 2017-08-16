package fr.skyost.skydocs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.Yaml;

import fr.skyost.skydocs.DocsMenu.DocsMenuEntry;
import fr.skyost.skydocs.exceptions.InvalidProjectDataException;
import fr.skyost.skydocs.exceptions.InvalidTemplateException;
import fr.skyost.skydocs.exceptions.LoadException;
import fr.skyost.skydocs.utils.Utils;

/**
 * Represents a project.
 */

public class DocsProject {
	
	/**
	 * The directory path of this project.
	 */
	
	private String directoryPath;
	
	/**
	 * Pages of this project.
	 */
	
	private final HashSet<DocsPage> pages = new HashSet<DocsPage>();
	
	/**
	 * Menus of this project.
	 */
	
	private final HashSet<DocsMenu> menus = new HashSet<DocsMenu>();
	
	/**
	 * Template of this project.
	 */
	
	private DocsTemplate template;
	
	/**
	 * Other variables of this project.
	 */
	
	private final HashMap<String, Object> projectVariables = new HashMap<String, Object>();
	
	/**
	 * Creates a new DocsProject instance.
	 * 
	 * @param projectVariables The prject.yml variables.
	 * @param directory The project's dirctory.
	 * 
	 * @throws InvalidProjectDataException If the projectVariables supplied are invalid.
	 * @throws IOException If an exception occurs while creating the template.
	 * @throws InvalidTemplateException If an exception occurs while creating the template.
	 */
	
	public DocsProject(final Map<String, Object> projectVariables, final File directory) throws InvalidProjectDataException, InvalidTemplateException, IOException {
		this(projectVariables, directory, null, null, null);
		if(projectVariables == null) {
			throw new InvalidProjectDataException("Invalid project data.");
		}
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_NAME)) {
			throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_NAME + "\".");
		}
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_DESCRIPTION)) {
			throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_DESCRIPTION + "\".");
		}
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_URL)) {
			throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_URL + "\".");
		}
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_LANGUAGE)) {
			throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_LANGUAGE + "\".");
		}
	}
	
	/**
	 * Creates a new DocsProject instance.
	 * 
	 * @param projectVariables The project.yml variables.
	 * @param directory The project's dirctory.
	 * @param pages Pages of this project.
	 * @param menus Menus of this project.
	 * @param template Template of this project.
	 * 
	 * @throws InvalidProjectDataException If the projectVariables supplied are invalid.
	 * @throws IOException If an exception occurs while creating the template.
	 * @throws InvalidTemplateException If an exception occurs while creating the template.
	 */
	
	private DocsProject(final Map<String, Object> projectVariables, final File directory, final Set<DocsPage> pages, final Set<DocsMenu> menus, final DocsTemplate template) throws InvalidProjectDataException, InvalidTemplateException, IOException {
		this.directoryPath = directory == null ? System.getProperty("user.dir") : directory.getPath();
		if(pages != null) {
			this.pages.addAll(pages);
		}
		if(menus != null) {
			this.menus.addAll(menus);
		}
		if(projectVariables != null) {
			this.projectVariables.putAll(projectVariables);
		}
		this.template = template == null ? new DocsTemplate(null, this) : template;
	}
	
	/**
	 * Gets the name of this project.
	 * 
	 * @return The name of this project.
	 */
	
	public final String getName() {
		return projectVariables.get(Constants.KEY_PROJECT_NAME).toString();
	}
	
	/**
	 * Sets the name of this project.
	 * 
	 * @param name The new name of this project.
	 */
	
	public final void setName(final String name) {
		projectVariables.put(Constants.KEY_PROJECT_NAME, name);
	}
	
	/**
	 * Gets the description of this project.
	 * 
	 * @return The description of this project.
	 */
	
	public final String getDescription() {
		return projectVariables.get(Constants.KEY_PROJECT_DESCRIPTION).toString();
	}
	
	/**
	 * Sets the description of this project.
	 * 
	 * @param description The new description of this project.
	 */
	
	public final void setDescription(final String description) {
		projectVariables.put(Constants.KEY_PROJECT_DESCRIPTION, description);
	}
	
	/**
	 * Gets the url of this project.
	 * 
	 * @return The url of this project.
	 */
	
	public final String getURL() {
		return projectVariables.get(Constants.KEY_PROJECT_URL).toString();
	}
	
	/**
	 * Sets the url of this project.
	 * 
	 * @param url The new url of this project.
	 */
	
	public final void setURL(final String url) {
		projectVariables.put(Constants.KEY_PROJECT_URL, url);
	}
	
	/**
	 * Gets the default language of this project.
	 * 
	 * @return The default language of this project.
	 */
	
	public final String getDefaultLanguage() {
		return projectVariables.get(Constants.KEY_PROJECT_LANGUAGE).toString();
	}
	
	/**
	 * Sets the default language of this project.
	 * 
	 * @param defaultLanguage The new default language of this project.
	 */
	
	public final void setDefaultLanguage(final String defaultLanguage) {
		projectVariables.put(Constants.KEY_PROJECT_LANGUAGE, defaultLanguage);
	}
	
	/**
	 * Gets if lunr search should be enabled for this project.
	 * 
	 * @return If lunr search should be enabled for this project.
	 */
	
	public final boolean getLunrSearch() {
		return projectVariables.containsKey(Constants.KEY_PROJECT_LUNR_SEARCH) && Boolean.valueOf(projectVariables.get(Constants.KEY_PROJECT_LUNR_SEARCH).toString());
	}
	
	/**
	 * Sets the lunr search variable of this project.
	 * 
	 * @param defaultLanguage The search variable of this project.
	 */
	
	public final void setLunrSearch(final boolean lunrSearch) {
		projectVariables.put(Constants.KEY_PROJECT_LUNR_SEARCH, lunrSearch);
	}
	
	/**
	 * Gets the directory of this project.
	 * 
	 * @return The directory of this project.
	 */
	
	public final File getDirectory() {
		return new File(directoryPath);
	}
	
	/**
	 * Sets the directory of this project.
	 * 
	 * @param directory The new directory of this project.
	 */
	
	public final void setDirectory(final File directory) {
		this.directoryPath = directory.getPath();
	}
	
	/**
	 * Gets all pages of this project.
	 * 
	 * @return The pages of this project.
	 */
	
	public final Set<DocsPage> getPages() {
		return pages;
	}
	
	/**
	 * Adds pages to this project.
	 * 
	 * @param pages Pages to add.
	 */
	
	public final void addPages(final DocsPage... pages) {
		this.pages.addAll(Arrays.asList(pages));
	}
	
	/**
	 * Removes a page from this project.
	 * 
	 * @param page Page to remove.
	 */
	
	public final void removePage(final DocsPage page) {
		pages.remove(page);
	}
	
	/**
	 * Clears all pages from this project.
	 */
	
	public final void clearPages() {
		pages.clear();
	}
	
	/**
	 * Gets a menu by its language.
	 * 
	 * @param language The language.
	 * 
	 * @return The menu corresponding to the specified language.
	 */
	
	public final DocsMenu getMenuByLanguage(final String language) {
		for(final DocsMenu menu : menus) {
			if(menu.getLanguage().equals(language)) {
				return menu;
			}
		}
		return null;
	}
	
	/**
	 * Gets a menu HTML content by its language.
	 * 
	 * @param language The language.
	 * 
	 * @return The menu HTML content corresponding to the specified language (or the default language if not found).
	 */
	
	public final String getMenuHTMLByLanguage(final String language) {
		DocsMenu menu = getMenuByLanguage(language);
		if(menu == null) {
			menu = getMenuByLanguage(getDefaultLanguage());
		}
		return menu.toHTML();
	}
	
	/**
	 * Gets all menus of this project.
	 * 
	 * @return The menus of this project.
	 */
	
	public final Set<DocsMenu> getMenus() {
		return menus;
	}
	
	/**
	 * Adds menus to this project.
	 * 
	 * @param menus Menus to add.
	 */
	
	public final void addMenus(final DocsMenu... menus) {
		this.menus.addAll(Arrays.asList(menus));
	}
	
	/**
	 * Removes a menu from this project.
	 * 
	 * @param menu Menu to remove.
	 */
	
	public final void removeMenu(final DocsMenu menu) {
		menus.remove(menu);
	}
	
	/**
	 * Clears all menus from this project.
	 */
	
	public final void clearMenus() {
		menus.clear();
	}
	
	/**
	 * Gets the template of this project.
	 * 
	 * @return The template of this project.
	 */
	
	public final DocsTemplate getTemplate() {
		return template;
	}
	
	/**
	 * Sets the template of this project.
	 * 
	 * @param template The template of this project.
	 */
	
	public final void setTemplate(final DocsTemplate template) {
		this.template = template;
	}
	
	/**
	 * Gets other project variables.
	 */
	
	public final HashMap<String, Object> getProjectVariables() {
		return projectVariables;
	}
	
	/**
	 * Gets the field (put by the user in the menu.yml).
	 * 
	 * @param key The key.
	 * 
	 * @return If found, the corresponding value.
	 */
	
	public final Object getField(final String key) {
		if(!projectVariables.containsKey(key)) {
			return "The project.yml file does not contains the specified key \"" + key + "\".";
		}
		return projectVariables.get(key);
	}
	
	/**
	 * Gets the build directory of this project.
	 * 
	 * @return The build directory.
	 */
	
	public final File getBuildDirectory() {
		return new File(directoryPath + File.separator + Constants.FILE_BUILD_DIRECTORY);
	}
	
	/**
	 * Gets the content directory of this project.
	 * 
	 * @return The content directory.
	 */
	
	public final File getContentDirectory() {
		return new File(directoryPath + File.separator + Constants.FILE_CONTENT_DIRECTORY);
	}
	
	/**
	 * Gets the theme directory of this project.
	 * 
	 * @return The theme directory.
	 */
	
	public final File getThemeDirectory() {
		return new File(directoryPath + File.separator + Constants.FILE_THEME_DIRECTORY);
	}
	
	/**
	 * Loads pages from the specified directory.
	 * 
	 * @param directory The directory.
	 * 
	 * @throws LoadException If the specified file is not a directory.
	 */
	
	private final void loadPages(final File directory) throws LoadException {
		loadPages(directory, new HashSet<String>());
	}
	
	/**
	 * Loads pages from the specified directory.
	 * 
	 * @param directory The directory.
	 * @param destinations Already added pages destinations.
	 * 
	 * @throws LoadException If the specified file is not a directory.
	 */
	
	private final void loadPages(final File directory, final HashSet<String> destinations) throws LoadException {
		if(!directory.isDirectory()) {
			throw new LoadException("The file \"" + directory + "\" is not a directory.");
		}
		for(final File child : directory.listFiles()) {
			if(child.isDirectory()) {
				loadPages(child, destinations);
				continue;
			}
			if(FilenameUtils.getExtension(child.getName()).equalsIgnoreCase("md")) {
				final DocsPage page = DocsPage.createFromFile(this, child);
				final String path = page.getBuildDestinationPath(this);
				if(destinations.contains(path)) {
					System.out.println();
					System.out.println("The file \"" + child.getPath() + "\" has a file with the same name in its build directory \"" + path + "\". Therefore it will not be copied.");
					continue;
				}
				destinations.add(path);
				addPages(page);
			}
		}
	}
	
	/**
	 * Loads a complete project from a specified directory.
	 * 
	 * @param directory The directory.
	 * 
	 * @return The loaded project.
	 * 
	 * @throws LoadException If an exception occurs while loading the project.
	 */
	
	public static final DocsProject loadFromDirectory(final File directory) throws LoadException {
		try {
			if(!directory.exists()) {
				throw new LoadException("The directory \"" + directory + "\" does not exist.");
			}
			if(!directory.isDirectory()) {
				throw new LoadException("The file \"" + directory + "\" is not a directory.");
			}
			
			final File projectData = new File(directory, Constants.FILE_PROJECT_DATA);
			if(!projectData.exists() || !projectData.isFile()) {
				throw new LoadException(Constants.FILE_PROJECT_DATA + " not found !");
			}
			
			final File themeDirectory = new File(directory, Constants.FILE_THEME_DIRECTORY);
			if(!themeDirectory.exists() || !themeDirectory.isDirectory()) {
				themeDirectory.mkdir();
				Utils.extract(Constants.RESOURCE_DEFAULT_THEME_PATH, Constants.RESOURCE_DEFAULT_THEME_DIRECTORY, themeDirectory);
			}
			
			final DocsProject project = DocsProject.createFromFile(projectData);
			project.setDirectory(directory);
			
			for(final File child : directory.listFiles()) {
				final String name = child.getName().toLowerCase();
				if(name.startsWith(Constants.FILE_MENU_PREFIX) && name.endsWith(Constants.FILE_MENU_SUFFIX)) {
					project.addMenus(DocsMenu.loadMenuFromMenuYML(project, child));
				}
			}
			
			project.loadPages(project.getContentDirectory());
			
			if(project.getMenus().size() == 0) {
				project.addMenus(new DocsMenu(project.getDefaultLanguage(), new DocsMenuEntry("No menu.yml found", "#", 0, false)));
			}
			if(project.getMenuByLanguage(project.getDefaultLanguage()) == null) {
				final DocsMenu defaultMenu = project.getMenus().iterator().next();
				project.addMenus(new DocsMenu(project.getDefaultLanguage(), defaultMenu.getEntries().toArray(new DocsMenuEntry[defaultMenu.getEntries().size()])));
			}
			
			Utils.createFileIfNotExist(project.getContentDirectory());
			
			return project;
		}
		catch(final Exception ex) {
			throw new LoadException(ex);
		}
	}
	
	/**
	 * Creates a DocsProject instance from a file.
	 * 
	 * @param file The page's content.
	 * 
	 * @return The DocsProject instance.
	 * @throws InvalidProjectDataException If the YAML file is invalid.
	 */
	
	private static final DocsProject createFromFile(final File file) throws InvalidProjectDataException {
		try {
			final Yaml yaml = new Yaml();
			@SuppressWarnings("unchecked")
			final HashMap<String, Object> data = (HashMap<String, Object>)yaml.load(new FileInputStream(file));

			return new DocsProject(data, file.getParentFile());
		}
		catch(final Exception ex) {
			throw new InvalidProjectDataException(ex);
		}
	}
			
}