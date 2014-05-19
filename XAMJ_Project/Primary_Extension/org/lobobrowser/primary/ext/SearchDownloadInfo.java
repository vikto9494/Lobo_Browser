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

public class SearchDownloadInfo  extends SearchInfo{
	enum InfType {
		TEXT, PICTURES;
		public static InfType fromInt (int value)
		{
		  switch (value)
		  {
		  case 0: return TEXT;
		  default: return PICTURES;
		  }
		}
	}
	enum SearchType {
		URL, REGEXP;
		public static SearchType fromInt (int value)
		{
		  switch (value)
		  {
		  case 0: return URL;
		  default: return REGEXP;
		  }
		}		
	};
	
	private String fileToSave;
	private InfType infType;
	private SearchType searchType;
	
	SearchDownloadInfo ()
	{
		super();
	}
	
	SearchDownloadInfo (String stringToSearch, String fileToSave, InfType infType, SearchType searchType, Integer numberOfPages)
	{
		super(stringToSearch, numberOfPages);
		this.fileToSave = fileToSave;
		this.infType = infType;
		this.searchType = searchType;
	}
	
	public String getFileToSave ()
	{
		return fileToSave;
	}
	
	public InfType getInfType ()
	{
		return infType;
	}
	
	public SearchType getSearchType ()
	{
		return searchType;
	}
	
	public void setFileToSave (String fileToSave)
	{
		this.fileToSave = fileToSave;
	}
	
	public void setInfType (InfType infType)
	{
		this.infType = infType;
	}
	
	public void setSearchType (SearchType searchType)
	{
		this.searchType = searchType;
	}
}

