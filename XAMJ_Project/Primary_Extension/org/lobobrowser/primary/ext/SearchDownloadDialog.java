/*
    GNU GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
package org.lobobrowser.primary.ext;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JOptionPane;

import java.io.*;
import java.net.*;
import java.lang.Object;

import org.lobobrowser.primary.ext.SearchDownloadAlpha;
import org.lobobrowser.primary.ext.SearchDownloadInfo;
import org.lobobrowser.primary.ext.SearchDownloadInfo.InfType;
import org.lobobrowser.primary.ext.SearchDownloadInfo.SearchType;
import org.lobobrowser.primary.gui.CheckBoxPanel;
import org.lobobrowser.primary.gui.FieldType;
import org.lobobrowser.primary.gui.SmartForm;
import org.lobobrowser.primary.gui.StringListControl;
import org.lobobrowser.primary.gui.FormField;
import org.lobobrowser.primary.gui.FormPanel;
import org.lobobrowser.primary.gui.SwingTasks;
import org.lobobrowser.primary.settings.ToolsSettings;
import org.lobobrowser.ua.NavigatorFrame;
import org.lobobrowser.ua.NavigatorWindow;
import org.lobobrowser.util.*;

public class SearchDownloadDialog extends JDialog {
	private final SmartForm seacrhString = new SmartForm(FieldType.TEXT,
			"Search:");
	private final FormField fileName = new FormField(FieldType.TEXT,
			"Save results in:");
	private final FormField numberOfPages = new FormField(FieldType.TEXT,
			"Max number of URLs for search:");
	private java.io.File selectedFile;
	private final StringListControl infTypeStringListControl = new StringListControl(
			false);
	private CheckBoxPanel ieSpoofPanel;
	FormPanel numberOfPagesPanel;
	
	private SearchDownloadInfo searchDownloadInfo = new SearchDownloadInfo();
	private SearchDownloadAlpha searchDownloadAlpha;

	public SearchDownloadDialog(Frame owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);
		searchDownloadAlpha = new SearchDownloadAlpha();
		ToolsSettings settings = ToolsSettings.getInstance();
		selectedFile = settings.getOpenFileDirectory();

		this.fileName.setValue(selectedFile.getAbsolutePath());
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.seacrhString.setValue("String to search");
		this.numberOfPages.setValue("0");
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		FormPanel fieldsPanel = new FormPanel();
		fieldsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		fieldsPanel.addField(this.seacrhString);
		FormPanel fieldsOpenFilePanel = new FormPanel();
		fieldsOpenFilePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		fieldsOpenFilePanel.addField(this.fileName);
		
		FormPanel numberOfPagesPanel = new FormPanel();
		this.numberOfPagesPanel = numberOfPagesPanel;
		numberOfPagesPanel.setBorder(new EmptyBorder(5, 30, 30, 5));
		numberOfPagesPanel.addField(this.numberOfPages);
		Dimension fpps = numberOfPagesPanel.getPreferredSize();
		numberOfPagesPanel.setPreferredSize(new Dimension(600, fpps.height));
		this.ieSpoofPanel = new CheckBoxPanel(
				"Search in different search systems by regular expression",
				numberOfPagesPanel);
		this.ieSpoofPanel.setPreferredSize(new Dimension(600, fpps.height));

		String[] infTypes = new String[] { "text", "pictures" };
		infTypeStringListControl.setStrings(infTypes);
		Box startupGroupBox = new Box(BoxLayout.Y_AXIS);
		startupGroupBox.setBorder(new TitledBorder(new EtchedBorder(),
				"Search settings"));
		Box infTypeBox = new Box(BoxLayout.X_AXIS);
		JLabel infTypeLabel = new JLabel("Choose information type:");
		infTypeBox.add(infTypeLabel);
		infTypeBox.add(infTypeStringListControl);
		startupGroupBox.add(infTypeBox);
		startupGroupBox.add(this.ieSpoofPanel);
		contentPane.add(startupGroupBox);
		//this.ieSpoofPanel.setSelected(false);
		//this.ieSpoofPanel.updateEnabling();

		Dimension fppsf = fieldsPanel.getPreferredSize();
		fieldsPanel.setPreferredSize(new Dimension(600, fppsf.height));
		contentPane.add(fieldsPanel);

		JComponent buttonsOpenFilePanel = new JPanel();
		buttonsOpenFilePanel.setLayout(new BoxLayout(buttonsOpenFilePanel,
				BoxLayout.X_AXIS));
		JButton fileOpenButton = new JButton();
		fileOpenButton.setAction(new OpenFile());
		fileOpenButton.setText("Browse");
		buttonsOpenFilePanel.add(fieldsOpenFilePanel);
		buttonsOpenFilePanel.add(Box.createHorizontalGlue());
		buttonsOpenFilePanel.add(fileOpenButton);
		buttonsOpenFilePanel.add(Box.createHorizontalGlue());
		contentPane.add(buttonsOpenFilePanel);

		JComponent buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		JButton okButton = new JButton();
		okButton.setAction(new OkAction());
		okButton.setText("Search&Download");
		JButton cancelButton = new JButton();
		cancelButton.setAction(new CancelAction());
		cancelButton.setText("Cancel");
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(okButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(4, 1)));
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		contentPane.add(buttonsPanel);
		contentPane.add(Box.createRigidArea(new Dimension(1, 4)));
	}

	public SearchDownloadInfo getBestSearchInfo() {
		return this.searchDownloadInfo;
	}

	private Component getSearchGroupBox() {
		String[] infTypes = new String[] { "text", "pictures" };
		infTypeStringListControl.setStrings(infTypes);
		numberOfPages.setValue("0");
		Box startupGroupBox = new Box(BoxLayout.Y_AXIS);
		startupGroupBox.setBorder(new TitledBorder(new EtchedBorder(),
				"Search settings"));
		Box infTypeBox = new Box(BoxLayout.X_AXIS);
		JLabel infTypeLabel = new JLabel("Choose information type:");
		infTypeBox.add(infTypeLabel);
		infTypeBox.add(infTypeStringListControl);
		startupGroupBox.add(infTypeBox);
		startupGroupBox.add(this.ieSpoofPanel);
		return startupGroupBox;
	}

	private class OkAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			searchDownloadInfo.setFileToSave(fileName.getValue());
			searchDownloadInfo.setStringToSearch(seacrhString.getValue());
			searchDownloadInfo.setInfType(InfType
					.fromInt(infTypeStringListControl.getSelectedItem()));
			searchDownloadInfo.setSearchType((!ieSpoofPanel.isSelected()) ?
					SearchType.URL : SearchType.REGEXP);
			Integer number = Integer.parseInt(numberOfPages.getValue());
			searchDownloadInfo.setNumberOfPages(
					searchDownloadInfo.getSearchType() == SearchType.URL ? 0 : 
						number);
			searchDownloadAlpha.setSearchDownloadInfo(searchDownloadInfo);
			searchDownloadAlpha.start();
			SearchDownloadDialog.this.dispose();
		}
	}

	private class CancelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			searchDownloadInfo = null;
			SearchDownloadDialog.this.dispose();
		}
	}

	private class OpenFile extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser
					.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (selectedFile != null) {
				fileChooser.setSelectedFile(selectedFile);
			}
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				fileName.setValue(selectedFile.getAbsolutePath());
			}
		}
	}
}
