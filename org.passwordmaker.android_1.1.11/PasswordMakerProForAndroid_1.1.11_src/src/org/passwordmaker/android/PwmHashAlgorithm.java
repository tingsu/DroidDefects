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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.passwordmaker.android.hashalgos.HmacHashAlgo;
import org.passwordmaker.android.hashalgos.Md4HashAlgo;
import org.passwordmaker.android.hashalgos.Md5HashAlgo;
import org.passwordmaker.android.hashalgos.RipeMd160HashAlgo;
import org.passwordmaker.android.hashalgos.Sha1HashAlgo;
import org.passwordmaker.android.hashalgos.Sha256HashAlgo;

public class PwmHashAlgorithm {

	public static interface UnderliningHashAlgo {
		public byte[] getHashBlob(String key, String text);
		public int digestLength();
		public int blockSize();
		public HashAlgo getAlgo();
	}
	
	public static abstract class UnderliningNormalHashAlgo implements UnderliningHashAlgo {
		
		public abstract byte[] hashText(byte[] text);
		
		public byte[] hashText(String text) {
			try {
				return hashText(text.getBytes("UTF8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		
		
		public byte[] getHashBlob(String key, String text) {
			return hashText(key + text);
		}
	}
	
	
	private final transient UnderliningHashAlgo hasher;
	private final int digestLength;
	protected PwmHashAlgorithm(UnderliningHashAlgo hasher) {
		this.hasher = hasher;
		this.digestLength = hasher.digestLength();
	}
	public final String hash(String key, String text, String characterSet) {
		return rstr2any(hasher.getHashBlob(key, text), characterSet);
	}
	
	public HashAlgo getHashAlgo() {
		return hasher.getAlgo();
	}
	
	public static int[] convertUnsignedByteArray(byte[] input) {
		int[] result = new int[input.length];
		int pos = 0;
		for ( byte b : input) {
			result[pos++] = (int)b & 0xFF;
		}
		return result;
	}
	
	/**
	 * This converts an array of bytes from an digest into the characterSet passed in. 
	 * 
	 * The basics of this code came from the C CLI version of rstr2any from passwordmaker.org.
	 * 
	 * @param inputBytes - digest
	 * @param characterSet - The characters to use as the encoding.
	 * @return - the input within the string encoding defined by character set
	 */
	private final String rstr2any(byte[] inputBytes, String characterSet) {
		
		// Can't handle odd lengths for input correctly with this function
		assert digestLength % 2 != 0;
		// This is the easiest way I could think of to do the proper calculations as java doesn't
		// have unsigned base data types.  - JDS
		int[] input =  convertUnsignedByteArray(inputBytes);
		if (digestLength % 2 != 0) {
			// XXX - log error
			return "";
		}
		
		
		final double LOG_2 = Math.log((double)2);
		final int divisor = (int)characterSet.length() ;
		int dividend_length = (int)Math.ceil((double)digestLength/2);
		int[] dividend = new int[dividend_length];
		for (int i = 0; i < dividend_length; i++)
		{
			dividend[i] = (((int) input[i*2]) << 8) | ((int) input[i*2+1]);
		}

		int full_length = (int)Math.ceil((double)digestLength * 8
								/ (Math.log((double)characterSet.length()) / LOG_2));
		final int [] remainders = new int[full_length];

		// this block here calculates the offsets into the character set
		int remainders_count = 0; // for use with trimming zeros method
		while(dividend_length > 0)
		{
			int[] quotient = new int[dividend_length];
			int quotient_length = 0;
			int qCounter = 0;
			int x = 0;

			for(int i = 0; i < dividend_length; i++)
			{
				int q;
				x = (x << 16) + dividend[i];
				q = (int)Math.floor((double)x / divisor);
				x -= q * divisor;
				if (quotient_length > 0 || q > 0)
				{
					quotient[qCounter++] = q;
					quotient_length++;
				}
			}
			remainders[remainders_count++] = x;
			dividend_length = quotient_length;
			dividend = quotient;
		}
		full_length = remainders_count;
		// end block of calculate index into character set
		// now transcribes the indices into the string
		StringBuilder output = new StringBuilder(full_length);
		for(int index = full_length - 1; index >= 0; index--) {
			output.append(characterSet.charAt(remainders[index]));
		}
		// return the string
		return output.toString() ;
	}
	
	public static PwmHashAlgorithm get(HashAlgo algo) {
		return new PwmHashAlgorithm(getUnderliningHasher(algo));
	}
	
	public static UnderliningHashAlgo getUnderliningHasher(HashAlgo algo) {
		try {
			UnderliningNormalHashAlgo normalHash = null;
			switch (algo.getUnderlining()) {
			case MD5:
				normalHash = new Md5HashAlgo() ;
				break;
			case SHA_256:
				normalHash = new Sha256HashAlgo() ;
				break;
			case SHA_1:
				normalHash = new Sha1HashAlgo() ;
				break;
			case MD4:
				normalHash = new Md4HashAlgo() ;
				break;
			case RIPEMD_160:
				normalHash = new RipeMd160HashAlgo() ;
				break;
			}
			if ( ! algo.isHMac() ) {
				return normalHash ;
			} else {
				return new HmacHashAlgo(algo, normalHash);
			}
		} catch (NoSuchAlgorithmException e) {
			// I bet some android device does not support one of these
			// Algos
			throw new RuntimeException(e);
		}
	}
}
