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

public class IntArrayList {
	int[] data;
	int capacity;
	int size = 0;
	
	public IntArrayList(int capacity) {
		this.capacity = capacity;
		data = new int[capacity];
	}
	
	public IntArrayList() {
		data = null;
		capacity = 0;
	}
	
	public IntArrayList(int[] data) {
		this.data = data;
		this.size = data.length;
		this.capacity = data.length;
	}
	
	public int length() {
		return size;
	}
	
	public int capacity() {
		return capacity;
	}
	
	public void setCapacity(int newCapacity) {
		int iterateTo = Math.min(newCapacity, size);
		int[] tmp = new int[newCapacity];
		for ( int i = 0; i < iterateTo; ++i ) {
			tmp[i] = data[i];
		}
		capacity = newCapacity;
		data = tmp;
	}
	
	public int get(int index) {
		return data[index];
	}
	
	public void put(int index, int value) {
		assert index < size;
		data[index] = value;
	}
	
	public void add(int value) {
		if ( size >= capacity ) {
			while (size >= capacity ) {
				capacity *= 2; 
			}
			setCapacity(capacity);
		}
		data[size++] = value;
	}
	
	public int[] toArray() {
		int[] result = new int[size];
		for ( int i = 0; i < size; ++i ) {
			result[i] = data[i];
		}
		return result;
	}
}
