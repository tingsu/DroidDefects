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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PwmProfileList implements Map<String, PwmProfile> {
	private Map<String, PwmProfile> profiles = new HashMap<String, PwmProfile>();
	
	public PwmProfileList() {
		
	}
	
	public PwmProfileList(Map<String, PwmProfile> profileList) {
		this.profiles = profileList;
	}

	public boolean set(PwmProfile profile) {
		profiles.put(profile.getName(), profile);
		return true;
	}

	public String[] toArray() {
		return toArray(new String[0]);
	}

	public <T> T[] toArray(T[] array) {
		return profiles.values().toArray(array);
	}

	public String[] toProfileNames() {
		Set<String> vals = profiles.keySet();
		return vals.toArray(new String[0]);
	}

	public void add(String name) {
		set(new PwmProfile(name));
		
	}

	public void clear() {
		profiles.clear();
		
	}

	public boolean containsKey(Object key) {
		return profiles.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return profiles.containsValue(value);
	}

	public Set<Map.Entry<String, PwmProfile>> entrySet() {
		return profiles.entrySet();
	}

	public PwmProfile get(Object key) {
		return profiles.get(key);
	}

	public boolean isEmpty() {
		return profiles.isEmpty();
	}

	public Set<String> keySet() {
		return profiles.keySet();
	}

	public PwmProfile put(String key, PwmProfile value) {
		return profiles.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends PwmProfile> objects) {
		putAll(objects.values());
	}

	public void putAll(Collection<? extends PwmProfile> objects) {
		for ( PwmProfile profile : objects ) {
			set(profile);
		}
	}
	
	public PwmProfile remove(Object key) {
		return profiles.remove(key);
	}

	public int size() {
		return profiles.size();
	}

	public Collection<PwmProfile> values() {
		return profiles.values();
	}
	
}
