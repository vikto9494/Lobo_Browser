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
package org.lobobrowser.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;
import java.io.*;
import java.net.URL;

import org.lobobrowser.clientlet.*;
import org.lobobrowser.main.ExtensionManager;
import org.lobobrowser.ua.*;
import org.lobobrowser.util.*;
import org.lobobrowser.util.Objects;

import java.util.logging.*;

/**
 * Default implementation of the {@link ExtensionSearchWindow} interface.
 */
public class ExtensionSearchWindow {
	private final FramePanel framePanel;
	private final String windowId;
	
	public ExtensionSearchWindow (){
		windowId = null;
		framePanel = FramePanelFactorySource.getInstance().getActiveFactory().createFramePanel(windowId);
	}
	
	public void navigate (URL url)
	{
		framePanel.navigate(url);
	}
	
	public void navigate(URL url, String method, ParameterInfo paramInfo, TargetType type, RequestType requestType) {
		framePanel.navigate(url, method, paramInfo, type, requestType, framePanel);
	}
	
	public String getSource ()
	{
		return framePanel.getSourceCode();
	}
	
	public String getURL ()
	{
		return framePanel.getURL();
	}
}
