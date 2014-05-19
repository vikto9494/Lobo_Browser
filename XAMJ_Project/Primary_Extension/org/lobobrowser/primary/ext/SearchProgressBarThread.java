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

import javax.swing.JDialog;

public class SearchProgressBarThread extends Thread {
	private SearchProgressBar dialog = null;
	private Thread mainProcess;
	
	public SearchProgressBarThread (Thread mainProcess){
		this.mainProcess = mainProcess;
	}
	
	public void run() {
		dialog = new SearchProgressBar(null, true, mainProcess);
		dialog.setTitle("Search&Download Processing");
		dialog.setLocationByPlatform(true);
		dialog.setResizable(false);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	public void updateProgressBar (int value, int max){
		while (dialog == null)
		  try {
			Thread.sleep(0, 100);
		  } catch (InterruptedException e) {
			 Thread.interrupted();
			 return;
		  }
		dialog.updateProgressBar (value, max);
	}
	
	public void close(){
		if (dialog != null){
		  dialog.dispose();
		  dialog = null;
		}
	}

}
