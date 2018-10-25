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

public class LeetConverter {
	public enum UseLeet {
		NotAtAll, 
		BeforeGeneratingPassword, 
		AfterGeneratingPassword, 
		BeforeAndAfterGeneratingPassword;
		
		public boolean useBefore() {
			return this == BeforeAndAfterGeneratingPassword 
				|| this == BeforeGeneratingPassword;
		}
		
		public boolean useAfter() {
			return this == BeforeAndAfterGeneratingPassword 
				|| this == AfterGeneratingPassword;
		}
	}
	
	public enum LeetLevel {
		One(0, "4bcd3fghijk1mn0p9rs7uvwxyz"), 
		Two(1, "4bcd3fgh1jk1mn0p9r57uvwxy2"), 
		Three(2, new String[] {"4", "8", "c", "d", "3", "f", "6", "h", "'", "j", "k", "1",
				"m", "n", "0", "p", "9", "r", "5", "7", "u", "v", "w", "x",
				"'/", "2"}),
		Four(3, new String[]{"@", "8", "c", "d", "3", "f", "6", "h", "'", "j", "k", "1",
				"m", "n", "0", "p", "9", "r", "5", "7", "u", "v", "w", "x",
				"'/", "2"}), 
		Five(4,new String[]{"@", "|3", "c", "d", "3", "f", "6", "#", "!", "7", "|<", "1",
				"m", "n", "0", "|>", "9", "|2", "$", "7", "u", "\\/", "w",
				"x", "'/", "2"}), 
		Six(5, new String[]{"@", "|3", "c", "|)", "&", "|=", "6", "#", "!", ",|", "|<",
				"1", "m", "n", "0", "|>", "9", "|2", "$", "7", "u", "\\/",
				"w", "x", "'/", "2"}), 
		Seven(6,new String[]{"@", "|3", "[", "|)", "&", "|=", "6", "#", "!", ",|", "|<",
				"1", "^^", "^/", "0", "|*", "9", "|2", "5", "7", "(_)", "\\/",
				"\\/\\/", "><", "'/", "2"}), 
		Eight(7, new String[]{"@", "8", "(", "|)", "&", "|=", "6", "|-|", "!", "_|", "|(",
				"1", "|\\/|", "|\\|", "()", "|>", "(,)", "|2", "$", "|", "|_|",
				"\\/", "\\^/", ")(", "'/", "\"/_"}), 
		Nine(8, new String[]{"@", "8", "(", "|)", "&", "|=", "6", "|-|", "!", "_|", "|{",
				"|_", "/\\/\\", "|\\|", "()", "|>", "(,)", "|2", "$", "|",
				"|_|", "\\/", "\\^/", ")(", "'/", "\"/_"});
		
		public final short value;
		private final String[] characterSet;
		LeetLevel(int value, String characterSet) {
			this.value = (short)value;
			String[] charSet = new String[characterSet.length()];
			for ( int i = 0; i < characterSet.length(); ++i) {
				charSet[i] = String.valueOf(characterSet.charAt(i));
			}
			this.characterSet = charSet;
		}
		LeetLevel(int value, String[] characterSet) {
			this.value = (short)value;
			this.characterSet = characterSet;
		}
		public String[] getCharacterSet() {
			return characterSet;
		}
	}
	
	public static String convert(LeetLevel level, String text) {
		final String[] charSet = level.characterSet;
		final int multipler = (level.value > 6 ? 3 : level.value >= 5 ? 2 : 1);
		final int length = text.length();
		StringBuilder buffer = new StringBuilder(length * multipler);
		for ( int index = 0; index < length; ++index) {
			final char c = text.charAt(index);
			// can not use Character.isLetter because this only works for ascii
			// letters a through z, not for unicode letters
			final char tc = Character.toLowerCase(c);
			if ( tc >= 'a' && tc <= 'z' ) {
				buffer.append(charSet[tc - 'a']);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
	
}
