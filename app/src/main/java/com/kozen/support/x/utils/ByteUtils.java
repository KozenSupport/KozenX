package com.kozen.support.x.utils;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

public class ByteUtils {
    private static final String TAG = "PosUtils";

    private static final char[] CHARS_TABLES = "0123456789ABCDEF".toCharArray();

    private static final byte[] BYTES = new byte[128];

    static {
        for (int i = 0; i < 10; i++) {
            BYTES['0' + i] = (byte) i;
            BYTES['A' + i] = (byte) (10 + i);
            BYTES['a' + i] = (byte) (10 + i);
        }
    }

    public static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 10);

        throw new RuntimeException ("invalid hex char '" + c + "'");
    }

    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if (bytes == null || bytes.length == 0 || offset < 0 || dateLen <= 0) {
            return null;
        }
        if (offset >= bytes.length || (bytes.length - offset) < dateLen) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);

        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
        }

        return asciiStr;
    }

    public static String bytesToAscii(byte[] bytes, int dateLen) {
        return bytesToAscii(bytes, 0, dateLen);
    }

    public static String bytesToAscii(byte[] bytes) {
        return bytesToAscii(bytes, 0, bytes.length);
    }

    public static String bytesToHexString(byte[] bytes, int offset, int len) {
        if (bytes == null) return "null!";

        int b;
        StringBuilder ret = new StringBuilder(2 * len);

        for (int i = 0 ; i < len ; i++) {
            b = 0x0f & (bytes[offset + i] >> 4);
            ret.append("0123456789abcdef".charAt(b));
            b = 0x0f & bytes[offset + i];
            ret.append("0123456789abcdef".charAt(b));
        }

        return ret.toString();
    }

    public static String bytesToHexString(byte[] bytes, int len) {
        return (bytes == null ? "null!" : bytesToHexString(bytes, 0, len));
    }

    public static String bytesToHexString(byte[] bytes) {
        return (bytes == null ? "null!" : bytesToHexString(bytes, bytes.length));
    }

    public static byte[] hexStringToBytes(String s) {
        if (s == null) return null;

        byte[] ret;
        int sz = s.length();

        try {
            ret = new byte[sz/2];
            for (int i=0 ; i <sz ; i+=2) {
                ret[i/2] = (byte) ((hexCharToInt(s.charAt(i)) << 4)
                                    | hexCharToInt(s.charAt(i+1)));
            }
            return ret;
        } catch (RuntimeException re) {
        }
        return null;
    }

    // here short is 2 bytes
    public static byte[] shortToBytesLe(short shortValue) {
        byte[] bytes = new byte[2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)((shortValue >> i*8) & 0xff);
        }

        return bytes;
    }

    // here short is 2 bytes
    public static byte[] shortToBytesBe(short shortValue) {
        byte[] bytes = new byte[2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[bytes.length - i - 1] = (byte)((shortValue >> i*8) & 0xff);
        }

        return bytes;
    }

    public static byte[] intToBytesLe(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)((intValue >> i*8) & 0xff);
        }

        return bytes;
    }

    public static byte[] intToBytesBe(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; i++) {
            bytes[bytes.length - i - 1] = (byte)((intValue >> i*8) & 0xff);
        }

        return bytes;
    }

    public static int bytesToIntLe(byte[] bytes) {
        if (bytes == null || bytes.length > 4) {
            throw new RuntimeException("invalid arg");
        }

        int ret = 0;

        for (int i = 0; i < bytes.length; i++) {
            ret += ((bytes[i] & 0xff) << (i * 8));
        }

        return ret;
    }

    public static int bytesToIntLe(byte[] data, int start, int end) {
        return bytesToIntLe(Arrays.copyOfRange(data, start, end));
    }

    public static int bytesToIntBe(byte[] bytes) {
        if (bytes == null || bytes.length > 4) {
            throw new RuntimeException("invalid arg");
        }

        int ret = 0;

        for (int i = 0; i < bytes.length; i++) {
            ret += ((bytes[i] & 0xff) << ((bytes.length - i - 1) * 8));
        }

        return ret;
    }

    public static int bytesToIntBe(byte[] data, int start, int end) {
        return bytesToIntBe(Arrays.copyOfRange(data, start, end));
    }

    public static int bytesToIntLe(byte b0, byte b1, byte b2, byte b3) {
        int ret = 0;

        ret = (b0 & 0xff);
        ret += ((b1 & 0xff) << 8);
        ret += ((b2 & 0xff) << 16);
        ret += ((b3 & 0xff) << 24);

        return ret;
    }

    public static int bytesToIntBe(byte b0, byte b1, byte b2, byte b3) {
        int ret = 0;

        ret = ((b0 & 0xff) << 24);
        ret += ((b1 & 0xff) << 16);
        ret += ((b2 & 0xff) << 8);
        ret += (b3 & 0xff);

        return ret;
    }

    public static short bytesToShortLe(byte[] bytes) {
        if (bytes == null || bytes.length > 2) {
            throw new RuntimeException("invalid arg");
        }

        short ret = 0;
        for (int i = 0; i < bytes.length; i++) {
            ret += ((bytes[i] & 0xff) << (i * 8));
        }

        return ret;
    }

    public static short bytesToShortLe(byte[] data, int start, int end) {
        return bytesToShortLe(Arrays.copyOfRange(data, start, end));
    }

    public static short bytesToShortBe(byte[] bytes) {
        if (bytes == null || bytes.length > 2) {
            throw new RuntimeException("invalid arg");
        }

        short ret = 0;

        for (int i = 0; i < bytes.length; i++) {
            ret += ((bytes[i] & 0xff) << ((bytes.length - i - 1) * 8));
        }

        return ret;
    }

    public static short bytesToShortBe(byte[] data, int start, int end) {
        return bytesToShortBe(Arrays.copyOfRange(data, start, end));
    }

    public static short bytesToShortLe(byte b0, byte b1) {
        short ret = 0;

        ret = (short)(b0 & 0xff);
        ret += (short)((b1 & 0xff) << 8);
        return ret;
    }

    public static short bytesToShortBe(byte b0, byte b1) {
        short ret = 0;

        ret = (short)((b0 & 0xff) << 8);
        ret += (short)(b1 & 0xff);
        return ret;
    }

    public static void byteArraySetByte(byte[] bytesArray, byte setValue, int index) {
        bytesArray[index] = setValue;
    }

    public static void byteArraySetByte(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte)(setValue & 0xFF);
    }

    public static void byteArraySetBytes(byte[] bytesArray, byte[] setValues, int index) {
        System.arraycopy(setValues, 0, bytesArray, index, setValues.length);
    }

    // here word is 2 bytes
    public static void byteArraySetWord(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte)(setValue & 0xFF);
        bytesArray[index + 1] = (byte)((setValue >> 8) & 0xFF);
    }

    // here word is 2 bytes
    public static void byteArraySetWordBe(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte)((setValue >> 8) & 0xFF);
        bytesArray[index + 1] = (byte)(setValue & 0xFF);
    }

    public static void byteArraySetInt(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte)(setValue & 0xFF);
        bytesArray[index + 1] = (byte)((setValue >> 8) & 0xFF);
        bytesArray[index + 2] = (byte)((setValue >> 16) & 0xFF);
        bytesArray[index + 3] = (byte)((setValue >> 24) & 0xFF);
    }

    public static void byteArraySetIntBe(byte[] bytesArray, int setValue, int index) {
        bytesArray[index] = (byte)((setValue >> 24) & 0xFF);
        bytesArray[index + 1] = (byte)((setValue >> 16) & 0xFF);
        bytesArray[index + 2] = (byte)((setValue >> 8) & 0xFF);
        bytesArray[index + 3] = (byte)(setValue & 0xFF);
    }

    public static void delayms(int ms) {
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
            }
        }
    }

    public static boolean isAscii(char ch) {
        if (ch <= (char)127) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAscii(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!isAscii(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasAsciiChar(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (isAscii(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDigitOrEnCharacter(byte c) {
        if ((c >= '0' && c <= '9')
            || (c >= 'A' && c <= 'Z')
            || (c >= 'a' && c <= 'z')) {
            return true;
        }

        return false;
    }

    public static String bcdToDecString(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    public static byte[] decStringToBcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static byte[] stringToBcd(String src) {
        return stringToBcd(src, ((src != null) ? src.length() : 0));
    }

    public static byte[] stringToBcd(String src, int numlen) {
        if (numlen % 2 != 0) {
            numlen++;
        }

        while (src.length() < numlen) {
            src = "0" + src;
        }

        byte[] bStr = new byte[src.length() / 2];
        char[] cs = src.toCharArray();
        int i = 0;
        int iNum = 0;

        for (i = 0; i < cs.length; i += 2) {
            int iTemp = 0;
            if (cs[i] >= '0' && cs[i] <= '9') {
                iTemp = (cs[i] - '0') << 4;
            } else {
                if (cs[i] >= 'a' && cs[i] <= 'f') {
                    cs[i] -= 32;
                }

                iTemp = (cs[i] - '0' - 7) << 4;
            }

            if (cs[i + 1] >= '0' && cs[i + 1] <= '9') {
                iTemp += cs[i + 1] - '0';
            } else {
                if (cs[i + 1] >= 'a' && cs[i + 1] <= 'f') {
                    cs[i + 1] -= 32;
                }

                iTemp += cs[i + 1] - '0' - 7;
            }

            bStr[iNum] = (byte) iTemp;
            iNum++;
        }

        return bStr;
    }

    public static String bcdToString(byte[] bcdNum) {
        return bcdToString(bcdNum, 0, ((bcdNum != null) ? bcdNum.length : 0));
    }

    public static String bcdToString(byte[] bcdNum, int offset, int len) {
        if (len <= 0 || offset < 0 || bcdNum == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(Integer.toHexString((bcdNum[i + offset] & 0xf0) >> 4));
            sb.append(Integer.toHexString(bcdNum[i + offset] & 0xf));
        }

        return sb.toString();
    }

    public static String toHexString(byte[] aBytes) {
        return toHexString(aBytes, 0, aBytes.length);
    }

    public static String toHexString(byte[] aBytes, int aLength) {
        return toHexString(aBytes, 0, aLength);
    }

    public static String toHexString(byte[] aBytes, int aOffset, int aLength) {
        char[] dst = new char[aLength * 2];

        for (int si = aOffset, di = 0; si < aOffset + aLength; si++) {
            byte b = aBytes[si];
            dst[di++] = CHARS_TABLES[(b & 0xf0) >>> 4];
            dst[di++] = CHARS_TABLES[(b & 0x0f)];
        }
        return new String(dst);
    }

    public static byte[] parseHex(String aHexString) {
        char[] src = aHexString.replace("\n", "").replace(" ", "").toUpperCase().toCharArray();
        byte[] dst = new byte[src.length / 2];

        for (int si = 0, di = 0; di < dst.length; di++) {
            byte high = BYTES[src[si++] & 0x7f];
            byte low = BYTES[src[si++] & 0x7f];
            dst[di] = (byte) ((high << 4) + low);
        }
        return dst;
    }

    public static Bundle jsonString2Bundle(String json) {
        Bundle bundle = null;
        try {
            if (!TextUtils.isEmpty(json)) {
                JSONObject jsonObj = new JSONObject(json);
                for (Iterator iter = jsonObj.keys(); iter.hasNext();) {
                    if (bundle == null) {
                        bundle = new Bundle();
                    }

                    String key = (String)iter.next();
                    Object value = jsonObj.get(key);
                    if (value instanceof Integer) {
                        bundle.putInt(key, ((Integer)value).intValue());
                    } else if (value instanceof Long) {
                        bundle.putLong(key, ((Long)value).longValue());
                    } else if (value instanceof Float) {
                        bundle.putFloat(key, ((Float)value).floatValue());
                    } else if (value instanceof Double) {
                        bundle.putDouble(key, ((Double)value).doubleValue());
                    } else if (value instanceof String) {
                        bundle.putString(key, ((String)value));
                    } else if (value instanceof Boolean) {
                        bundle.putBoolean(key, ((Boolean)value).booleanValue());
                    } else {
                        Log.e(TAG, "jsonString2Bundle:: not support object type key= " + key + ", value= " + value);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bundle;
    }
}

