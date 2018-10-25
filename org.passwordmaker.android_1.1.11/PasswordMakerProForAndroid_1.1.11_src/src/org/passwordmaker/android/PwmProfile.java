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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;

import com.tasermonkeys.google.json.Gson;
import com.tasermonkeys.google.json.GsonBuilder;

public class PwmProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum UrlComponents {
		Protocol, Subdomain, Domain, PortPathAnchorQuery
	}
	
	
	private String name = "Default";
	private HashAlgo currentAlgo = HashAlgo.MD5;
	private transient PwmHashAlgorithm hashAlgo = PwmHashAlgorithm.get(currentAlgo);
	private UseLeet useLeet = UseLeet.NotAtAll;
	private LeetLevel leetLevel = LeetLevel.One;
	private EnumSet<UrlComponents> urlComponents = defaultUrlComponents();
	private short lengthOfPassword = 8;
	private String username = "";
	private String modifier = "";
	private String characters = CharacterSetSelection.alphaNum.getCharacterSet();
	private String passwordPrefix = "";
	private String passwordSuffix = "";
	private Set<String> pwmFavoriteInputs = new HashSet<String>();
	private boolean storePasswordHash = false;
	private String currentPasswordHash = null;
	private String passwordSalt = null;
	
	public PwmProfile() {
	}

	public PwmProfile(String name) {
		this.name = name;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		hashAlgo = PwmHashAlgorithm.get(currentAlgo);
	}

	public static EnumSet<UrlComponents> defaultUrlComponents() {
		return EnumSet.of(UrlComponents.Domain);
	}
	
	public PwmHashAlgorithm getHashAlgo() {
		if ( hashAlgo == null ) {
            hashAlgo = PwmHashAlgorithm.get(currentAlgo);
        }
        return hashAlgo;
	}

	public void setHashAlgo(PwmHashAlgorithm hashAlgo) {
        if ( hashAlgo == null ) throw new NullPointerException("Hash algorithm is null");
		this.hashAlgo = hashAlgo;
		this.currentAlgo = hashAlgo.getHashAlgo();
	}
	
	public UseLeet getUseLeet() {
		return useLeet;
	}

	public void setUseLeet(UseLeet useLeet) {
		this.useLeet = useLeet;
	}

	public LeetLevel getLeetLevel() {
		return leetLevel;
	}

	public void setLeetLevel(LeetLevel leetLevel) {
		this.leetLevel = leetLevel;
	}

	public EnumSet<UrlComponents> getUrlComponents() {
		return urlComponents;
	}

	public void setUrlComponents(EnumSet<UrlComponents> urlComponents) {
		this.urlComponents = urlComponents;
	}

	public short getLengthOfPassword() {
		return lengthOfPassword;
	}

	public void setLengthOfPassword(short lengthOfPassword) {
		this.lengthOfPassword = lengthOfPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getCharacters() {
		return characters;
	}

	public void setCharacters(CharacterSetSelection characters) {
		setCharacters(characters.getCharacterSet());
	}
	
	public void setCharacters(String characters) {
		this.characters = characters;
	}

	public String getPrefix() {
		return passwordPrefix;
	}

	public void setPrefix(String passwordPrefix) {
		this.passwordPrefix = passwordPrefix;
	}

	public String getSuffix() {
		return passwordSuffix;
	}

	public void setSuffix(String passwordSuffix) {
		this.passwordSuffix = passwordSuffix;
	}
	
	public String getName() {
		return name;
	}
	
	public HashAlgo getCurrentAlgo() {
		return currentAlgo;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setHashAlgo(HashAlgo algo) {
		setHashAlgo(PwmHashAlgorithm.get(algo));
	}
	
	public Set<String> getFavorites() {
		return pwmFavoriteInputs;
	}
	
	public boolean addFavorite(String newFav) {
		return pwmFavoriteInputs.add(newFav);
	}

	public void addFavorite(List<String> favs) {
		pwmFavoriteInputs.addAll(favs);
	}
	

	public void setProfiles(Collection<String> favorites) {
		pwmFavoriteInputs.clear();
		pwmFavoriteInputs.addAll(favorites);
	}

	
	public String getCurrentPasswordHash() {
		return currentPasswordHash;
	}

	public void setCurrentPasswordHash(String currentPasswordHash, String salt) {
		this.currentPasswordHash = currentPasswordHash;
		this.passwordSalt = salt;
		setStorePasswordHash(true);
	}
	
	public String getPasswordSalt() {
		return this.passwordSalt;
	}
	
	public boolean hasPasswordHash() {
		return shouldStorePasswordHash() && this.passwordSalt != null && this.passwordSalt.length() > 0 && this.currentPasswordHash != null && this.currentPasswordHash.length() > 0;
	}
	
	public boolean shouldStorePasswordHash() {
		return storePasswordHash;
	}

	public void setStorePasswordHash(boolean storePasswordHash) {
		this.storePasswordHash = storePasswordHash;
		if ( !storePasswordHash ) {
			this.passwordSalt = null;
			this.currentPasswordHash = null;
		}
	}

	public void disablePasswordHash() {
		setStorePasswordHash(false);
	}


    @Override
    public String toString() {
        return "PwmProfile{" +
                "name='" + name + '\'' +
                ", currentAlgo=" + currentAlgo +
                ", hashAlgo=" + hashAlgo +
                ", useLeet=" + useLeet +
                ", leetLevel=" + leetLevel +
                ", urlComponents=" + urlComponents +
                ", lengthOfPassword=" + lengthOfPassword +
                ", username='" + username + '\'' +
                ", modifier='" + modifier + '\'' +
                ", characters='" + characters + '\'' +
                ", passwordPrefix='" + passwordPrefix + '\'' +
                ", passwordSuffix='" + passwordSuffix + '\'' +
                ", pwmFavoriteInputs=" + pwmFavoriteInputs +
                ", storePasswordHash=" + storePasswordHash +
                ", currentPasswordHash='" + currentPasswordHash + '\'' +
                ", passwordSalt='" + passwordSalt + '\'' +
                '}';
    }
}
