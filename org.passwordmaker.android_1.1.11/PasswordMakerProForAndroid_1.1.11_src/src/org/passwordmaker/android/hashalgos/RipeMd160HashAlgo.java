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

package org.passwordmaker.android.hashalgos;

import java.io.UnsupportedEncodingException;

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PwmHashAlgorithm.UnderliningNormalHashAlgo;
import org.passwordmaker.android.hashalgos.thirdparty.RipeMd160;

public class RipeMd160HashAlgo  extends UnderliningNormalHashAlgo {

	public int digestLength() {
		return RipeMd160.DIGEST_SIZE;
	}

	@Override
	public byte[] hashText(String text) {
		try {
			return hashText(text.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public HashAlgo getAlgo() {
		return HashAlgo.RIPEMD_160;
	}
	
	public int blockSize() {
		return 64;
	}

	@Override
	public byte[] hashText(byte[] text) {
		RipeMd160 digest = new RipeMd160();
		digest.update(text);
		return digest.digest();
	}
}
