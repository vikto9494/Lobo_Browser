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

public class BestSearchInfo extends SearchInfo{
	private String fileToSave;
	BestSearchInfo ()
	{
		super();
	}
	
	BestSearchInfo (String stringToSearch, String fileToSave, Integer numberOfPages)
	{
		super(stringToSearch, numberOfPages);
		this.fileToSave = fileToSave;
	}
	
	public String getFileToSave ()
	{
		return fileToSave;
	}
	
	public void setFileToSave (String fileToSave)
	{
		this.fileToSave = fileToSave;
	}
}
