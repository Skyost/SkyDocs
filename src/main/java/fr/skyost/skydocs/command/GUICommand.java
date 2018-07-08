package fr.skyost.skydocs.command;

import fr.skyost.skydocs.frame.ProjectsFrame;

import javax.swing.*;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * "gui" command.
 */

public class GUICommand extends Command<Void> {

	/**
	 * Creates a new Command instance.
	 *
	 * @param args User arguments.
	 */

	public GUICommand(final String... args) {
		this(System.out, System.in, args);
	}

	/**
	 * Creates a new Command instance.
	 *
	 * @param out The output stream.
	 * @param in The input stream.
	 * @param args User arguments.
	 */

	public GUICommand(final PrintStream out, final InputStream in, final String... args) {
		super(out, in, args, null);
	}

	@Override
	protected Boolean execute() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final ProjectsFrame projectsFrame = new ProjectsFrame();
		projectsFrame.setVisible(true);
		projectsFrame.checkForUpdates();

		return true;
	}
	
}