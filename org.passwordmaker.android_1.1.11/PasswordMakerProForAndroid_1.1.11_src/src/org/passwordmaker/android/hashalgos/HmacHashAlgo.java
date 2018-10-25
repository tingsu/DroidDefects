package org.passwordmaker.android.hashalgos;

import java.io.UnsupportedEncodingException;

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PwmHashAlgorithm.UnderliningHashAlgo;
import org.passwordmaker.android.PwmHashAlgorithm.UnderliningNormalHashAlgo;

public class HmacHashAlgo implements UnderliningHashAlgo {
	private UnderliningNormalHashAlgo underliningHash;
	private HashAlgo hashAlgo;

	public HmacHashAlgo(HashAlgo hashAlgo,
			UnderliningNormalHashAlgo underliningHash) {
		this.hashAlgo = hashAlgo;
		this.underliningHash = underliningHash;
	}

	@Override
	public int digestLength() {
		return this.underliningHash.digestLength();
	}

	@Override
	public int blockSize() {
		// TODO Auto-generated method stub
		return this.underliningHash.blockSize();
	}

	@Override
	public HashAlgo getAlgo() {
		return hashAlgo;
	}

	@Override
	public byte[] getHashBlob(String key, String text) {
		byte[] keyBytes = new byte[64];
		try {
			byte[] tmp = key.getBytes("UTF8");
			System.arraycopy(tmp, 0, keyBytes, 0, tmp.length);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		// Here is a bug this should compare tmp above not keyBytes as its not dependent on size... above will crap out if this condition would have held true
		if (keyBytes.length > blockSize()) {
			keyBytes = underliningHash.hashText(keyBytes);
		}

		byte[] kIpad = new byte[64];
		byte[] kOpad = new byte[64];
		// XOR key with ipad and opad values
		for (int i = 0; i < 64; i++) {
			kIpad[i] = (byte) (keyBytes[i] ^ (byte) 0x36);
			kOpad[i] = (byte) (keyBytes[i] ^ (byte) 0x5c);
		}
		byte[] innerBytes ;
		try {
			byte[] tmp = text.getBytes("UTF8");
			innerBytes = new byte[tmp.length + kIpad.length];
			System.arraycopy(kIpad, 0, innerBytes, 0, kIpad.length);
			System.arraycopy(tmp, 0, innerBytes, kIpad.length, tmp.length);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		byte[] innerHash = underliningHash.hashText(innerBytes);
		innerBytes = new byte[kOpad.length + innerHash.length];
		System.arraycopy(kOpad, 0, innerBytes, 0, kOpad.length);
		System.arraycopy(innerHash, 0, innerBytes, kOpad.length, innerHash.length);		
		return underliningHash.hashText(innerBytes);
	}

}
