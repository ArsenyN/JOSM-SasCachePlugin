package org.openstreetmap.josm.plugins.sascache;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.tools.GBC;

public class SasCachePathDialog extends ExtendedDialog 
{

	private static final String[] BUTTON_TEXTS = new String[] {tr("OK"), tr("Cancel")};
	private static final String[] BUTTON_ICONS = new String[] {"ok.png", "cancel.png"};

	protected final JPanel panel = new JPanel(new GridBagLayout());
	protected JTextField pathTextField;

	
	public SasCachePathDialog(String title) 
	{
		super(Main.parent, title, BUTTON_TEXTS, true);

		contentInsets = new Insets(15, 15, 5, 15);
		setButtonIcons(BUTTON_ICONS);

		pathTextField = new JTextField(60);
		panel.add(pathTextField, GBC.std());

		pathTextField.setText(SasCachePlugin.getSasCachePath());		

		JButton browseButton = new JButton("Обзор...");
		panel.add(browseButton, GBC.std());	

		ActionListener actionListener = new BrowseButtonActionListener();
		browseButton.addActionListener(actionListener);	

		setContent(panel);		
	}

	public void saveSettings()
	{
		String cPath = pathTextField.getText();
		if (new File(cPath).exists())
			Main.pref.put("sascache.path", cPath);	
	}

	private class BrowseButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	

			String cPath = pathTextField.getText();
			if (new File(cPath).exists())
				fileChooser.setCurrentDirectory(new File(cPath));

			if (fileChooser.showOpenDialog(Main.parent) == JFileChooser.APPROVE_OPTION)
			{
				pathTextField.setText(fileChooser.getSelectedFile().toString());
			}
		}
	} 


}
