package fr.skyost.skydocs;

import com.google.common.io.Files;
import fr.skyost.skydocs.DocsMenu.DocsMenuEntry;
import fr.skyost.skydocs.exception.InvalidProjectDataException;
import fr.skyost.skydocs.exception.InvalidTemplateException;
import fr.skyost.skydocs.exception.LoadException;
import fr.skyost.skydocs.utils.Utils;
import fr.skyost.skydocs.utils.Utils.Pair;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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
	
	private final HashSet<DocsPage> pages = new HashSet<>();
	
	/**
	 * Menus of this project.
	 */
	
	private final HashSet<DocsMenu> menus = new HashSet<>();
	
	/**
	 * Template of this project.
	 */
	
	private DocsTemplate template;
	
	/**
	 * Other variables of this project.
	 */
	
	private final HashMap<String, Object> projectVariables = new HashMap<>();
	
	/**
	 * Creates a new DocsProject instance.
	 * 
	 * @param projectVariables The prject.yml variables.
	 * @param directory The project's directory.
	 * 
	 * @throws InvalidProjectDataException If the supplied project variables are invalid.
	 * @throws IOException If an exception occurs while creating the template.
	 * @throws InvalidTemplateException If an exception occurs while creating the template.
	 */
	
	public DocsProject(final Map<String, Object> projectVariables, final File directory) throws InvalidProjectDataException, InvalidTemplateException, IOException {
		this(projectVariables, directory, null, null, null);
		if(projectVariables == null) {
			throw new InvalidProjectDataException("Invalid project data.");
		}
	}
	
	/**
	 * Creates a new DocsProject instance.
	 * 
	 * @param projectVariables The project.yml variables.
	 * @param directory The project's directory.
	 * @param pages Pages of this project.
	 * @param menus Menus of this project.
	 * @param template Template of this project.
	 * 
	 * @throws IOException If an exception occurs while creating the template.
	 * @throws InvalidTemplateException If an exception occurs while creating the template.
	 */
	
	private DocsProject(final Map<String, Object> projectVariables, final File directory, final Set<DocsPage> pages, final Set<DocsMenu> menus, final DocsTemplate template) throws InvalidTemplateException, IOException {
		this.setDirectory(directory);
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
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_NAME)) {
			setName("My Documentation");
		}
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
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_DESCRIPTION)) {
			setDescription("Documentation built with " + Constants.APP_NAME + ".");
		}
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
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_URL)) {
			setURL(Constants.APP_WEBSITE);
		}
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
		if(!projectVariables.containsKey(Constants.KEY_PROJECT_LANGUAGE)) {
			setDefaultLanguage(Locale.ENGLISH.getLanguage());
		}
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
	 * Gets if the default order should be alphabetical for this project.
	 * 
	 * @return If the default order should be alphabetical for this project.
	 */
	
	public final boolean isDefaultOrderAlphabetical() {
		return projectVariables.containsKey(Constants.KEY_PROJECT_DEFAULT_ORDER_ALPHABETICAL) && Boolean.TRUE.equals(Utils.parseBoolean(projectVariables.get(Constants.KEY_PROJECT_DEFAULT_ORDER_ALPHABETICAL).toString()));
	}
	
	/**
	 * Sets if the default order should be alphabetical for this project.
	 * 
	 * @param defaultOrderAlphabetical Whether the default order should be alphabetical for this project.
	 */
	
	public final void setDefaultOrderAlphabetical(final boolean defaultOrderAlphabetical) {
		projectVariables.put(Constants.KEY_PROJECT_DEFAULT_ORDER_ALPHABETICAL, defaultOrderAlphabetical);
	}
	
	/**
	 * Gets if lunr search should be enabled for this project.
	 * 
	 * @return If lunr search should be enabled for this project.
	 */
	
	@SuppressWarnings("deprecation")
	public final boolean hasLunrSearch() {
		if(projectVariables.containsKey(Constants.KEY_PROJECT_LUNR_SEARCH)) {
			return !Boolean.FALSE.equals(Utils.parseBoolean(projectVariables.get(Constants.KEY_PROJECT_LUNR_SEARCH).toString()));
		}
		return !projectVariables.containsKey(Constants.KEY_PROJECT_ENABLE_LUNR) || !Boolean.FALSE.equals(Utils.parseBoolean(projectVariables.get(Constants.KEY_PROJECT_ENABLE_LUNR).toString()));
	}
	
	/**
	 * Sets if lunr search should be enabled for this project.
	 * 
	 * @param enable If lunr search should be enabled for this project.
	 */
	
	@SuppressWarnings("deprecation")
	public final void setLunrSearch(final boolean enable) {
		projectVariables.put(Constants.KEY_PROJECT_LUNR_SEARCH, enable);
		projectVariables.put(Constants.KEY_PROJECT_ENABLE_LUNR, enable);
	}
	
	/**
	 * Gets if minification in production mode should be enabled for this project.
	 * 
	 * @return If minification in production mode should be enabled for this project.
	 */
	
	public final boolean hasMinification() {
		if(projectVariables.containsKey(Constants.KEY_PROJECT_ENABLE_MINIFICATION)) {
			return !Boolean.FALSE.equals(Utils.parseBoolean(projectVariables.get(Constants.KEY_PROJECT_ENABLE_MINIFICATION).toString()));
		}
		return true;
	}
	
	/**
	 * Sets if minification in production mode should be enabled for this project.
	 * 
	 * @param enable If minification in production mode should be enabled for this project.
	 */
	
	public final void setMinification(final boolean enable) {
		projectVariables.put(Constants.KEY_PROJECT_ENABLE_MINIFICATION, enable);
	}
	
	/**
	 * Gets if less compilation should be enabled for this project.
	 * 
	 * @return If less compilation should be enabled for this project.
	 */
	
	public final boolean hasLess() {
		return !projectVariables.containsKey(Constants.KEY_PROJECT_ENABLE_LESS) || !Boolean.FALSE.equals(Utils.parseBoolean(projectVariables.get(Constants.KEY_PROJECT_ENABLE_LESS).toString()));
	}
	
	/**
	 * Sets if less compilation should be enabled for this project.
	 * 
	 * @param enable If less compilation should be enabled for this project.
	 */
	
	public final void setLess(final boolean enable) {
		projectVariables.put(Constants.KEY_PROJECT_ENABLE_LESS, enable);
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
		this.directoryPath = directory == null ? System.getProperty("user.dir") : directory.getPath();
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
	 * Gets all pages of this project for the specified language.
	 * 
	 * @param language The language.
	 * 
	 * @return The pages of this project for the specified language.
	 */
	
	public final Set<DocsPage> getPages(final String language) {
		final HashSet<DocsPage> pages = new HashSet<>();
		for(final DocsPage page : this.pages) {
			if(!page.getLanguage().equals(language)) {
				continue;
			}
			pages.add(page);
		}
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
	 * @return The menu corresponding to the specified language. If not found, will return the default menu.
	 */
	
	public final DocsMenu getMenuByLanguage(final String language) {
		if(menus.isEmpty()) {
			return null;
		}
		for(final DocsMenu menu : menus) {
			if(menu.getLanguage().equals(language)) {
				return menu;
			}
		}
		return getMenuByLanguage(getDefaultLanguage());
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

			if(menu == null) {
				return null;
			}
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
			return "The \"" + Constants.FILE_PROJECT_DATA + "\" file does not contains the specified key \"" + key + "\".";
		}
		return projectVariables.get(key);
	}
	
	/**
	 * Gets the build directory of this project.
	 * 
	 * @return The build directory.
	 */
	
	public final File getBuildDirectory() {
		return new File(directoryPath, Constants.FILE_BUILD_DIRECTORY);
	}
	
	/**
	 * Gets the content directory of this project.
	 * 
	 * @return The content directory.
	 */
	
	public final File getContentDirectory() {
		return new File(directoryPath, Constants.FILE_CONTENT_DIRECTORY);
	}
	
	/**
	 * Gets the theme directory of this project.
	 * 
	 * @return The theme directory.
	 */
	
	public final File getThemeDirectory() {
		return new File(directoryPath, Constants.FILE_THEME_DIRECTORY);
	}

	/**
	 * Returns whether modifying this file should trigger a project reload.
	 *
	 * @param modifiedFile The file.
	 *
	 * @return Whether modifying this file should trigger a project reload.
	 */

	public final boolean shouldReloadProject(final File modifiedFile) {
		if(!modifiedFile.exists() || !modifiedFile.isFile()) {
			return false;
		}

		final String name = modifiedFile.getName();
		return name.endsWith(".yml") || name.endsWith(".html");
	}
	
	/**
	 * Loads pages from the specified directory.
	 * 
	 * @param directory The directory.
	 * 
	 * @return A set of files that are not going to be copied because they already exist in the destination.
	 * 
	 * @throws LoadException If the specified file is not a directory.
	 */
	
	private HashSet<String> loadPages(final File directory) throws LoadException {
		return loadPages(directory, new HashSet<>());
	}
	
	/**
	 * Loads pages from the specified directory.
	 * 
	 * @param directory The directory.
	 * @param destinations Already added pages destinations.
	 * 
	 * @return A set of files that are not going to be copied because they already exist in the destination.
	 * 
	 * @throws LoadException If the specified file is not a directory.
	 */
	
	private HashSet<String> loadPages(final File directory, final HashSet<String> destinations) throws LoadException {
		if(!directory.isDirectory()) {
			throw new LoadException("The file \"" + directory + "\" is not a directory.");
		}
		final HashSet<String> alreadyExist = new HashSet<>();
		for(final File child : directory.listFiles()) {
			if(child.isDirectory()) {
				loadPages(child, destinations);
				continue;
			}
			if(Files.getFileExtension(child.getName()).equalsIgnoreCase("md")) {
				final DocsPage page = new DocsPage(this, child);
				final String path = page.getBuildDestinationPath();
				if(destinations.contains(path)) {
					alreadyExist.add(path);
					continue;
				}
				destinations.add(path);
				addPages(page);
			}
		}
		return alreadyExist;
	}
	
	/**
	 * Loads a complete project from a specified directory.
	 * 
	 * @param directory The directory.
	 * 
	 * @return 0 : The loaded project. 1 : A HashSet<String> of pages that can't be copied.
	 * 
	 * @throws LoadException If an exception occurs while loading the project.
	 */
	
	public static Pair<DocsProject, Set<String>> loadFromDirectory(final File directory) throws LoadException {
		try {
			if(!directory.exists()) {
				throw new LoadException("The directory \"" + directory + "\" does not exist.");
			}
			if(!directory.isDirectory()) {
				throw new LoadException("The file \"" + directory.getPath() + "\" is not a directory.");
			}
			
			final File projectData = new File(directory, Constants.FILE_PROJECT_DATA);
			if(!projectData.exists() || !projectData.isFile()) {
				throw new LoadException(directory.getPath() + File.separator + Constants.FILE_PROJECT_DATA + " not found !");
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
					project.addMenus(DocsMenu.createFromFile(project, child));
				}
			}
			
			final HashSet<String> alreadyExist = project.loadPages(project.getContentDirectory());
			if(project.isDefaultOrderAlphabetical()) {
				final List<DocsPage> pages = new ArrayList<>(project.getPages());
				Collections.sort(pages);
				final int size = pages.size();
				for(int i = 0; i != pages.size(); i++) {
					final DocsPage page = pages.get(i);
					if(!page.hasPreviousPage() && i > 0) {
						final DocsPage previous = pages.get(i - 1);
						if(page.getLanguage().equals(previous.getLanguage())) {
							page.setPreviousPage(previous.getPageRelativeURL());
						}
					}
					if(!page.hasNextPage() && i < size - 1) {
						final DocsPage next = pages.get(i + 1);
						if(page.getLanguage().equals(next.getLanguage())) {
							page.setNextPage(next.getPageRelativeURL());
						}
					}
				}
			}
			
			if(project.getMenus().isEmpty()) {
				project.addMenus(new DocsMenu(project.getDefaultLanguage(), new DocsMenuEntry("No \"" + Constants.FILE_MENU_PREFIX + Constants.FILE_MENU_SUFFIX + "\" found", "#", 0, false)));
			}
			if(project.getMenuByLanguage(project.getDefaultLanguage()) == null) {
				final DocsMenu defaultMenu = project.getMenus().iterator().next();
				project.addMenus(new DocsMenu(project.getDefaultLanguage(), defaultMenu.getEntries().toArray(new DocsMenuEntry[defaultMenu.getEntries().size()])));
			}
			
			Utils.createFileIfNotExist(project.getContentDirectory());
			return new Pair<>(project, alreadyExist);
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
	
	private static DocsProject createFromFile(final File file) throws InvalidProjectDataException {
		try {
			final Yaml yaml = new Yaml();
			final HashMap<String, Object> data = yaml.load(new FileInputStream(file));

			return new DocsProject(data, file.getParentFile());
		}
		catch(final Exception ex) {
			throw new InvalidProjectDataException(ex);
		}
	}
			
}