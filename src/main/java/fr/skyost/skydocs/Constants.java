package fr.skyost.skydocs;

/**
 * Contains all app constants.
 */

public class Constants {
	
	/**
	 * ==============
	 * APP PROPERTIES
	 * ==============
	 */
	
	/**
	 * App's name.
	 */
	
	public static final String APP_NAME = "SkyDocs";
	
	/**
	 * App's version.
	 */
	
	public static final String APP_VERSION = "v0.1 Beta";
	
	/**
	 * App's authors.
	 */
	
	public static final String APP_AUTHORS = "Skyost";
	
	/**
	 * ========
	 * COMMANDS
	 * ========
	 */
	
	/**
	 * The create a new project command.
	 */
	
	public static final String COMMAND_NEW = "new";
	
	/**
	 * The new command syntax.
	 */
	
	public static final String COMMAND_NEW_SYNTAX = COMMAND_NEW + " [directory] - Creates a new documentation in the specified directory.";
	
	/**
	 * The build a project command.
	 */
	
	public static final String COMMAND_BUILD = "build";
	
	/**
	 * The build command syntax.
	 */
	
	public static final String COMMAND_BUILD_SYNTAX = COMMAND_BUILD + " [directory] - Builds the documentation located in the specified directory.";
	
	/**
	 * The serve a project command.
	 */
	
	public static final String COMMAND_SERVE = "serve";
	
	/**
	 * The serve command syntax.
	 */
	
	public static final String COMMAND_SERVE_SYNTAX = COMMAND_SERVE + " [directory] [port] - Builds the documentation located in the specified directory and serve it on localhost with the specified port.";
	
	/**
	 * The update app command.
	 */
	
	public static final String COMMAND_UPDATE = "update";
	
	/**
	 * The update command syntax.
	 */
	
	public static final String COMMAND_UPDATE_SYNTAX = COMMAND_UPDATE + " - Checks for updates.";
	
	/**
	 * The help command.
	 */
	
	public static final String COMMAND_HELP = "help";
	
	/**
	 * The help command syntax.
	 */
	
	public static final String COMMAND_HELP_SYNTAX = COMMAND_HELP + " [command] - Shows the available commands with their description.";
	
	/**
	 * ================
	 * YAML HEADER KEYS
	 * ================
	 */
	
	/**
	 * The title key.
	 */
	
	public static final String KEY_HEADER_TITLE = "title";
	
	/**
	 * The language key.
	 */
	
	public static final String KEY_HEADER_LANGUAGE = "language";
	
	/**
	 * ==============
	 * YAML MENU KEYS
	 * ==============
	 */
	
	/**
	 * Title of the menu item key.
	 */
	
	public static final String KEY_MENU_TITLE = "title";
	
	/**
	 * Link of the menu item key.
	 */
	
	public static final String KEY_MENU_LINK = "link";
	
	/**
	 * Weight of the menu item key.
	 */
	
	public static final String KEY_MENU_WEIGHT = "weight";
	
	/**
	 * Children of the menu item key.
	 */
	
	public static final String KEY_MENU_CHILDREN = "children";
	
	/**
	 * ==========================
	 * YAML PROJECT KEYS AND TAGS
	 * ==========================
	 */
	
	/**
	 * Project's name key.
	 */
	
	public static final String KEY_PROJECT_NAME = "project_name";
	
	/**
	 * Project's description key.
	 */
	
	public static final String KEY_PROJECT_DESCRIPTION = "project_description";
	
	/**
	 * Project's url key.
	 */
	
	public static final String KEY_PROJECT_URL = "project_url";
	
	/**
	 * Project's default language key.
	 */
	
	public static final String KEY_PROJECT_LANGUAGE = "default_language";
	
	/**
	 * lunr search key.
	 */
	
	public static final String KEY_PROJECT_LUNR_SEARCH = "lunr_search";
	
	/**
	 * =============================
	 * PROJECT FILES AND DIRECTORIES
	 * =============================
	 */
	
	/**
	 * YAML project data file.
	 */
	
	public static final String FILE_PROJECT_DATA = "project.yml";
	
	/**
	 * YAML menu data file prefix.
	 */
	
	public static final String FILE_MENU_PREFIX = "menu";
	
	/**
	 * YAML menu data file suffix.
	 */
	
	public static final String FILE_MENU_SUFFIX = ".yml";
	
	/**
	 * Content directory.
	 */
	
	public static final String FILE_CONTENT_DIRECTORY = "content";
	
	/**
	 * Build directory.
	 */
	
	public static final String FILE_BUILD_DIRECTORY = "build";
	
	/**
	 * Theme directory.
	 */
	
	public static final String FILE_THEME_DIRECTORY = "theme";
	
	/**
	 * Template page file.
	 */
	
	public static final String FILE_THEME_PAGE_FILE = "page.html";
	
	/**
	 * Theme assets directory.
	 */
	
	public static final String FILE_ASSETS_DIRECTORY = "assets";
	
	/**
	 * =========
	 * RESOURCES
	 * =========
	 */
	
	/**
	 * The redirect language file path.
	 */
	
	public static final String RESOURCE_REDIRECT_LANGUAGE_PATH = "main/resources/redirect_language_page/";
	
	/**
	 * The redirect language file.
	 */
	
	public static final String RESOURCE_REDIRECT_LANGUAGE_FILE = "index.html";
	
	/**
	 * The default theme directory path.
	 */
	
	public static final String RESOURCE_DEFAULT_THEME_PATH = "main/resources/";
	
	/**
	 * The default theme directory.
	 */
	
	public static final String RESOURCE_DEFAULT_THEME_DIRECTORY = "default_theme";
	
	/**
	 * The search page file path.
	 */
	
	public static final String RESOURCE_SEARCH_PAGE_PATH = "main/resources/search_page/";
	
	/**
	 * The search page file.
	 */
	
	public static final String RESOURCE_SEARCH_PAGE_FILE = "search.html";
	
	/**
	 * The new project directory path.
	 */
	
	public static final String RESOURCE_NEW_PROJECT_PATH = "main/resources/";
	
	/**
	 * The new project directory.
	 */
	
	public static final String RESOURCE_NEW_PROJECT_DIRECTORY = "new_project";
	
	/**
	 * ==============================
	 * CUSTOM VARIABLES AND FUNCTIONS
	 * ==============================
	 */
	
	/**
	 * The include file function.
	 */
	
	public static final String FUNCTION_INCLUDE_FILE = "includeFile";
	
	/**
	 * The project variable.
	 */
	
	public static final String VARIABLE_PROJECT = "project";
	
	/**
	 * The page variable.
	 */
	
	public static final String VARIABLE_PAGE = "page";
	
	/**
	 * The redirection url variable.
	 */
	
	public static final String VARIABLE_REDIRECTION_URL = "redirectionUrl";
	
	/**
	 * The lunr data variable.
	 */
	
	public static final String VARIABLE_LUNR_DATA = "lunrData";
	
	/**
	 * ======
	 * OTHERS
	 * ======
	 */
	
	/**
	 * Header marks (like YAML front matter).
	 */
	
	public static final String HEADER_MARK = "---";
	
	/**
	 * The default serve port.
	 */
	
	public static final int DEFAULT_PORT = 4444;
	
}