/**
* This file licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.passwordmaker.android.hashalgos.thirdparty;

public class RipeMd160 {

	private static final int[][] ArgTable = {
			{ 11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8, 7, 6, 8,
					13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12, 11, 13, 6,
					7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5, 11, 12, 14,
					15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12, 9, 15, 5, 11,
					6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6 },
			{ 8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6, 9, 13,
					15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11, 9, 7, 15,
					11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5, 15, 5, 8, 11,
					14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8, 8, 5, 12, 9, 12,
					5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11 } };

	private static final int[][] IndexTable = {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 7, 4, 13,
					1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8, 3, 10, 14, 4,
					9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12, 1, 9, 11, 10, 0, 8,
					12, 4, 13, 3, 7, 15, 14, 5, 6, 2, 4, 0, 5, 9, 7, 12, 2, 10,
					14, 1, 3, 8, 11, 6, 15, 13 },
			{ 5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12, 6, 11, 3,
					7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2, 15, 5, 1, 3, 7,
					14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13, 8, 6, 4, 1, 3, 11,
					15, 0, 5, 12, 2, 13, 9, 7, 10, 14, 12, 15, 10, 4, 1, 5, 8,
					7, 6, 2, 13, 14, 0, 3, 9, 11 } };

	public static final int DIGEST_SIZE = 20;
	
	private int[] digest_buffer;

	private int[] working;
	private int working_ptr;
	private int msglen;

	public RipeMd160() {
		digest_buffer = new int[5];
		digest_buffer[0] = 0x67452301;
		digest_buffer[1] = 0xefcdab89;
		digest_buffer[2] = 0x98badcfe;
		digest_buffer[3] = 0x10325476;
		digest_buffer[4] = 0xc3d2e1f0;
		working = new int[16];
		working_ptr = 0;
		msglen = 0;
	}

	public void reset() {
		digest_buffer = new int[5];
		digest_buffer[0] = 0x67452301;
		digest_buffer[1] = 0xefcdab89;
		digest_buffer[2] = 0x98badcfe;
		digest_buffer[3] = 0x10325476;
		digest_buffer[4] = 0xc3d2e1f0;
		working = new int[16];
		working_ptr = 0;
		msglen = 0;
	}

	private void compress(int[] X) {
		int index = 0;

		int a, b, c, d, e;
		int A, B, C, D, E;
		int temp, s;

		A = a = digest_buffer[0];
		B = b = digest_buffer[1];
		C = c = digest_buffer[2];
		D = d = digest_buffer[3];
		E = e = digest_buffer[4];

		for (; index < 16; index++) {
			// The 16 FF functions - round 1 */
			temp = a + (b ^ c ^ d) + X[IndexTable[0][index]];
			a = e;
			e = d;
			d = (c << 10) | (c >>> 22);
			c = b;
			s = ArgTable[0][index];
			b = ((temp << s) | (temp >>> (32 - s))) + a;

			// The 16 JJJ functions - parallel round 1 */
			temp = A + (B ^ (C | ~D)) + X[IndexTable[1][index]] + 0x50a28be6;
			A = E;
			E = D;
			D = (C << 10) | (C >>> 22);
			C = B;
			s = ArgTable[1][index];
			B = ((temp << s) | (temp >>> (32 - s))) + A;
		}

		for (; index < 32; index++) {
			// The 16 GG functions - round 2 */
			temp = a + ((b & c) | (~b & d)) + X[IndexTable[0][index]]
					+ 0x5a827999;
			a = e;
			e = d;
			d = (c << 10) | (c >>> 22);
			c = b;
			s = ArgTable[0][index];
			b = ((temp << s) | (temp >>> (32 - s))) + a;

			// The 16 III functions - parallel round 2 */
			temp = A + ((B & D) | (C & ~D)) + X[IndexTable[1][index]]
					+ 0x5c4dd124;
			A = E;
			E = D;
			D = (C << 10) | (C >>> 22);
			C = B;
			s = ArgTable[1][index];
			B = ((temp << s) | (temp >>> (32 - s))) + A;
		}

		for (; index < 48; index++) {
			// The 16 HH functions - round 3 */
			temp = a + ((b | ~c) ^ d) + X[IndexTable[0][index]] + 0x6ed9eba1;
			a = e;
			e = d;
			d = (c << 10) | (c >>> 22);
			c = b;
			s = ArgTable[0][index];
			b = ((temp << s) | (temp >>> (32 - s))) + a;

			// The 16 HHH functions - parallel round 3 */
			temp = A + ((B | ~C) ^ D) + X[IndexTable[1][index]] + 0x6d703ef3;
			A = E;
			E = D;
			D = (C << 10) | (C >>> 22);
			C = B;
			s = ArgTable[1][index];
			B = ((temp << s) | (temp >>> (32 - s))) + A;
		}

		for (; index < 64; index++) {
			// The 16 II functions - round 4 */
			temp = a + ((b & d) | (c & ~d)) + X[IndexTable[0][index]]
					+ 0x8f1bbcdc;
			a = e;
			e = d;
			d = (c << 10) | (c >>> 22);
			c = b;
			s = ArgTable[0][index];
			b = ((temp << s) | (temp >>> (32 - s))) + a;

			// The 16 GGG functions - parallel round 4 */
			temp = A + ((B & C) | (~B & D)) + X[IndexTable[1][index]]
					+ 0x7a6d76e9;
			A = E;
			E = D;
			D = (C << 10) | (C >>> 22);
			C = B;
			s = ArgTable[1][index];
			B = ((temp << s) | (temp >>> (32 - s))) + A;
		}

		for (; index < 80; index++) {
			// The 16 JJ functions - round 5 */
			temp = a + (b ^ (c | ~d)) + X[IndexTable[0][index]] + 0xa953fd4e;
			a = e;
			e = d;
			d = (c << 10) | (c >>> 22);
			c = b;
			s = ArgTable[0][index];
			b = ((temp << s) | (temp >>> (32 - s))) + a;

			// The 16 FFF functions - parallel round 5 */
			temp = A + (B ^ C ^ D) + X[IndexTable[1][index]];
			A = E;
			E = D;
			D = (C << 10) | (C >>> 22);
			C = B;
			s = ArgTable[1][index];
			B = ((temp << s) | (temp >>> (32 - s))) + A;
		}

		/* combine results */
		D += c + digest_buffer[1]; /* final result for MDbuf[0] */
		digest_buffer[1] = digest_buffer[2] + d + E;
		digest_buffer[2] = digest_buffer[3] + e + A;
		digest_buffer[3] = digest_buffer[4] + a + B;
		digest_buffer[4] = digest_buffer[0] + b + C;
		digest_buffer[0] = D;
	}

	private void MDfinish(int[] input, int lswlen, int mswlen) {
		/* append the bit m_n == 1 */
		input[(lswlen >> 2) & 15] ^= 1 << (((lswlen & 3) << 3) + 7);

		if ((lswlen & 63) > 55) {
			/* length goes to next block */
			compress(input);
			for (int i = 0; i < 14; i++)
				input[i] = 0;
		}

		/* append length in bits */
		input[14] = lswlen << 3;
		input[15] = (lswlen >> 29) | (mswlen << 3);
		compress(input);
	}

	public void update(byte input) {
		working[working_ptr >> 2] ^= ((int) input) << ((working_ptr & 3) << 3);
		working_ptr++;
		if (working_ptr == 64) {
			compress(working);
			for (int j = 0; j < 16; j++)
				working[j] = 0;
			working_ptr = 0;
		}
		msglen++;
	}

	public void update(byte[] input) {
		for (int i = 0; i < input.length; i++) {
			working[working_ptr >> 2] ^= ((int) input[i]) << ((working_ptr & 3) << 3);
			working_ptr++;
			if (working_ptr == 64) {
				compress(working);
				for (int j = 0; j < 16; j++)
					working[j] = 0;
				working_ptr = 0;
			}
		}
		msglen += input.length;
	}

	public void update(byte[] input, int offset, int len) {
		if (offset + len >= input.length) {
			for (int i = offset; i < input.length; i++) {
				working[working_ptr >> 2] ^= ((int) input[i]) << ((working_ptr & 3) << 3);
				working_ptr++;
				if (working_ptr == 64) {
					compress(working);
					for (int j = 0; j < 16; j++)
						working[j] = 0;
					working_ptr = 0;
				}
			}
			msglen += input.length - offset;
		} else {
			for (int i = offset; i < offset + len; i++) {
				working[working_ptr >> 2] ^= ((int) input[i]) << ((working_ptr & 3) << 3);
				working_ptr++;
				if (working_ptr == 64) {
					compress(working);
					for (int j = 0; j < 16; j++)
						working[j] = 0;
					working_ptr = 0;
				}
			}
			msglen += len;
		}
	}

	public byte[] digest() {
		MDfinish(working, msglen, 0);
		byte[] res = new byte[DIGEST_SIZE];
		for (int i = 0; i < DIGEST_SIZE; i++)
			res[i] = (byte) ((digest_buffer[i >> 2] >>> ((i & 3) << 3)) & 0x000000FF);
		return res;
	}
}