package fr.skyost.skydocs;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import fr.skyost.skydocs.DocsMenu.DocsMenuEntry;
import fr.skyost.skydocs.exceptions.InvalidProjectDataException;
import fr.skyost.skydocs.exceptions.LoadException;
import fr.skyost.skydocs.utils.Utils;

/**
 * Represents a project.
 */

public class DocsProject {
	
	/**
	 * The name of this project.
	 */
	
	private String name;
	
	/**
	 * The description of this project.
	 */
	
	private String description;
	
	/**
	 * The URL of this project.
	 */
	
	private String url;
	
	/**
	 * The default language of this project.
	 */
	
	private String defaultLanguage;
	
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
	 * Other variables of this project.
	 */
	
	private final HashMap<String, Object> projectVariables = new HashMap<String, Object>();
	
	/**
	 * Creates a new DocsProject instance.
	 * 
	 * @param name The name of the project.
	 */
	
	public DocsProject(final String name) {
		this(name, name, "https://skyost.github.io/SkyDocs", Locale.ENGLISH.getLanguage(), Utils.getParentFolder(), null, null, null);
	}
	
	/**
	 * Creates a new DocsProject instance.
	 * 
	 * @param name The name of the project.
	 * @param description The description of the project.
	 * @param url The url of this project.
	 * @param defaultLanguage The default language of this project.
	 * @param directory The directory of this project.
	 * @param pages Pages of this project.
	 * @param menus Menus of this project.
	 * @param projectVariables Other variables to add to the project.
	 */
	
	private DocsProject(final String name, final String description, final String url, final String defaultLanguage, final File directory, final Set<DocsPage> pages, final Set<DocsMenu> menus, final Map<String, Object> projectVariables) {
		this.name = name;
		this.description = description;
		this.url = url;
		this.defaultLanguage = defaultLanguage;
		this.directoryPath = directory.getPath();
		if(pages != null) {
			this.pages.addAll(pages);
		}
		if(menus != null) {
			this.menus.addAll(menus);
		}
		if(projectVariables != null) {
			this.projectVariables.putAll(projectVariables);
		}
	}
	
	/**
	 * Gets the name of this project.
	 * 
	 * @return The name of this project.
	 */
	
	public final String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this project.
	 * 
	 * @param name The new name of this project.
	 */
	
	public final void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Gets the default language of this project.
	 * 
	 * @return The default language of this project.
	 */
	
	public final String getDefaultLanguage() {
		return defaultLanguage;
	}
	
	/**
	 * Sets the default language of this project.
	 * 
	 * @param defaultLanguage The new default language of this project.
	 */
	
	public final void setDefaultLanguage(final String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	
	/**
	 * Gets the description of this project.
	 * 
	 * @return The description of this project.
	 */
	
	public final String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description of this project.
	 * 
	 * @param description The new description of this project.
	 */
	
	public final void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Gets the url of this project.
	 * 
	 * @return The url of this project.
	 */
	
	public final String getURL() {
		return url;
	}
	
	/**
	 * Sets the url of this project.
	 * 
	 * @param url The new url of this project.
	 */
	
	public final void setURL(final String url) {
		this.url = url;
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
	
	public final void removePage(final DocsMenu page) {
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
	 * Gets other project variables.
	 */
	
	public final HashMap<String, Object> getProjectVariables() {
		return projectVariables;
	}
	
	/**
	 * Puts other project variables to this project.
	 * 
	 * @param projectVariables Other project variables.
	 */
	
	public final void putProjectVariables(final Map<String, Object> projectVariables) {
		this.projectVariables.putAll(projectVariables);
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
	 * Creates a new project from a file containing YAML menu data.
	 * 
	 * @param projectData The file containing project data.
	 * 
	 * @return The parsed project.
	 * 
	 * @throws InvalidProjectDataException If an error occurred while parsing the file.
	 */
	
	public final void loadDataFromProjectYML(final File projectData) throws InvalidProjectDataException {
		try {
			final Yaml yaml = new Yaml();
			@SuppressWarnings("unchecked")
			final HashMap<String, Object> data = (HashMap<String, Object>)yaml.load(new FileInputStream(projectData));
			
			if(!data.containsKey(Constants.KEY_PROJECT_NAME)) {
				throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_NAME + "\".");
			}
			setName(data.get(Constants.KEY_PROJECT_NAME).toString());
			
			if(!data.containsKey(Constants.KEY_PROJECT_DESCRIPTION)) {
				throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_DESCRIPTION + "\".");
			}
			setDescription(data.get(Constants.KEY_PROJECT_DESCRIPTION).toString());
			
			if(!data.containsKey(Constants.KEY_PROJECT_URL)) {
				throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_URL + "\".");
			}
			setURL(data.get(Constants.KEY_PROJECT_URL).toString());
			
			if(!data.containsKey(Constants.KEY_PROJECT_LANGUAGE)) {
				throw new InvalidProjectDataException("Missing key \"" + Constants.KEY_PROJECT_LANGUAGE + "\".");
			}
			setDefaultLanguage(data.get(Constants.KEY_PROJECT_LANGUAGE).toString());
			
			putProjectVariables(data);
		}
		catch(final Exception ex) {
			throw new InvalidProjectDataException(ex);
		}
	}
	
	/**
	 * Loads pages from this project's directory.
	 */
	
	private final void loadPages(final File directory) {
		for(final File child : directory.listFiles()) {
			if(child.isDirectory()) {
				loadPages(child);
				continue;
			}
			if(child.getName().endsWith(".md")) {
				addPages(DocsPage.createFromFile(this, child));
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
			
			final DocsProject project = new DocsProject(null);
			project.setDirectory(directory);
			
			final File projectData = new File(directory, Constants.FILE_PROJECT_DATA);
			if(!projectData.exists() || !projectData.isFile()) {
				throw new LoadException(Constants.FILE_PROJECT_DATA + " not found !");
			}
			project.loadDataFromProjectYML(projectData);
			
			for(final File child : directory.listFiles()) {
				final String name = child.getName().toLowerCase();
				if(name.startsWith(Constants.FILE_MENU_PREFIX) && name.endsWith(Constants.FILE_MENU_SUFFIX)) {
					project.addMenus(DocsMenu.loadMenuFromMenuYML(project, child));
				}
			}
			
			project.loadPages(project.getContentDirectory());
			
			if(project.getMenus().size() == 0) {
				project.addMenus(new DocsMenu(project.getDefaultLanguage(), new DocsMenuEntry("No menu.yml found", "#", 0)));
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
			
}