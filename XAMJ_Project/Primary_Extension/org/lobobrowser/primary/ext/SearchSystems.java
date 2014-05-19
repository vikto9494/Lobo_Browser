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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lobobrowser.primary.ext.SearchDownloadInfo;
import org.lobobrowser.primary.ext.SearchDownloadInfo.InfType;
import org.lobobrowser.gui.ExtensionSearchWindow;
import org.lobobrowser.html.FormInput;
import org.lobobrowser.html.domimpl.HTMLElementImpl;
import org.lobobrowser.html.domimpl.NodeVisitor;
import org.lobobrowser.primary.clientlets.html.HtmlClientlet;
import org.lobobrowser.primary.clientlets.html.HtmlRendererContextImpl.LocalParameterInfo;
import org.w3c.dom.Node;

public class SearchSystems {
	private static final Logger logger = Logger.getLogger(SearchDownloadAlpha.class.getName());
	private final ExtensionSearchWindow searchWindow;
	private static final int MinLengthOfGoogleSearch = 7;
	private static final int MinLengthOfYahooSearch = 8;
	private final Object sync = new Object();
	
	public SearchSystems (ExtensionSearchWindow searchWindow){
		this.searchWindow = searchWindow;
	}
	
	public static String ChekcLinkOnResultOfGoogleSeacrh (String link_url){
		if (link_url.length() < MinLengthOfGoogleSearch) return null;
		if (link_url.charAt(0) != '/') return null;
		if (link_url.charAt(1) != 'u') return null;
		if (link_url.charAt(2) != 'r') return null;
		if (link_url.charAt(3) != 'l') return null;
		if (link_url.charAt(4) != '?') return null;
		if (link_url.charAt(5) != 'q') return null;
		if (link_url.charAt(6) != '=') return null;
		String res = link_url.substring(MinLengthOfGoogleSearch, link_url.length());
		return res;
	}
	
	public static String CorrectHtmlYahoo (String htmlSource){
		int srclen = htmlSource.length();
		int runlen = srclen - MinLengthOfYahooSearch;
		String res = new String ("");
		for (int i = 0; i < runlen; i++){
			res = res + String.valueOf(htmlSource.charAt(i));
			if (htmlSource.charAt(i) == '"'){
				if (htmlSource.charAt(i+1) == 't' && htmlSource.charAt(i+2) == 'a' &&
						htmlSource.charAt(i+3) == 'r' && htmlSource.charAt(i+4) == 'g' &&
						htmlSource.charAt(i+3) == 'e' && htmlSource.charAt(i+4) == 't'){
					res = res + " ";
				}
			}
		}
		for (int i = runlen; i < srclen; i++){
			res = res + String.valueOf(htmlSource.charAt(i));
		}
		return res;
	}
	
	public static String GetYahooLink (String link){
		int srclen = link.length();
		int runlen = srclen - 3;
		String res = new String ("");
		for (int i = 0; i < runlen; i++){
			if (link.charAt(i) == '%'){
				if (link.charAt(i+1) == '3' && link.charAt(i+2) == 'a'){
					res = res + ":";
				}
				if (link.charAt(i+1) == '2' && link.charAt(i+2) == 'f'){
					res = res + "/";
				}
				i += 2;
			}
			else
				res = res + String.valueOf(link.charAt(i));
		}
		for (int i = runlen; i < srclen; i++){
			res = res + String.valueOf(link.charAt(i));
		}
		return res;
	}
	
	enum SearchSystemType {
		GOOGLE, YANDEX, YAHOO, RAMBLER, NOT_DEFINED;
		public static SearchSystemType fromInt (int value)
		{
		  switch (value)
		  {
		  case 0: return GOOGLE;
		  case 1: return YANDEX;
		  case 2: return YAHOO;
		  case 3: return RAMBLER;
		  default: return NOT_DEFINED;
		  }
		}
		
		public static String GetSearchSystemUrlString (SearchSystemType searchSystem){
			switch (searchSystem)
			{
			case GOOGLE: return ("http://www.google.ru/search");
			case YANDEX: return null;
			case YAHOO: return ("https://search.yahoo.com/search");
			case RAMBLER: return null;
			default: return null;
			}
		}
		
		public static FormInput[] GetFormInputsForGoogleSearch (String stringToSearch){
			FormInput[] formInputs = new FormInput[6];
			formInputs[0] = new FormInput("btnG","Поиск в Google");
			formInputs[1] = new FormInput("ie","windows-1251");
			formInputs[2] = new FormInput("hi","ru");
			formInputs[3] = new FormInput("source","hp");
			formInputs[4] = new FormInput("q",stringToSearch);
			formInputs[5] = new FormInput("gvb","1");
			return formInputs;
		}
		
		public static FormInput[] GetFormInputsForYahooSearch (String stringToSearch){
			FormInput[] formInputs = new FormInput[6];
			formInputs[0] = new FormInput("btnG","Поиск в Google");
			formInputs[1] = new FormInput("ie","windows-1251");
			formInputs[2] = new FormInput("hi","ru");
			formInputs[3] = new FormInput("source","hp");
			formInputs[4] = new FormInput("q",stringToSearch);
			formInputs[5] = new FormInput("gvb","1");
			return formInputs;
		}
		
		public static FormInput[] getFormInputs (String stringToSearch, 
				SearchSystemType searchSystemType){
			switch (searchSystemType)
			{
			case GOOGLE: return GetFormInputsForGoogleSearch(stringToSearch);
			case YANDEX: return null;
			case YAHOO: return GetFormInputsForYahooSearch(stringToSearch);
			case RAMBLER: return null;
			default: return null;		
			}
		}
	}
	
	public void navigate (String stringToSearch, SearchSystemType searchSystemType){
		java.net.URL search_url;
		String method = new String ("GET");
		String enctype = null;
		FormInput[] formInputs = SearchSystemType.
				getFormInputs(stringToSearch, searchSystemType);
		ParameterInfo paramInfo = new LocalParameterInfo(enctype, formInputs);
		TargetType type = TargetType.SELF;
		RequestType requestType = RequestType.FORM;
		try {
			search_url = new java.net.URL(
					SearchSystemType.GetSearchSystemUrlString(searchSystemType));
		} catch(java.net.MalformedURLException mfu) {
			logger.log(Level.WARNING, "getSourceByURL()", mfu);
			search_url = null;
		}
		searchWindow.navigate(search_url, method, paramInfo, type, requestType);
	}
	
	public String getSourceGoogleSearch ()
	{
		String res;
		HtmlClientlet.setSynchronization (sync);
		synchronized (sync){
			try {
				sync.wait(10000);
			} catch (InterruptedException e) {
				logger.info("SearchDownloadAlpha: problems with synchronization");
			}
			res = searchWindow.getSource();
			for (int i = 0; i < 50; i++)
			{
				if (res.contains("/url?q=")) break;
				try {
					sync.wait(2000);
				} catch (InterruptedException e) {
					logger.info("SearchDownloadAlpha: problems with synchronization");
				}
				res = searchWindow.getSource();
			}
		}
		HtmlClientlet.deleteSynchronization();
		return res;
	}
	
	public String getSourceYahooSearch ()
	{
		String res;
		HtmlClientlet.setSynchronization (sync);
		synchronized (sync){
			try {
				sync.wait(10000);
			} catch (InterruptedException e) {
				logger.info("SearchDownloadAlpha: problems with synchronization");
			}
			res = searchWindow.getSource();
			for (int i = 0; i < 50; i++)
			{
				if (res.contains("target=\"_blank\"")) break;
				try {
					sync.wait(2000);
				} catch (InterruptedException e) {
					logger.info("SearchDownloadAlpha: problems with synchronization");
				}
				res = searchWindow.getSource();
			}
		}
		HtmlClientlet.deleteSynchronization();
		return res;
	}
	
	public String getSourceGoogleSearchByPageURL (int page, String URL)
	{
		int link = page*10;
		String[] arr = URL.split("&start=");
		String result_url = arr[0] + "&start=" + link + "&sa=N";
		return result_url;
	}
	
	public String getSourceYahooSearchByPageURL (int page, String URL)
	{
		int link = page*10 + 1;
		String[] arr = URL.split("fr2=&iscqry=");
		String result_url = URL.split("fr2=&iscqry=")[0] + "b=" + link;
		return result_url;
	}
	
	public String getSource (){
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
	
	public String getURL(){
		return searchWindow.getURL();
	}
	
	public String getSourceByURL (String url){
		try {
			java.net.URL search_url = new java.net.URL(url);
			searchWindow.navigate(search_url);
		} catch(java.net.MalformedURLException mfu) {
			logger.log(Level.WARNING, "getSourceByURL()", mfu);
			url = null;
		}
		return getSource ();
	}
	
	public String[] findURLs(SearchInfo searchInfo)
	{
		final int MinLinkLength = 30;
		final int MaxUsedPagesCount = 30;
		String[] res = new String[searchInfo.getNumberOfPages() * 2];
		final int resSize = searchInfo.getNumberOfPages() * 2;
		String link;
		int counter = 0, res_counter = 0;
		//Google
		this.navigate (searchInfo.getStringToSearch(),
				SearchSystemType.GOOGLE);
		String htmlCode = this.getSourceGoogleSearch();
		Document document = Jsoup.parse(htmlCode);
		Elements srhs = document.select("a[href]");
		for (Element srh : srhs)
		{
			link = SearchSystems.ChekcLinkOnResultOfGoogleSeacrh(srh.attr("href"));
			if (link != null  && 
					res_counter < resSize){
				res[res_counter++] = link;
				counter++;
				if (counter == searchInfo.getNumberOfPages()) break;
			}
		}
		String StartGoogleURL = getURL();
		for (int numberOfCurrentPage = 0; counter < searchInfo.getNumberOfPages() && numberOfCurrentPage < MaxUsedPagesCount; numberOfCurrentPage++){
			htmlCode = this.getSourceByURL(
					this.getSourceGoogleSearchByPageURL(numberOfCurrentPage, StartGoogleURL));
			document = Jsoup.parse(htmlCode);
			srhs = document.select("a[href]");
			for (Element srh : srhs)
			{
				link = SearchSystems.ChekcLinkOnResultOfGoogleSeacrh(srh.attr("href"));
				if (link != null  && 
						res_counter < resSize){
					res[res_counter++] = link;
					counter++;
					if (counter == searchInfo.getNumberOfPages()) break;
				}
			}			
		}
		
		//Yahoo
		this.navigate (searchInfo.getStringToSearch(),
				SearchSystemType.YAHOO);
		htmlCode = this.getSourceYahooSearch();
		String[] resarr = htmlCode.split("target=\"_blank\""), linkarr;
		counter = ((searchInfo.getNumberOfPages() < resarr.length) ? searchInfo.getNumberOfPages() : resarr.length);
		for (int i = 0; i < counter && i < resarr.length; i++)
		{
//			System.out.println("i: " + i + ";counter: " + counter + ";resarrlen: " + resarr.length + ";NP: " + bestSearchInfo.getNumberOfPages());
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
			if (link != null && link.length() > MinLinkLength && 
					res_counter < resSize){
				res[res_counter++] = link;
			}		
		}
		String StartYahooURL = getURL();
		for (int numberOfCurrentPage = 0; res_counter < searchInfo.getNumberOfPages()*2 && numberOfCurrentPage < MaxUsedPagesCount; numberOfCurrentPage++){
			htmlCode = this.getSourceByURL(
					this.getSourceYahooSearchByPageURL(numberOfCurrentPage, StartYahooURL));
			document = Jsoup.parse(htmlCode);
			resarr = htmlCode.split("target=\"_blank\"");
			counter = ((searchInfo.getNumberOfPages() < resarr.length) ? searchInfo.getNumberOfPages() : resarr.length);
			for (int i = 0; i < counter && i < resarr.length; i++)
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
				if (link != null && link.length() > MinLinkLength && 
						res_counter < resSize){
					res[res_counter++] = link;
				}		
			}		
		}
		return res;
	}
}
