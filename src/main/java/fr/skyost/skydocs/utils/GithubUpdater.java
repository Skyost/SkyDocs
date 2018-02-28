package fr.skyost.skydocs.utils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * An update checker based on Github releases.
 * 
 * @author Skyost.
 */

public class GithubUpdater extends Thread {
	
	public static final String UPDATER_NAME = "GithubUpdater";
	public static final String UPDATER_VERSION = "0.1.2";
	
	public static final String UPDATER_GITHUB_USERNAME = "Skyost";
	public static final String UPDATER_GITHUB_REPO = "SkyDocs";
		
	private final String localVersion;
	private final GithubUpdaterResultListener caller;
	
	/**
	 * Creates a new <b>GithubUpdater</b> instance.
	 * 
	 * @param localVersion The local version.
	 * @param caller The caller.
	 */
	
	public GithubUpdater(final String localVersion, final GithubUpdaterResultListener caller) {
		this.localVersion = localVersion;
		this.caller = caller;
	}
	
	@Override
	public final void run() {
		caller.updaterStarted();
		try {
			final HttpURLConnection connection = (HttpURLConnection)new URL("https://api.github.com/repos/" + UPDATER_GITHUB_USERNAME + "/" + UPDATER_GITHUB_REPO + "/releases/latest").openConnection();
			connection.addRequestProperty("User-Agent", UPDATER_NAME + " v" + UPDATER_VERSION);
			
			final String response = connection.getResponseCode() + " " + connection.getResponseMessage();
			caller.updaterResponse(response);
			
			final InputStream input = response.startsWith("2") ? connection.getInputStream() : connection.getErrorStream();
			final InputStreamReader inputStreamReader = new InputStreamReader(input, StandardCharsets.UTF_8);
			final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			final JsonObject latest = Json.parse(bufferedReader.readLine()).asObject();
			input.close();
			inputStreamReader.close();
			bufferedReader.close();
			
			final String remoteVersion = latest.getString("tag_name", "v0").substring(1);
			if(compareVersions(remoteVersion, localVersion)) {
				caller.updaterUpdateAvailable(localVersion, remoteVersion);
			}
			else {
				caller.updaterNoUpdate(localVersion, remoteVersion);
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			caller.updaterException(ex);
		}
	}
	
	/**
	 * Compares two versions.
	 * 
	 * @param versionTo The version you want to compare to.
	 * @param versionWith The version you want to compare with.
	 * 
	 * @return <b>true</b> If <b>versionTo</b> is inferior than <b>versionWith</b>.
	 * <br><b>false</b> If <b>versionTo</b> is superior or equals to <b>versionWith</b>.
	 */
	
	private static boolean compareVersions(final String versionTo, final String versionWith) {
		return normalisedVersion(versionTo, ".", 4).compareTo(normalisedVersion(versionWith, ".", 4)) > 0;
	}
	
	/**
	 * Gets the formatted name of a version.
	 * <br>Used for the method <b>compareVersions(...)</b> of this class.
	 * 
	 * @param version The version you want to format.
	 * @param separator The separator between the numbers of this version.
	 * @param maxWidth The max width of the formatted version.
	 * 
	 * @return A string which the formatted version of your version.
	 * 
	 * @author Peter Lawrey.
	 */

	private static String normalisedVersion(final String version, final String separator, final int maxWidth) {
		final StringBuilder stringBuilder = new StringBuilder();
		for(final String normalised : Pattern.compile(separator, Pattern.LITERAL).split(version)) {
			stringBuilder.append(String.format("%" + maxWidth + 's', normalised));
		}
		return stringBuilder.toString();
	}
	
	public interface GithubUpdaterResultListener {
		
		/**
		 * When the updater starts.
		 */
		
		public void updaterStarted();
		
		/**
		 * When an Exception occurs.
		 * 
		 * @param ex The Exception.
		 */
		
		public void updaterException(final Exception ex);
		
		/**
		 * The response of the request.
		 * 
		 * @param response The response.
		 */
		
		public void updaterResponse(final String response);
		
		/**
		 * If an update is available.
		 * 
		 * @param localVersion The local version (used to create the updater).
		 * @param remoteVersion The remote version.
		 */
		
		public void updaterUpdateAvailable(final String localVersion, final String remoteVersion);
		
		/**
		 * If there is no update.
		 * 
		 * @param localVersion The local version (used to create the updater).
		 * @param remoteVersion The remote version.
		 */
		
		public void updaterNoUpdate(final String localVersion, final String remoteVersion);
		
	}

}