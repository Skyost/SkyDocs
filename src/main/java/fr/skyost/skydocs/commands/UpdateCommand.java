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
		super.run();
		new GithubUpdater(Constants.APP_VERSION.split(" ")[0].substring(1), this).start();
		exitIfNeeded();
	}
	
	@Override
	public final boolean isInterruptible() {
		return false;
	}

	@Override
	public final void updaterStarted() {
		outputLine("Checking for updates...");
	}

	@Override
	public final void updaterException(final Exception ex) {
		outputLine("Error while checking for updates :");
		printStackTrace(ex);
		broadcastCommandError(ex);
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

	@Override
	public final Object getArguments() {
		return null;
	}
	
}