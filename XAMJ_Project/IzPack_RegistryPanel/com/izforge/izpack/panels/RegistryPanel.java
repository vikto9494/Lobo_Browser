/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
*/
/*
 * Created on Jun 5, 2005
 */
package com.izforge.izpack.panels;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.util.*;
import ca.beq.util.win32.registry.*;
import java.io.*;
import java.util.logging.*;

/**
 * @author J. H. S.
 */
public class RegistryPanel extends IzPanel implements NativeLibraryClient {
	private static final Logger logger = Logger.getLogger(RegistryPanel.class.getName());
	private final InstallData installData;
	
	public RegistryPanel(InstallerFrame arg0, InstallData arg1) {
		super(arg0, arg1);
		this.installData = arg1;
	}
	
	/* (non-Javadoc)
	 * @see com.izforge.izpack.installer.IzPanel#panelActivate()
	 */
	public void panelActivate() {
		super.panelActivate();
		this.performFileActions();
		this.performRegistryActions();
	}

	private void performFileActions() {
		try {
			File userHome = new File(System.getProperty("user.home"));
			File loboHome = new File(userHome, ".lobo");
			File cacheHome = new File(loboHome, "cache");
			this.deleteDecorationFiles(cacheHome);
		} catch(Exception err) {
			logger.log(Level.SEVERE, "performFileActions()", err);
		}
	}
	
	private void deleteDecorationFiles(File rootDir) {
		// Deletes decoration files in cache directory
		File[] files = rootDir.listFiles();
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				File file = files[i];
				if(file.isDirectory()) {
					this.deleteDecorationFiles(file);					
				}
				else {
					String name = file.getName().toLowerCase();
					if(name.endsWith(".decor")) {
						file.delete();
					}
				}
			}
		}
	}
	
	private void associateWithBrowser(String extension, String longName, String title, String mimeType, boolean noPrompt) {
		RegistryKey xamjRK = new RegistryKey(RootKey.HKEY_CLASSES_ROOT, extension);
		if(!xamjRK.exists()) {
			xamjRK.create();
		}		
		RegistryValue xamjRV = new RegistryValue(null, ValueType.REG_SZ, longName);
		xamjRK.setValue(xamjRV);

		RegistryValue mimeRV = new RegistryValue("Content Type", ValueType.REG_SZ, mimeType);
		xamjRK.setValue(mimeRV);

		RegistryKey xamjfileRK = new RegistryKey(RootKey.HKEY_CLASSES_ROOT, longName);
		if(!xamjfileRK.exists()) {
			xamjfileRK.create();
		}		
		RegistryValue xamjfileRV = new RegistryValue(null, ValueType.REG_SZ, title);
		xamjfileRK.setValue(xamjfileRV);

		RegistryKey defaultIconRK = new RegistryKey(RootKey.HKEY_CLASSES_ROOT, longName + "\\DefaultIcon");
		if(!defaultIconRK.exists()) {
			defaultIconRK.create();
		}
		File installPathFile = new File(installData.getInstallPath());
		String iconPath = new File(installPathFile, "lobo.ico").getAbsolutePath();
		//System.out.println("iconPath=" + iconPath);
		RegistryValue defaultIconRV = new RegistryValue(null, ValueType.REG_SZ, iconPath);
		defaultIconRK.setValue(defaultIconRV);

		if(noPrompt) {
		    RegistryValue editFlagsRV = new RegistryValue("EditFlags", ValueType.REG_BINARY, new byte[] { 0x00, 0x00, 0x01, 0x00 });
		    xamjfileRK.setValue(editFlagsRV);
		}

		RegistryKey openRK = new RegistryKey(RootKey.HKEY_CLASSES_ROOT, longName + "\\Shell\\Open\\Command");
		if(!openRK.exists()) {
			openRK.create();
		}
		String javaExe = new File(new File(new File(System.getProperty("java.home")), "bin"), "javaw.exe").getAbsolutePath();
		String loboJar = new File(installPathFile, "lobo.jar").getAbsolutePath();
		//System.out.println("javaExe=" + javaExe);
		StringBuffer launcher = new StringBuffer();
		launcher.append('"');
		launcher.append(javaExe);
		launcher.append('"');
		launcher.append(" -jar ");
		launcher.append('"');
		launcher.append(loboJar);
		launcher.append('"');
		launcher.append(" ");
		launcher.append('"');
		launcher.append("%1");
		launcher.append('"');
		RegistryValue openRV = new RegistryValue(null, ValueType.REG_SZ, launcher.toString());
		openRK.setValue(openRV);
		
		RegistryKey mimeRK = new RegistryKey(RootKey.HKEY_CLASSES_ROOT, "MIME\\Database\\Content Type\\" + mimeType);
		if(!mimeRK.exists()) {
			mimeRK.create();
		}
		mimeRV = new RegistryValue("Extension", ValueType.REG_SZ, extension);
		mimeRK.setValue(mimeRV);
		
	}
	
	private void performRegistryActions() {
		/* (skipping this altogether at the moment)
		try {
			try {
				Librarian.getInstance().loadLibrary("jRegistryKey", this);
			} catch(Exception error) {
				logger.info("Skipping registry panel (system is not Windows)");
				if(logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "Exception trace.", error);
				}
				parent.skipPanel();
				return;
			}
			//(no associations at the moment)
			//this.associateWithBrowser(".xamj", "xamjfile", "Executable XAMJ File", "application/x-xamj+xml", false);
			//this.associateWithBrowser(".htln", "htlnfile", "Link To Browser", "application/x-htln+xml", true);
		} catch(Throwable t) {
			logger.log(Level.WARNING, "performRegistryActions(): Unexpected exception.", t);
		}
		if(logger.isLoggable(Level.FINE)) {
			logger.fine("Registry associations installed. Skipping panel.");
		}
		*/
		parent.skipPanel();		
	}
		
	/* (non-Javadoc)
	 * @see com.izforge.izpack.util.NativeLibraryClient#freeLibrary(java.lang.String)
	 */
	public void freeLibrary(String arg0) {
	}
}
