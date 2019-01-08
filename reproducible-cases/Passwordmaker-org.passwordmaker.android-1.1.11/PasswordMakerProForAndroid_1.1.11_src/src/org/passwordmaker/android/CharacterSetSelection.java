/*
 *  Copyright 2011 James Stapleton
 * 
 *  This file is part of PasswordMaker Pro For Android.
 *
 *  PasswordMaker Pro For Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  PasswordMaker Pro For Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PasswordMaker Pro For Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.passwordmaker.android;

public enum CharacterSetSelection {
	alphaNumSym("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`~!@#$%^&*()_-+={}|[]\\:\";'<>?,./"), 
	alphaNum("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"), 
	hex("0123456789abcdef"), 
	num("0123456789"), 
	alpha("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"), 
	sym("`~!@#$%^&*()_-+={}|[]\\:\";'<>?,./"),
	custom("custom");
	
	private final String characterSet;
	
	private CharacterSetSelection(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public static CharacterSetSelection findSetName(String characters) {
		for ( CharacterSetSelection s : CharacterSetSelection.values() ) {
			if ( s.getCharacterSet().equals(characters) ) return s;
		}
		return custom;
	}
}
