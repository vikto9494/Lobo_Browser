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
import javax.swing.JDialog;

import java.lang.Object;
import java.lang.System;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lobobrowser.ua.NavigatorFrame;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lobobrowser.primary.ext.SearchDownloadInfo;
import org.lobobrowser.gui.ExtensionSearchWindow;
import org.lobobrowser.primary.clientlets.html.HtmlClientlet;
import org.lobobrowser.primary.ext.SearchSystems;
import org.lobobrowser.primary.ext.SearchSystems.SearchSystemType;
import org.lobobrowser.primary.ext.TranslateSystem;
import org.lobobrowser.primary.ext.SearchProgressBar;

public class SearchDownloadAlpha extends Thread{
	private final SearchProgressBarThread searchProgressBarThread = new SearchProgressBarThread(this);
	private final SearchSystems searchSystems;
	private final TranslateSystem translateSystem;
	private static final Logger logger = Logger.getLogger(SearchDownloadAlpha.class.getName());
	private final ExtensionSearchWindow searchWindow;
	private SearchDownloadInfo searchDownloadInfo;
	private Document document;
	private String placeToSave;
	private final String defaultFileToSaveName = new String ("\\result_lobo_search.txt");
	private final Object sync = new Object();
	
	private int stepsTextURL = 3;
	private int stepsPictureURL = 5;
	private int stepsTextRegExp = 3;
	private int stepsPictureRegExp = 3;
	
	private int stepTextURL = 0;
	private int stepPictureURL = 0;
	private int stepTextRegExp = 0;
	private int stepPictureRegExp = 0;
	
	SearchDownloadAlpha (){
		this.searchWindow = new ExtensionSearchWindow();
		searchSystems = new SearchSystems(searchWindow);
		translateSystem = new TranslateSystem(searchWindow);
	}	
	
	SearchDownloadAlpha (SearchDownloadInfo SDInfo){
		searchDownloadInfo = SDInfo;
		this.searchWindow = new ExtensionSearchWindow();
		searchSystems = new SearchSystems(searchWindow);
		translateSystem = new TranslateSystem(searchWindow);
	}
	
	public SearchDownloadInfo getSearchDownloadInfo(){
		return searchDownloadInfo;
	}	
	
	public void setSearchDownloadInfo(SearchDownloadInfo SDInfo){
		searchDownloadInfo = SDInfo;
	}
	
	private String getSource ()
	{
		String res;
		HtmlClientlet.setSynchronization (sync);
		synchronized (sync){
			try {
				sync.wait(60000);
			} catch (InterruptedException e) {
				logger.info("SearchDownloadAlpha: problems with synchronization");
			}
			res = searchWindow.getSource();
		}
		HtmlClientlet.deleteSynchronization();
		return res;
	}
	
	private String getSourceByURL (String url){
		try {
			java.net.URL search_url = new java.net.URL(url);
			searchWindow.navigate(search_url);
		} catch(java.net.MalformedURLException mfu) {
			logger.log(Level.WARNING, "getSourceByURL()", mfu);
			url = null;
		}
		return getSource ();
	}
	
	public void run() {
		java.io.File selectedFile = new java.io.File (searchDownloadInfo.getFileToSave());

		searchProgressBarThread.setDaemon(true);
		searchProgressBarThread.start();
		
		stepTextURL = 1;
		stepPictureURL = 1;
		stepTextRegExp = 1;
		stepPictureRegExp = 1;
		
		if (Thread.interrupted()) return;
		searchProgressBarThread.updateProgressBar(1, 100);
		System.out.println ("pr done.");
		switch (searchDownloadInfo.getSearchType()) {
		case URL:
			try {
				String htmlCode = this.getSourceByURL(searchDownloadInfo.getStringToSearch());
				java.io.File file = new java.io.File(selectedFile.getPath());
		    	if (!file.exists()) {
		        	file.createNewFile();
		    	}
		    	else {
		    		long time = System.currentTimeMillis();
		    		file = new java.io.File(placeToSave + time);
		    		file.createNewFile();
		    	}
		    	if (Thread.interrupted()) return;
				document = Jsoup.parse(htmlCode);
				if (Thread.interrupted()) return;
				switch (searchDownloadInfo.getInfType()) {
				case TEXT:
					searchProgressBarThread.updateProgressBar(++stepTextURL, stepsTextURL);
					placeToSave = selectedFile.getPath();
					if (!selectedFile.isFile()) {
						placeToSave += defaultFileToSaveName;
					}
					makeTextSearchDownloadByURL();
					break;
				case PICTURES:	
					searchProgressBarThread.updateProgressBar(++stepPictureURL, stepsPictureURL);
					if (!selectedFile.isDirectory()) {
						placeToSave = selectedFile.getParent();
					} else {
						placeToSave = selectedFile.getPath();
					}
					makePicturesSearchDownloadByURL();
				}
			} catch (IOException e) {
		        e.printStackTrace();
		    }
			break;
		case REGEXP:
			searchDownloadInfo.setStringToSearch(
					translateSystem.TranslateOnEnglish(
							searchDownloadInfo.getStringToSearch()));
			if (Thread.interrupted()) return;
			switch (searchDownloadInfo.getInfType()) {
			case TEXT:
				stepsTextRegExp = searchDownloadInfo.getNumberOfPages() * 3;
				searchProgressBarThread.updateProgressBar(++stepTextRegExp, stepsTextRegExp);
				if (!selectedFile.isDirectory()) {
					placeToSave = selectedFile.getParent();
				} else {
					placeToSave = selectedFile.getPath();
				}
				makeTextSearchDownloadByRequest();
				break;
			case PICTURES:	
				stepsPictureRegExp = searchDownloadInfo.getNumberOfPages() * 3;
				searchProgressBarThread.updateProgressBar(++stepPictureRegExp, stepsPictureRegExp);
				if (!selectedFile.isDirectory()) {
					placeToSave = selectedFile.getParent();
				} else {
					placeToSave = selectedFile.getPath();
				}
				makePicturesSearchDownloadByRequest();
			}
		}
		searchProgressBarThread.close();
	}
	
	private void makeTextSearchDownloadByURL(){
		try {
			java.io.File file = new java.io.File(placeToSave);
	    	if (!file.exists()) {
	        	file.createNewFile();
	    	}
	    	else {
	    		long time = System.currentTimeMillis();
	    		file = new java.io.File(placeToSave + time);
	    		file.createNewFile();
	    	}
	    	PrintWriter wrt = new PrintWriter(file);
			
	        Elements links = document.select("a[href]");
	        Elements media = document.select("[src]");
	        Elements imports = document.select("link[href]");
	        
	        String title = document.title();
	        Elements head = document.head().select("*");
	        Elements bodies = document.body().select("*");
	        
	        printInPlace(wrt, "\nMedia: (%d)", media.size());
	        for (Element src : media) {
	        	if (Thread.interrupted()) return;
	            if (src.tagName().equals("img"))
	            	printInPlace(wrt," * %s: <%s> %sx%s (%s)",
	                        src.tagName(), src.attr("src"), src.attr("width"), src.attr("height"),
	                        src.attr("alt"));
	            else
	            	printInPlace(wrt," * %s: <%s>", src.tagName(), src.attr("src"));
	        }

	        printInPlace(wrt,"\nImports: (%d)", imports.size());
	        for (Element link : imports) {
	        	if (Thread.interrupted()) return;
	        	printInPlace(wrt," * %s <%s> (%s)", link.tagName(),link.attr("href"), link.attr("rel"));
	        }

	        printInPlace(wrt,"\nLinks: (%d)", links.size());
	        for (Element link : links) {
	        	if (Thread.interrupted()) return;
	        	printInPlace(wrt," * a: <%s>  (%s)", link.attr("href"), link.text());
	        }
	        
	        printInPlace(wrt, "\n\n\nText:");
	        wrt.println(title);
	        wrt.println(head.text());
	        for(Element body : bodies )
	        {
	        	if (Thread.interrupted()) return;
	            wrt.println(body.getAllElements().text());
	        }
	        
	        wrt.close();
		} catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private void makePicturesSearchDownloadByURL(){
		Elements images = document.select("img[src~=(?i)\\.(png|jpg|gif|bmp|fst|swf)]");
	    for (Element image : images) {
	    	if (Thread.interrupted()) return;
	        String[] parts = image.attr("src").split("\\/");
            String fileName = placeToSave.concat("\\" + parts[parts.length - 1]); 
            try {
            	BufferedImage img = ImageIO.read(new URL(CorrectProtocol(image.attr("src"),
            			searchDownloadInfo.getStringToSearch())));
            	java.io.File file = new java.io.File(fileName);
            	if (!file.exists()) {
                	file.createNewFile();
            	}
            	ImageIO.write(img, "png", file);           	
            } catch (IOException e) {
		        e.printStackTrace();
		    }
        }		
	}
	
	private void makeTextSearchDownloadByRequest(){
		String[] urls = searchSystems.findURLs(searchDownloadInfo);
		int urls_num = urls.length;
		for (int i = 0; i < urls_num; i++){
			if (Thread.interrupted()) return;
			searchProgressBarThread.updateProgressBar(++stepTextRegExp, stepsTextRegExp);
			if (urls[i] == null) continue;
			String htmlCode = this.getSourceByURL(urls[i]);
			document = Jsoup.parse(htmlCode);
			try {
	            String fileName = placeToSave.concat("\\" + "result" + i + ".txt"); 
            	java.io.File file = new java.io.File(fileName);
            	if (!file.exists()) {
                	file.createNewFile();
            	}			
    	    	PrintWriter wrt = new PrintWriter(file);
    			
    	        Elements links = document.select("a[href]");
    	        Elements media = document.select("[src]");
    	        Elements imports = document.select("link[href]");
    	        
    	        String title = document.title();
    	        Elements head = document.head().select("*");
    	        Elements bodies = document.body().select("*");
    	        
    	        printInPlace(wrt, "\nMedia: (%d)", media.size());
    	        for (Element src : media) {
    	            if (src.tagName().equals("img"))
    	            	printInPlace(wrt," * %s: <%s> %sx%s (%s)",
    	                        src.tagName(), src.attr("src"), src.attr("width"), src.attr("height"),
    	                        src.attr("alt"));
    	            else
    	            	printInPlace(wrt," * %s: <%s>", src.tagName(), src.attr("src"));
    	        }

    	        printInPlace(wrt,"\nImports: (%d)", imports.size());
    	        for (Element link : imports) {
    	        	printInPlace(wrt," * %s <%s> (%s)", link.tagName(),link.attr("href"), link.attr("rel"));
    	        }

    	        printInPlace(wrt,"\nLinks: (%d)", links.size());
    	        for (Element link : links) {
    	        	printInPlace(wrt," * a: <%s>  (%s)", link.attr("href"), link.text());
    	        }
    	        
    	        printInPlace(wrt, "\n\n\nText:");
    	        wrt.println(title);
    	        wrt.println(head.text());
    	        for(Element body : bodies )
    	        {
    	        	if (Thread.interrupted()) return;
    	            wrt.println(body.getAllElements().text());
    	        }
    	        
    	        wrt.close();
			} catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	private void makePicturesSearchDownloadByRequest(){
		String[] urls = searchSystems.findURLs(searchDownloadInfo);
		int urls_num = urls.length;
		stepsPictureRegExp = urls_num;
		for (int i = 0; i < urls_num; i++){
			if (Thread.interrupted()) return;
			searchProgressBarThread.updateProgressBar(++stepPictureRegExp, stepsPictureRegExp);
			String htmlCode = this.getSourceByURL(urls[i]);
			document = Jsoup.parse(htmlCode);
			Elements images = document.select("img[src~=(?i)\\.(png|jpg|gif|bmp|fst|swf)]");
		    for (Element image : images) {
		    	if (Thread.interrupted()) return;
		        String[] parts = image.attr("src").split("\\/");
	            String fileName = placeToSave.concat("\\" + parts[parts.length - 1]); 
	            try {
	            	BufferedImage img = ImageIO.read(new URL(CorrectProtocol(image.attr("src"),
	            			urls[i])));
	            	java.io.File file = new java.io.File(fileName);
	            	if (!file.exists()) {
	                	file.createNewFile();
	            	}
	            	ImageIO.write(img, "png", file);           	
	            } catch (IOException e) {
			        e.printStackTrace();
			    }
	        }
		}		
	}
	
	private String[] findURLs()
	{
		String[] res = new String[searchDownloadInfo.getNumberOfPages() * 2];
		String link;
		int counter = 0, res_counter = 0;
		//Google
		searchSystems.navigate (searchDownloadInfo.getStringToSearch(),
				SearchSystemType.GOOGLE);
		String htmlCode = searchSystems.getSourceGoogleSearch();
		document = Jsoup.parse(htmlCode);
		Elements srhs = document.select("a[href]");
		for (Element srh : srhs)
		{
			link = SearchSystems.ChekcLinkOnResultOfGoogleSeacrh(srh.attr("href"));
			if (link != null){
				res[res_counter++] = link;
				counter++;
				if (counter == searchDownloadInfo.getNumberOfPages()) break;
			}
		}
		
		//Yahoo
		searchSystems.navigate (searchDownloadInfo.getStringToSearch(),
				SearchSystemType.YAHOO);
		htmlCode = searchSystems.getSourceYahooSearch();
		String[] resarr = htmlCode.split("target=\"_blank\""), linkarr;
		counter = ((searchDownloadInfo.getNumberOfPages() < resarr.length) ? searchDownloadInfo.getNumberOfPages() : resarr.length);
		for (int i = 0; i < counter; i++)
		{
			linkarr = resarr[i].split("/RU=");
			if (linkarr.length == 1) {
				counter++;
				continue;
			}
			linkarr = linkarr[linkarr.length - 1].split("/RK=");
			if (linkarr.length == 1) {
				counter++;
				continue;
			}
			link = SearchSystems.GetYahooLink(linkarr[0]);
			if (link != null){
				res[res_counter++] = link;
			}		
		}
		return res;
	}
	
	
	
    private void printInPlace(PrintWriter wrt, String msg, Object... args) {
    	wrt.println(String.format(msg, args));
    }
    
    public static String CorrectProtocol (String link, String page){
    	if (link.length() > 8 && 
    			link.charAt(0) == 'h' && link.charAt(1) == 't' &&
    			link.charAt(2) == 't' && link.charAt(3) == 'p' && 
    			link.charAt(4) == ':'){
    		return link;
    	}
    	else if (link.charAt(0) == '/')
    	{
    		if (link.charAt(1) == '/') return ("http:" + link);
    		else return null;
    	}
    	else return null;
    }
}
