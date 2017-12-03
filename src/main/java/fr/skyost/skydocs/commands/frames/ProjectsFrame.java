package fr.skyost.skydocs.commands.frames;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import fr.skyost.skydocs.Constants;
import fr.skyost.skydocs.commands.BuildCommand;
import fr.skyost.skydocs.commands.Command;
import fr.skyost.skydocs.commands.Command.CommandListener;
import fr.skyost.skydocs.commands.NewCommand;
import fr.skyost.skydocs.commands.ServeCommand;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;

public class ProjectsFrame extends JFrame implements CommandListener {

	private static final long serialVersionUID = 1L;
	
	private static final short PROJECTS_PANEL_WIDTH = 500;
	private static final short BUTTONS_WIDTH = 160;
	
	private final PrintStream guiPrintStream = new PrintStream(new GUIPrintStream());
	
	private final DefaultListModel<String> projectsModel = new DefaultListModel<String>();
	private final JList<String> projectsList = new JList<String>(projectsModel);
	
	private final JButton createProjectButton = new JButton(Constants.GUI_BUTTON_CREATE);
	private final JButton addProjectButton = new JButton(Constants.GUI_BUTTON_ADD);
	private final JButton removeProjectButton = new JButton(Constants.GUI_BUTTON_REMOVE);
	private final JButton buildProjectButton = new JButton(Constants.GUI_BUTTON_BUILD);
	private final JButton serveProjectButton = new JButton(Constants.GUI_BUTTON_SERVE);
	
	private final JTextArea logTextArea = new JTextArea();
	
	private NewCommand newCommand;
	private BuildCommand buildCommand;
	private ServeCommand serveCommand;
	
	public ProjectsFrame() {
		this.setTitle(Constants.GUI_FRAME_TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImages(buildIconsList());
		
		projectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		projectsList.setBorder(new LineBorder(Color.GRAY));
		
		logTextArea.setEditable(false);
		
		final JPanel projectsPanel = new JPanel();
		projectsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.getContentPane().add(projectsPanel, BorderLayout.CENTER);
		
		final GroupLayout projectsPanelLayout = new GroupLayout(projectsPanel);
		projectsPanelLayout.setHorizontalGroup(
			projectsPanelLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(projectsList, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, PROJECTS_PANEL_WIDTH, Short.MAX_VALUE)
		);
		projectsPanelLayout.setVerticalGroup(
			projectsPanelLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(projectsList, GroupLayout.DEFAULT_SIZE, PROJECTS_PANEL_WIDTH / 3, Short.MAX_VALUE)
		);
		projectsPanel.setLayout(projectsPanelLayout);
		
		final JLabel lblGlobalMenu = new JLabel(Constants.GUI_LABEL_MENU);
		lblGlobalMenu.setFont(lblGlobalMenu.getFont().deriveFont(Font.ITALIC));
		
		createProjectButton.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				final JFileChooser chooser = new JFileChooser();
				chooser.changeToParentDirectory();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(chooser.showSaveDialog(ProjectsFrame.this) == JFileChooser.APPROVE_OPTION) {
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
			}
			
		});
		addProjectButton.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
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
			}
			
		});
		removeProjectButton.setEnabled(false);
		removeProjectButton.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				int index = projectsList.getSelectedIndex();
				projectsModel.removeElementAt(index);
				projectsList.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(projectsList, --index, index, false));
			}
			
		});
		buildProjectButton.setEnabled(false);
		buildProjectButton.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
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
					ex.printStackTrace();
				}
			}
			
		});
		serveProjectButton.setEnabled(false);
		serveProjectButton.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				if(serveCommand == null) {
					serveCommand = new ServeCommand(projectsModel.getElementAt(projectsList.getSelectedIndex()));
					serveCommand.setOut(guiPrintStream);
					serveCommand.addListener(ProjectsFrame.this);
					new Thread(serveCommand).start();
					return;
				}
				serveCommand.interrupt();
			}
			
		});
		
		final JScrollPane scrollPane = new JScrollPane(logTextArea);
		scrollPane.setBorder(projectsList.getBorder());
		
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
								.addComponent(addProjectButton, 0, BUTTONS_WIDTH, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(removeProjectButton, 0, BUTTONS_WIDTH, Short.MAX_VALUE))
						.addComponent(scrollPane, Alignment.TRAILING, 0, BUTTONS_WIDTH * 3 + 20, Short.MAX_VALUE))
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
						.addComponent(addProjectButton)
						.addComponent(removeProjectButton))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(buildProjectButton)
						.addComponent(serveProjectButton))
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, PROJECTS_PANEL_WIDTH / 4, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		menuPanel.setLayout(menuPanelLayout);
		
		projectsList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public final void valueChanged(final ListSelectionEvent event) {
				final int index = event.getFirstIndex();
				final boolean enabled = 0 <= index && index < projectsModel.size();
				removeProjectButton.setEnabled(enabled);
				buildProjectButton.setEnabled(enabled);
				serveProjectButton.setEnabled(enabled);
			}
			
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
			projectsModel.addElement(command.getArguments()[0]);
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
	}
	
	@Override
	public final void onCommandError(final Command command, final Throwable error) {
		JOptionPane.showMessageDialog(this, String.format(Constants.GUI_DIALOG_ERROR_MESSAGE, error.getClass().getName()), Constants.GUI_DIALOG_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
	}
	
	private final List<Image> buildIconsList() {
		final List<Image> icons = new ArrayList<Image>();
		final Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(Constants.RESOURCE_PROJECT_ICON));
		icons.addAll(Arrays.asList(
			icon.getScaledInstance(16, 16, Image.SCALE_SMOOTH),
			icon.getScaledInstance(32, 32, Image.SCALE_SMOOTH),
			icon.getScaledInstance(64, 64, Image.SCALE_SMOOTH),
			icon.getScaledInstance(128, 128, Image.SCALE_SMOOTH),
			icon.getScaledInstance(256, 256, Image.SCALE_SMOOTH),
			icon//.getScaledInstance(512, 512, Image.SCALE_SMOOTH) // Already in 512x512.
		));
		return Collections.unmodifiableList(icons);
	}
	
	public class GUIPrintStream extends OutputStream {

		@Override
		public final void write(final byte[] buffer, final int offset, final int length) throws IOException {
			final String text = new String(buffer, offset, length);
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public final void run() {
					logTextArea.append(text);
				}
				
			});
		}
		
		@Override
		public final void write(final int b) throws IOException {
			write(new byte []{(byte)b}, 0, 1);
		}
		
	}
	
}