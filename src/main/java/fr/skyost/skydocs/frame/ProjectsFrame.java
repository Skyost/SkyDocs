package fr.skyost.skydocs.frame;

import com.google.common.io.Files;
import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.command.BuildCommand;
import fr.skyost.skydocs.command.Command;
import fr.skyost.skydocs.command.Command.CommandListener;
import fr.skyost.skydocs.command.NewCommand;
import fr.skyost.skydocs.command.ServeCommand;
import fr.skyost.skydocs.utils.GithubUpdater;
import fr.skyost.skydocs.utils.GithubUpdater.GithubUpdaterResultListener;
import fr.skyost.skydocs.utils.Utils;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * SkyDocs' GUI.
 */

public class ProjectsFrame extends JFrame implements CommandListener, GithubUpdaterResultListener {

	private static final long serialVersionUID = 1L;
	
	private static final short PROJECTS_PANEL_WIDTH = 500;
	private static final short BUTTONS_WIDTH = 160;
	
	private final PrintStream guiPrintStream = new PrintStream(new GUIPrintStream());
	
	private final DefaultListModel<String> projectsModel = new DefaultListModel<>();
	private final JList<String> projectsList = new JList<>(projectsModel);
	
	private final JButton createProjectButton = new JButton(Constants.GUI_BUTTON_CREATE);
	private final JButton openProjectButton = new JButton(Constants.GUI_BUTTON_OPEN);
	private final JButton removeProjectButton = new JButton(Constants.GUI_BUTTON_REMOVE);
	private final JButton buildProjectButton = new JButton(Constants.GUI_BUTTON_BUILD);
	private final JButton serveProjectButton = new JButton(Constants.GUI_BUTTON_SERVE);
	
	private final JTextArea logTextArea = new JTextArea("---- Log ----" + System.lineSeparator());
	
	private NewCommand newCommand;
	private BuildCommand buildCommand;
	private ServeCommand serveCommand;
	
	public ProjectsFrame() {
		loadHistory();
		
		this.setTitle(Constants.GUI_FRAME_TITLE);
		this.setIconImages(buildIconsList());
		this.addWindowListener(new WindowAdapter() {

			@Override
			public final void windowClosing(final WindowEvent event) {
				if(newCommand != null) {
					newCommand.interrupt();
				}
				else if(buildCommand != null) {
					buildCommand.interrupt();
				}
				else if(serveCommand != null) {
					serveCommand.interrupt();
				}
				saveHistory();
				System.exit(0);
			}

		});
		
		projectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		((DefaultCaret)logTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		logTextArea.setEditable(false);
		logTextArea.setFont(createProjectButton.getFont());
		
		final JPanel projectsPanel = new JPanel();
		projectsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.getContentPane().add(projectsPanel, BorderLayout.CENTER);
		
		final JScrollPane projectsScrollPane = new JScrollPane(projectsList);
		projectsScrollPane.setBorder(new LineBorder(Color.GRAY));
		final JScrollPane logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setBorder(projectsScrollPane.getBorder());
		
		final GroupLayout projectsPanelLayout = new GroupLayout(projectsPanel);
		projectsPanelLayout.setHorizontalGroup(
			projectsPanelLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(projectsScrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, PROJECTS_PANEL_WIDTH, Short.MAX_VALUE)
		);
		projectsPanelLayout.setVerticalGroup(
			projectsPanelLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(projectsScrollPane, GroupLayout.DEFAULT_SIZE, PROJECTS_PANEL_WIDTH / 3, Short.MAX_VALUE)
		);
		projectsPanel.setLayout(projectsPanelLayout);
		
		final JLabel lblGlobalMenu = new JLabel("What do you want to do ?");
		lblGlobalMenu.setFont(lblGlobalMenu.getFont().deriveFont(Font.ITALIC));
		
		createProjectButton.addActionListener(event -> {
			final JFileChooser chooser = new JFileChooser();
			chooser.changeToParentDirectory();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(chooser.showOpenDialog(ProjectsFrame.this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if(!file.isDirectory()) {
					file = file.getParentFile();
				}
				final String path = file.getPath();
				if(projectsModel.contains(path)) {
					projectsList.setSelectedValue(path, true);
					return;
				}
				newCommand = new NewCommand(path);
				newCommand.setOut(guiPrintStream);
				newCommand.addListener(ProjectsFrame.this);
				new Thread(newCommand).start();
			}
		});
		openProjectButton.addActionListener(event -> {
			final JFileChooser chooser = new JFileChooser();
			chooser.changeToParentDirectory();
			chooser.setFileFilter(new FileFilter() {

				@Override
				public final boolean accept(final File file) {
					return file.isDirectory() || file.getName().equalsIgnoreCase(Constants.FILE_PROJECT_DATA);
				}

				@Override
				public final String getDescription() {
					return Constants.GUI_CHOOSER_DESCRIPTION;
				}

			});
			if(chooser.showOpenDialog(ProjectsFrame.this) == JFileChooser.APPROVE_OPTION) {
				final String path = chooser.getSelectedFile().getParentFile().getPath();
				if(projectsModel.contains(path)) {
					projectsList.setSelectedValue(path, true);
					return;
				}
				projectsModel.addElement(path);
			}
		});
		removeProjectButton.setEnabled(false);
		removeProjectButton.addActionListener(event -> {
			int index = projectsList.getSelectedIndex();
			projectsModel.removeElementAt(index);
			projectsList.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(projectsList, --index, index, false));
		});
		buildProjectButton.setEnabled(false);
		buildProjectButton.addActionListener(event -> {
			try {
				if(buildCommand == null) {
					buildCommand = new BuildCommand(true, guiPrintStream, projectsModel.getElementAt(projectsList.getSelectedIndex()));
					buildCommand.addListener(ProjectsFrame.this);
					new Thread(buildCommand).start();
					return;
				}
				buildCommand.interrupt();
			}
			catch(final Exception ex) {
				ex.printStackTrace(guiPrintStream);
				ex.printStackTrace();
				JOptionPane.showMessageDialog(ProjectsFrame.this, String.format(Constants.GUI_DIALOG_ERROR_MESSAGE, ex.getMessage()), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			}
		});
		serveProjectButton.setEnabled(false);
		serveProjectButton.addActionListener(event -> {
			if(serveCommand == null) {
				serveCommand = new ServeCommand("-directory", projectsModel.getElementAt(projectsList.getSelectedIndex()), "-manualRebuild", "false");
				serveCommand.setOut(guiPrintStream);
				serveCommand.addListener(ProjectsFrame.this);
				new Thread(serveCommand).start();
				return;
			}
			serveCommand.interrupt();
		});
		
		final JPanel menuPanel = new JPanel();
		this.getContentPane().add(menuPanel, BorderLayout.SOUTH);
		
		final GroupLayout menuPanelLayout = new GroupLayout(menuPanel);
		menuPanelLayout.setHorizontalGroup(
			menuPanelLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(menuPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(menuPanelLayout.createSequentialGroup()
							.addGroup(menuPanelLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(buildProjectButton, Alignment.LEADING, 0, BUTTONS_WIDTH, Short.MAX_VALUE)
								.addComponent(createProjectButton, Alignment.LEADING, 0, BUTTONS_WIDTH, Short.MAX_VALUE)
								.addComponent(lblGlobalMenu, Alignment.LEADING, 0, BUTTONS_WIDTH, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(menuPanelLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(serveProjectButton, 0, BUTTONS_WIDTH, Short.MAX_VALUE)
								.addComponent(openProjectButton, 0, BUTTONS_WIDTH, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(removeProjectButton, 0, BUTTONS_WIDTH, Short.MAX_VALUE))
						.addComponent(logScrollPane, Alignment.TRAILING, 0, BUTTONS_WIDTH * 3 + 20, Short.MAX_VALUE))
					.addContainerGap())
		);
		menuPanelLayout.setVerticalGroup(
			menuPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(menuPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblGlobalMenu)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(createProjectButton)
						.addComponent(openProjectButton)
						.addComponent(removeProjectButton))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(buildProjectButton)
						.addComponent(serveProjectButton))
					.addGap(18)
					.addComponent(logScrollPane, GroupLayout.PREFERRED_SIZE, PROJECTS_PANEL_WIDTH / 4, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		menuPanel.setLayout(menuPanelLayout);
		
		projectsList.addListSelectionListener(event -> {
			final int index = event.getFirstIndex();
			final boolean enabled = 0 <= index && index < projectsModel.size();

			for(final JButton button : new JButton[]{removeProjectButton, buildProjectButton, serveProjectButton}) {
				if(button.getText().equals(Constants.GUI_BUTTON_STOP)) {
					return;
				}
			}
			removeProjectButton.setEnabled(enabled);
			buildProjectButton.setEnabled(enabled);
			serveProjectButton.setEnabled(enabled);
		});
		
		this.pack();
		this.setLocationRelativeTo(null);
	}

	@Override
	public final void onCommandStarted(final Command command) {
		if(command instanceof NewCommand) {
			createProjectButton.setEnabled(false);
			buildProjectButton.setEnabled(false);
			serveProjectButton.setEnabled(false);
		}
		else if(command instanceof BuildCommand) {
			createProjectButton.setEnabled(false);
			buildProjectButton.setText(Constants.GUI_BUTTON_STOP);
			serveProjectButton.setEnabled(false);
		}
		else {
			createProjectButton.setEnabled(false);
			buildProjectButton.setEnabled(false);
			serveProjectButton.setText(Constants.GUI_BUTTON_STOP);
		}
		removeProjectButton.setEnabled(false);
	}

	@Override
	public final void onCommandFinished(final Command command) {
		final int index = projectsList.getSelectedIndex();
		final boolean enabled = 0 <= index && index < projectsModel.size();
		if(command instanceof NewCommand) {
			newCommand = null;
			projectsModel.addElement(((NewCommand)command).getArguments().directory);
			createProjectButton.setEnabled(true);
			buildProjectButton.setEnabled(enabled);
			serveProjectButton.setEnabled(enabled);
		}
		else if(command instanceof BuildCommand) {
			buildCommand = null;
			createProjectButton.setEnabled(true);
			buildProjectButton.setText(Constants.GUI_BUTTON_BUILD);
			serveProjectButton.setEnabled(enabled);
		}
		else {
			serveCommand = null;
			createProjectButton.setEnabled(true);
			buildProjectButton.setEnabled(enabled);
			serveProjectButton.setText(Constants.GUI_BUTTON_SERVE);
		}
		removeProjectButton.setEnabled(true);
		command.blankLine();
		logTextArea.setCaretPosition(logTextArea.getText().length());
	}
	
	@Override
	public final void onCommandError(final Command command, final Throwable error) {
		JOptionPane.showMessageDialog(this, String.format(Constants.GUI_DIALOG_ERROR_MESSAGE, error.getMessage()), error.getClass().getName(), JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public final void updaterStarted() {}

	@Override
	public final void updaterException(final Exception ex) {}

	@Override
	public final void updaterResponse(final String response) {}

	@Override
	public final void updaterUpdateAvailable(final String localVersion, final String remoteVersion) {
		final String link = "https://github.com/" + GithubUpdater.UPDATER_GITHUB_USERNAME + "/" + GithubUpdater.UPDATER_GITHUB_REPO + "/releases/latest";
		if(JOptionPane.showConfirmDialog(this, "<html>An update is available : v" + remoteVersion + " !<br/>" + "Would you like to visit " + link + " to download it ?</html>", Constants.APP_NAME, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			try {
				if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					Desktop.getDesktop().browse(new URI(link));
				}
			}
			catch(final Exception ex) {
				ex.printStackTrace(guiPrintStream);
				ex.printStackTrace();
				JOptionPane.showMessageDialog(ProjectsFrame.this, String.format(Constants.GUI_DIALOG_ERROR_MESSAGE, ex.getMessage()), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public final void updaterNoUpdate(final String localVersion, final String remoteVersion) {}
	
	/**
	 * Builds a list of icons to use with Swing.
	 * 
	 * @return A list of icons to use with Swing.
	 */
	
	private List<Image> buildIconsList() {
		final Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(Constants.RESOURCE_PROJECT_ICON));
		return Arrays.asList(
			icon.getScaledInstance(16, 16, Image.SCALE_SMOOTH),
			icon.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
			icon.getScaledInstance(64, 64, Image.SCALE_SMOOTH),
			icon.getScaledInstance(128, 128, Image.SCALE_SMOOTH),
			icon.getScaledInstance(256, 256, Image.SCALE_SMOOTH),
			icon//.getScaledInstance(512, 512, Image.SCALE_SMOOTH) // Already in 512x512.
		);
	}
	
	/**
	 * Checks for updates.
	 */
	
	public final void checkForUpdates() {
		new GithubUpdater(Constants.APP_VERSION.split(" ")[0].substring(1), this).start();
	}
	
	/**
	 * Loads projects from the history.
	 */
	
	public final void loadHistory() {
		try {
			final File history = new File(Utils.getParentFolder(), Constants.FILE_GUI_HISTORY);
			if(!history.exists()) {
				return;
			}
			for(final String path : Files.readLines(history, StandardCharsets.UTF_8)) {
				final File projectData = new File(path, Constants.FILE_PROJECT_DATA);
				if(!projectData.exists()) {
					continue;
				}
				projectsModel.addElement(path);
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace(guiPrintStream);
			ex.printStackTrace();
			JOptionPane.showMessageDialog(ProjectsFrame.this, String.format(Constants.GUI_DIALOG_ERROR_MESSAGE, ex.getMessage()), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Saves the projects history.
	 */
	
	public final void saveHistory() {
		try {
			final Utils.AutoLineBreakStringBuilder builder = new Utils.AutoLineBreakStringBuilder();
			for(int i = 0; i < projectsModel.size(); i++) {
				builder.append(projectsModel.getElementAt(i));
			}
			Files.write(builder.toString(), new File(Utils.getParentFolder(), Constants.FILE_GUI_HISTORY), StandardCharsets.UTF_8);
		}
		catch(final Exception ex) {
			ex.printStackTrace(guiPrintStream);
			ex.printStackTrace();
			JOptionPane.showMessageDialog(ProjectsFrame.this, String.format(Constants.GUI_DIALOG_ERROR_MESSAGE, ex.getMessage()), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Allows to forward an output stream to the GUI.
	 */
	
	public class GUIPrintStream extends OutputStream {

		@Override
		public final void write(final byte[] buffer, final int offset, final int length) {
			final String text = new String(buffer, offset, length);
			SwingUtilities.invokeLater(() -> logTextArea.append(text));
		}
		
		@Override
		public final void write(final int b) {
			write(new byte []{(byte)b}, 0, 1);
		}
		
	}
	
}