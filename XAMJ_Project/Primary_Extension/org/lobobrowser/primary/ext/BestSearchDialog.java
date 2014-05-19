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

import org.lobobrowser.primary.ext.BestSearchAlpha;
import org.lobobrowser.primary.gui.FieldType;
import org.lobobrowser.primary.gui.SmartForm;
import org.lobobrowser.primary.gui.FormField;
import org.lobobrowser.primary.gui.FormPanel;
import org.lobobrowser.primary.settings.ToolsSettings;
import org.lobobrowser.util.*;
import org.lobobrowser.primary.ext.BestSearchInfo;
import org.lobobrowser.primary.ext.SearchDownloadInfo.SearchType;

public class BestSearchDialog extends JDialog {
	private final FormField seacrhString = new SmartForm (FieldType.TEXT, "Search:");
	private final FormField fileName = new FormField (FieldType.TEXT, "Save results in:");
	private final FormField numberOfPages = new FormField(FieldType.TEXT,
			"Max number of URLs for search:");
	java.io.File selectedFile;
	
	BestSearchInfo bestSearchInfo = new BestSearchInfo();
	BestSearchAlpha bestSearchAlpha = new BestSearchAlpha();
	
	public BestSearchDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.seacrhString.setValue("String to search");
		this.numberOfPages.setValue("10");
		ToolsSettings settings = ToolsSettings.getInstance();
		selectedFile = settings.getOpenFileDirectory();

		this.fileName.setValue(selectedFile.getAbsolutePath());
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		FormPanel fieldsPanel = new FormPanel();
		fieldsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		fieldsPanel.addField(this.seacrhString);
		FormPanel fieldsOpenFilePanel = new FormPanel();
		fieldsOpenFilePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		fieldsOpenFilePanel.addField(this.fileName);
		FormPanel fieldsNumberOfPages = new FormPanel();
		fieldsNumberOfPages.setBorder(new EmptyBorder(5, 5, 5, 5));
		fieldsNumberOfPages.addField(this.numberOfPages);
		
		Dimension fpps = fieldsPanel.getPreferredSize();
		fieldsPanel.setPreferredSize(new Dimension(600, fpps.height));		
		contentPane.add(fieldsPanel);
		
		JComponent buttonsOpenFilePanel = new JPanel();
		buttonsOpenFilePanel.setLayout(new BoxLayout(buttonsOpenFilePanel, BoxLayout.X_AXIS));
		JButton fileOpenButton = new JButton();
		fileOpenButton.setAction(new OpenFile());
		fileOpenButton.setText("Browse");
		buttonsOpenFilePanel.add(fieldsOpenFilePanel);
		buttonsOpenFilePanel.add(Box.createHorizontalGlue());
		buttonsOpenFilePanel.add(fileOpenButton);
		buttonsOpenFilePanel.add(Box.createHorizontalGlue());
		contentPane.add(buttonsOpenFilePanel);

		fpps = fieldsNumberOfPages.getPreferredSize();
		fieldsNumberOfPages.setPreferredSize(new Dimension(600, fpps.height));		
		contentPane.add(fieldsNumberOfPages);
		
		JComponent buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		JButton okButton = new JButton();
		okButton.setAction(new OkAction());
		okButton.setText("Search");
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

	public BestSearchInfo getBestSearchInfo() {
		return this.bestSearchInfo;
	}
	
	private class OkAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			bestSearchInfo.setStringToSearch(seacrhString.getValue());
			bestSearchInfo.setFileToSave(fileName.getValue());
			Integer number = Integer.parseInt(numberOfPages.getValue());
			bestSearchInfo.setNumberOfPages(number);
			bestSearchAlpha.setBestSearchInfo(bestSearchInfo);
			bestSearchAlpha.start();
			BestSearchDialog.this.dispose();
		}
	}
	
	private class CancelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			bestSearchInfo = null;
			BestSearchDialog.this.dispose();
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
