package fr.skyost.skydocs;

import fr.skyost.BuildConfig;

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
	 * App's state.
	 */

	public static final String APP_STATE = "Beta";
	
	/**
	 * App's version.
	 */
	
	public static final String APP_VERSION = "v" + BuildConfig.VERSION + " " + APP_STATE;
	
	/**
	 * App's authors.
	 */
	
	public static final String APP_AUTHORS = "Skyost";
	
	/**
	 * App's website.
	 */
	
	public static final String APP_WEBSITE = "https://skydocs.skyost.eu";
	
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
	
	public static final String COMMAND_NEW_SYNTAX = COMMAND_NEW + " -directory [directory] - Creates a new documentation in the specified directory.";
	
	/**
	 * The build a project command.
	 */
	
	public static final String COMMAND_BUILD = "build";
	
	/**
	 * The build command syntax.
	 */
	
	public static final String COMMAND_BUILD_SYNTAX = COMMAND_BUILD + " -directory [directory] - Builds the documentation located in the specified directory.";
	
	/**
	 * The serve a project command.
	 */
	
	public static final String COMMAND_SERVE = "serve";
	
	/**
	 * The serve command syntax.
	 */
	
	public static final String COMMAND_SERVE_SYNTAX = COMMAND_SERVE + " -directory [directory] -port [port] -manualRebuild [true|false] - Builds the documentation located in the specified directory and serve it on localhost with the specified port.";
	
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
	
	public static final String COMMAND_HELP_SYNTAX = COMMAND_HELP + " -command [command] - Shows the available command with their description.";
	
	/**
	 * The GUI command.
	 */
	
	public static final String COMMAND_GUI = "gui";
	
	/**
	 * The GUI command syntax.
	 */
	
	public static final String COMMAND_GUI_SYNTAX = COMMAND_GUI + " - Opens up a GUI.";
	
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
	 * The previous key.
	 */
	
	public static final String KEY_HEADER_PREVIOUS = "previous";
	
	/**
	 * The next key.
	 */
	
	public static final String KEY_HEADER_NEXT = "next";
	
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
	 * Whether the menu item should be opened in a new tab.
	 */
	
	public static final String KEY_MENU_NEW_TAB = "new_tab";
	
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
	 * Whether the default order should be alphabetical.
	 */
	
	public static final String KEY_PROJECT_DEFAULT_ORDER_ALPHABETICAL = "default_order_alphabetical";
	
	/**
	 * Whether lunr search is enabled.
	 */
	
	public static final String KEY_PROJECT_ENABLE_LUNR = "enable_lunr";
	
	/**
	 * Whether minification is enabled in production mode.
	 */
	
	public static final String KEY_PROJECT_ENABLE_MINIFICATION = "enable_minification";
	
	/**
	 * Whether less compilation is enabled.
	 */
	
	public static final String KEY_PROJECT_ENABLE_LESS = "enable_less";
	
	/**
	 * lunr search key.
	 */
	
	@Deprecated
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
	 * GUI history.
	 */
	
	public static final String FILE_GUI_HISTORY = "history";
	
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
	 * The project's icon location. No need to use another icon as it is already stored somewhere in the application.
	 */
	
	public static final String RESOURCE_PROJECT_ICON = "/main/resources/default_theme/assets/img/icon.png";
	
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
	 * The range function.
	 */
	
	public static final String FUNCTION_RANGE = "range";
	
	/**
	 * The SkyDocs variable.
	 */
	
	public static final String VARIABLE_GENERATOR_NAME = "generator_name";
	
	/**
	 * The SkyDocs version.
	 */
	
	public static final String VARIABLE_GENERATOR_VERSION = "generator_version";
	
	/**
	 * The SkyDocs website.
	 */
	
	public static final String VARIABLE_GENERATOR_WEBSITE = "generator_website";
	
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
	 * ==============
	 * SERVE COMMMAND
	 * ==============
	 */
	
	/**
	 * This will be the interval between two files check.
	 */
	
	public static final long SERVE_FILE_POLLING_INTERVAL = 2 * 1000L;
	
	/**
	 * Modifying one of this file or folder will results in a re-build.
	 */
	
	public static final String[] SERVE_REBUILD_PREFIX = new String[]{FILE_CONTENT_DIRECTORY, FILE_THEME_DIRECTORY, FILE_MENU_PREFIX, FILE_PROJECT_DATA};
	
	/**
	 * Going to http://localhost:port/SERVE_LASTBUILD_URL will print the last build date in milliseconds.
	 */
	
	public static final String SERVE_LASTBUILD_URL = "lastbuild";
	
	/**
	 * ============
	 * GUI ELEMENTS
	 * ============
	 */
	
	/**
	 * The frame's title.
	 */
	
	public static final String GUI_FRAME_TITLE = APP_NAME + " " + APP_VERSION + " - GUI";
	
	/**
	 * The create button text.
	 */
	
	public static final String GUI_BUTTON_CREATE = "Create project...";
	
	/**
	 * The add button text.
	 */
	
	public static final String GUI_BUTTON_OPEN = "Open project...";
	
	/**
	 * The remove button text.
	 */
	
	public static final String GUI_BUTTON_REMOVE = "Remove project";
	
	/**
	 * The build button text.
	 */
	
	public static final String GUI_BUTTON_BUILD = "Build project";
	
	/**
	 * The serve button text.
	 */
	
	public static final String GUI_BUTTON_SERVE = "Serve project";
	
	/**
	 * The stop button text.
	 */
	
	public static final String GUI_BUTTON_STOP = "Stop";
	
	/**
	 * The file chooser description.
	 */
	
	public static final String GUI_CHOOSER_DESCRIPTION = "Project data (" + FILE_PROJECT_DATA + ")";
	
	/**
	 * The error dialog message.
	 */
	
	public static final String GUI_DIALOG_ERROR_MESSAGE = "An error occurred :\n%s";
	
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

	/**
	 * Auto rebuild message.
	 */

	public static final String SERVE_AUTO_REBUILD = "Press CTRL+C to quit (auto rebuild is enabled) :";

	/**
	 * Manual & auto rebuild message.
	 */

	public static final String SERVE_MANUAL_REBUILD = "Enter nothing to rebuild the website or enter something to stop the server (auto & manual rebuild are enabled). You can also press CTRL+C to quit :";

}