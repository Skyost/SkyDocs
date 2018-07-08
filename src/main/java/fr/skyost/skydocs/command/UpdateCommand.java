package fr.skyost.skydocs.command;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.utils.GithubUpdater;
import fr.skyost.skydocs.utils.GithubUpdater.GithubUpdaterResultListener;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * "update" command.
 */

public class UpdateCommand extends Command<Void> implements GithubUpdaterResultListener {

	/**
	 * Creates a new Command instance.
	 **/

	public UpdateCommand() {
		this(System.out, System.in);
	}

	/**
	 * Creates a new Command instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 */

	public UpdateCommand(final PrintStream out, final InputStream in) {
		super(out, in, null, null);
	}

	@Override
	protected Boolean execute() {
		new GithubUpdater(Constants.APP_VERSION.split(" ")[0].substring(1), this).start();
		return true;
	}

	@Override
	public final void updaterStarted() {
		outputLine("Checking for updates...");
	}

	@Override
	public final void updaterException(final Exception ex) {
		outputLine("Error while checking for updates :");
		ex.printStackTrace(this.getOutputStream());
	}

	@Override
	public final void updaterResponse(final String response) {}

	@Override
	public final void updaterUpdateAvailable(final String localVersion, final String remoteVersion) {
		outputLine("An update is available : v" + remoteVersion + " !");
		outputLine("Head to https://github.com/" + GithubUpdater.UPDATER_GITHUB_USERNAME + "/" + GithubUpdater.UPDATER_GITHUB_REPO + "/releases/latest to download it !");
	}

	@Override
	public final void updaterNoUpdate(final String localVersion, final String remoteVersion) {
		outputLine("No update available.");
	}
	
}