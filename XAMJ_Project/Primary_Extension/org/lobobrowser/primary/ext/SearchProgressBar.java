/*
    GNU GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

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
import javax.swing.JProgressBar;

public class SearchProgressBar extends JDialog {

	private final JProgressBar progressBar = new JProgressBar();
	private final Thread mainProcess;
	
	public SearchProgressBar(Frame owner, boolean modal, Thread mainProcess) throws HeadlessException {
		super(owner, modal);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.mainProcess = mainProcess;

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		Box rootPanel = new Box(BoxLayout.Y_AXIS);
		rootPanel.setBorder(new EmptyBorder(4, 8, 4, 8));
		rootPanel.add(this.progressBar);
		contentPane.add(rootPanel);
		
		JComponent buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		JButton cancelButton = new JButton();
		cancelButton.setAction(new CancelAction());
		cancelButton.setText("Cancel");
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(Box.createRigidArea(new Dimension(4, 1)));
		buttonsPanel.add(cancelButton);
		contentPane.add(buttonsPanel);
		contentPane.add(Box.createRigidArea(new Dimension(1, 4)));
		
		this.progressBar.setIndeterminate(false);
		this.progressBar.setStringPainted(true);
		this.progressBar.setValue(0);
		this.progressBar.setMaximum(100);
		this.progressBar.setString("0%");
	}
	
	public void updateProgressBar (int value, int max){
		this.progressBar.setValue(value);
		this.progressBar.setMaximum(max);
		int progress = 100*value / max;
		this.progressBar.setString(String.valueOf(progress) + "%");		
	}
	
	private class CancelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			mainProcess.interrupt();
			SearchProgressBar.this.dispose();
		}
	}
}

