package fr.skyost.skydocs.commands;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.utils.GithubUpdater;
import fr.skyost.skydocs.utils.GithubUpdater.GithubUpdaterResultListener;

/**
 * "update" command.
 */

public class UpdateCommand extends Command implements GithubUpdaterResultListener {
	
	@Override
	public final void run() {
		new GithubUpdater(Constants.APP_VERSION.split(" ")[0].substring(1), this).start();
		super.run();
	}

	@Override
	public final void updaterStarted() {
		outputLine("Checking for updates...");
	}

	@Override
	public final void updaterException(final Exception ex) {
		printStackTrace(ex);
		outputLine("Error while checking for updates :");
	}

	@Override
	public final void updaterResponse(final String response) {
		outputLine("Bad response while checking for updates : " + response);
	}

	@Override
	public final void updaterUpdateAvailable(final String localVersion, final String remoteVersion) {
		outputLine("An update is available : v" + remoteVersion + " !");
		outputLine("Heads to https://github.com/" + GithubUpdater.UPDATER_GITHUB_USERNAME + "/" + GithubUpdater.UPDATER_GITHUB_REPO + "/releases to download it !");
	}

	@Override
	public final void updaterNoUpdate(final String localVersion, final String remoteVersion) {
		outputLine("No update available.");
	}
	
}