package fr.skyost.skydocs.commands.frames;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
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
	
	private final DefaultListModel<String> projectsModel = new DefaultListModel<String>();
	private final JList<String> projectsList = new JList<String>(projectsModel);
	
	private final JButton btnCreateProject = new JButton(Constants.GUI_BUTTON_CREATE);
	private final JButton btnAddProject = new JButton(Constants.GUI_BUTTON_ADD);
	private final JButton btnRemoveProject = new JButton(Constants.GUI_BUTTON_REMOVE);
	private final JButton btnBuildProject = new JButton(Constants.GUI_BUTTON_BUILD);
	private final JButton btnServeProject = new JButton(Constants.GUI_BUTTON_SERVE);
	
	private NewCommand newCommand;
	private BuildCommand buildCommand;
	private ServeCommand serveCommand;
	
	public ProjectsFrame() {
		this.setTitle(Constants.GUI_FRAME_TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImages(buildIconsList());
		
		projectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		projectsList.setBorder(new LineBorder(Color.BLACK));
		
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
		
		btnCreateProject.addActionListener(new ActionListener() {

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
					newCommand.addListener(ProjectsFrame.this);
					new Thread(newCommand).start();
				}
			}
			
		});
		btnAddProject.addActionListener(new ActionListener() {

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
		btnRemoveProject.setEnabled(false);
		btnRemoveProject.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				int index = projectsList.getSelectedIndex();
				projectsModel.removeElementAt(index);
				projectsList.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(projectsList, --index, index, false));
			}
			
		});
		btnBuildProject.setEnabled(false);
		btnBuildProject.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
				try {
					if(buildCommand == null) {
						buildCommand = new BuildCommand(true, projectsModel.getElementAt(projectsList.getSelectedIndex()));
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
		btnServeProject.setEnabled(false);
		btnServeProject.addActionListener(new ActionListener() {

			@Override
			public final void actionPerformed(final ActionEvent event) {
					if(serveCommand == null) {
						serveCommand = new ServeCommand(projectsModel.getElementAt(projectsList.getSelectedIndex()));
						serveCommand.addListener(ProjectsFrame.this);
						new Thread(serveCommand).start();
						return;
					}
					serveCommand.interrupt();
			}
			
		});
		
		final JPanel menuPanel = new JPanel();
		this.getContentPane().add(menuPanel, BorderLayout.SOUTH);
		
		final GroupLayout menuPanelLayout = new GroupLayout(menuPanel);
		menuPanelLayout.setHorizontalGroup(
			menuPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(menuPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnBuildProject, GroupLayout.DEFAULT_SIZE, BUTTONS_WIDTH, Short.MAX_VALUE)
						.addComponent(btnCreateProject, GroupLayout.DEFAULT_SIZE, BUTTONS_WIDTH, Short.MAX_VALUE)
						.addComponent(lblGlobalMenu, GroupLayout.DEFAULT_SIZE, BUTTONS_WIDTH, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnServeProject, GroupLayout.DEFAULT_SIZE, BUTTONS_WIDTH, Short.MAX_VALUE)
						.addComponent(btnAddProject, GroupLayout.DEFAULT_SIZE, BUTTONS_WIDTH, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnRemoveProject, GroupLayout.DEFAULT_SIZE, BUTTONS_WIDTH, Short.MAX_VALUE)
					.addContainerGap())
		);
		menuPanelLayout.setVerticalGroup(
			menuPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(menuPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblGlobalMenu)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCreateProject)
						.addComponent(btnAddProject)
						.addComponent(btnRemoveProject))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(menuPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnBuildProject)
						.addComponent(btnServeProject))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		menuPanel.setLayout(menuPanelLayout);
		
		projectsList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public final void valueChanged(final ListSelectionEvent event) {
				final int index = event.getFirstIndex();
				final boolean enabled = 0 <= index && index < projectsModel.size();
				btnRemoveProject.setEnabled(enabled);
				btnBuildProject.setEnabled(enabled);
				btnServeProject.setEnabled(enabled);
			}
			
		});
		
		this.pack();
		this.setLocationRelativeTo(null);
	}

	@Override
	public final void onCommandStarted(final Command command) {
		if(command instanceof NewCommand) {
			btnCreateProject.setEnabled(false);
			btnBuildProject.setEnabled(false);
			btnServeProject.setEnabled(false);
		}
		else if(command instanceof BuildCommand) {
			btnCreateProject.setEnabled(false);
			btnBuildProject.setText(Constants.GUI_BUTTON_STOP);
			btnServeProject.setEnabled(false);
		}
		else {
			btnCreateProject.setEnabled(false);
			btnBuildProject.setEnabled(false);
			btnServeProject.setText(Constants.GUI_BUTTON_STOP);
		}
		btnRemoveProject.setEnabled(false);
	}

	@Override
	public final void onCommandFinished(final Command command) {
		final int index = projectsList.getSelectedIndex();
		final boolean enabled = 0 <= index && index < projectsModel.size();
		if(command instanceof NewCommand) {
			newCommand = null;
			projectsModel.addElement(command.getArguments()[0]);
			btnCreateProject.setEnabled(true);
			btnBuildProject.setEnabled(enabled);
			btnServeProject.setEnabled(enabled);
		}
		else if(command instanceof BuildCommand) {
			buildCommand = null;
			btnCreateProject.setEnabled(true);
			btnBuildProject.setText(Constants.GUI_BUTTON_BUILD);
			btnServeProject.setEnabled(enabled);
		}
		else {
			serveCommand = null;
			btnCreateProject.setEnabled(true);
			btnBuildProject.setEnabled(enabled);
			btnServeProject.setText(Constants.GUI_BUTTON_SERVE);
		}
		btnRemoveProject.setEnabled(true);
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
	
}