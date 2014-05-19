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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lobobrowser.gui.ExtensionSearchWindow;
import org.lobobrowser.primary.clientlets.html.HtmlClientlet;
import org.lobobrowser.primary.ext.SearchSystems;
import org.lobobrowser.primary.ext.SearchSystems.SearchSystemType;
import org.lobobrowser.primary.ext.TranslateSystem;

public class BestSearchAlpha extends Thread{
	private final SearchProgressBarThread searchProgressBarThread = new SearchProgressBarThread(this);
	private final SearchSystems searchSystems;
	private final TranslateSystem translateSystem;
	private static final Logger logger = Logger.getLogger(BestSearchAlpha.class.getName());
	private final ExtensionSearchWindow searchWindow;
	private BestSearchInfo bestSearchInfo;
	private Document document;
	private String placeToSave;
	private final String defaultFileToSaveName = new String ("\\result_lobo_search.txt");
	
	BestSearchAlpha (){
		this.searchWindow = new ExtensionSearchWindow();
		searchSystems = new SearchSystems(searchWindow);
		translateSystem = new TranslateSystem(searchWindow);
	}	
	
	BestSearchAlpha (BestSearchInfo BSInfo){
		bestSearchInfo = BSInfo;
		this.searchWindow = new ExtensionSearchWindow();
		searchSystems = new SearchSystems(searchWindow);
		translateSystem = new TranslateSystem(searchWindow);
	}
	
	public BestSearchInfo getBestSearchAlpha(){
		return bestSearchInfo;
	}	
	
	public void setBestSearchInfo(BestSearchInfo BSInfo){
		bestSearchInfo = BSInfo;
	}
	
	private String getSource (){
		return searchSystems.getSource();
	}
	
	private String getURL(){
		return searchWindow.getURL();
	}
	
	private String getSourceByURL (String url){
		return searchSystems.getSourceByURL(url);
	}
	
	@Override
	public void run() {
		java.io.File selectedFile = new java.io.File (bestSearchInfo.getFileToSave());
		
		searchProgressBarThread.setDaemon(true);
		searchProgressBarThread.start();
		
		placeToSave = selectedFile.getPath();
		if (!selectedFile.isFile()) {
			placeToSave += defaultFileToSaveName;
		}
		if (Thread.interrupted()) return;
		makeBestSearchByRequest();
		
		searchProgressBarThread.close();
	}
	
	private class UrlAndQuality{
		private String url;
		private Integer quality;
		
		public UrlAndQuality(String url, int quality){
			this.url = url;
			this.quality = quality;
		}
		
		public String getURL(){
			return this.url;
		}
	}
		
	private void makeBestSearchByRequest(){
		bestSearchInfo.setStringToSearch(
				translateSystem.TranslateOnEnglish(
						bestSearchInfo.getStringToSearch()));
		String[] urls = searchSystems.findURLs(bestSearchInfo);
		int urls_num = urls.length;
//		Pattern stringToSearch = Pattern.compile(bestSearchInfo.getStringToSearch());
		Vector<UrlAndQuality> searchResultToSort = new Vector<UrlAndQuality>();
		for (int i = 0; i < urls_num; i++){
			if (Thread.interrupted()) return;
			if (urls[i] == null) continue;
//			String htmlCode = this.getSourceByURL(urls[i]);
//			Matcher matchStringToSearch = stringToSearch.matcher(htmlCode);
	        int counter = 0;	        
//	        while(matchStringToSearch.find()) {
//	            counter++;
//	        }
	        searchResultToSort.add(new UrlAndQuality(urls[i], counter));
		}
//		searchResultToSort.sort(new Comparator<UrlAndQuality>(){
//			public int compare(UrlAndQuality o1, UrlAndQuality o2){
//				return o1.quality.compareTo(o2.quality);
//			}
//		});
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
	        
	    	for (int i = searchResultToSort.size() - 1; i >= 0; i--){
	    		wrt.println(searchResultToSort.elementAt(i).url);
	    	}
	        wrt.close();
		} catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
    private void printInPlace(PrintWriter wrt, String msg, Object... args) {
    	wrt.println(String.format(msg, args));
    }
    
    public static String CorrectProtocol (String link){
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
