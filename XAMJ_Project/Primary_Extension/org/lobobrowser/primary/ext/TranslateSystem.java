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

import java.io.IOException;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.lang.Object;
import java.lang.System;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lobobrowser.ua.NavigatorFrame;
import org.lobobrowser.ua.ParameterInfo;
import org.lobobrowser.ua.RequestType;
import org.lobobrowser.ua.TargetType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.lobobrowser.primary.ext.SearchDownloadInfo;
import org.lobobrowser.primary.ext.SearchDownloadInfo.InfType;
import org.lobobrowser.primary.ext.SearchSystems.SearchSystemType;
import org.lobobrowser.gui.ExtensionSearchWindow;
import org.lobobrowser.html.FormInput;
import org.lobobrowser.html.domimpl.HTMLElementImpl;
import org.lobobrowser.html.domimpl.NodeVisitor;
import org.lobobrowser.primary.clientlets.html.HtmlClientlet;
import org.lobobrowser.primary.clientlets.html.HtmlRendererContextImpl.LocalParameterInfo;
import org.w3c.dom.Node;


public class TranslateSystem {
	private static final Logger logger = Logger.getLogger(SearchDownloadAlpha.class.getName());
	private final ExtensionSearchWindow searchWindow;
	private final Object sync = new Object();
	
	public TranslateSystem (ExtensionSearchWindow searchWindow){
		this.searchWindow = searchWindow;
	}
	
	private String getSource ()
	{
		String res;
		HtmlClientlet.setSynchronization (sync);
		synchronized (sync){
			try {
				sync.wait(60000);
			} catch (InterruptedException e) {
				logger.info("TranslateSystem: problems with synchronization");
			}
			res = searchWindow.getSource();
		}
		HtmlClientlet.deleteSynchronization();
		return res;
	}
	
	public static FormInput[] GetFormInputsForGoogleTranslateRussianToEnglish(
			String stringToTranslate){
		FormInput[] formInputs = new FormInput[8];
		formInputs[0] = new FormInput("sl","ru");
		formInputs[1] = new FormInput("tl","en");
		formInputs[2] = new FormInput("js","y");
		formInputs[3] = new FormInput("prev","_t");
		formInputs[4] = new FormInput("hl","ru");
		formInputs[5] = new FormInput("ie","windows-1251");
		formInputs[6] = new FormInput("text",stringToTranslate);
		formInputs[7] = new FormInput("edit-text","");
		return formInputs;
	}
	
	public void navigate (String stringToTranslate){
		java.net.URL search_url;
		String method = new String ("POST");
		String enctype = null;
		FormInput[] formInputs = GetFormInputsForGoogleTranslateRussianToEnglish(
				stringToTranslate);
		ParameterInfo paramInfo = new LocalParameterInfo(enctype, formInputs);
		TargetType type = TargetType.SELF;
		RequestType requestType = RequestType.FORM;
		try {
			search_url = new java.net.URL(
					"http://translate.google.ru/");
		} catch(java.net.MalformedURLException mfu) {
			logger.log(Level.WARNING, "navigate", mfu);
			search_url = null;
		}
		searchWindow.navigate(search_url, method, paramInfo, type, requestType);
	}
	
	public String TranslateOnEnglish (String Text){
/*		this.navigate (Text);
		String htmlCode = getSource();
		System.out.println(htmlCode);
		String[] arr = htmlCode.split("TRANSLATED_TEXT='");
		arr = arr[1].split("'");
		return arr[0];*/return Text;
	}
}
