package com.company.crypto.padding;

public class PKCS7 {
    public static void doPadding(byte[] array) {

    }

    public static int doDepadding(byte[] decoded) {
        int position;
        for (position = 0; position < decoded.length; position++) {
            if (decoded[position] == 0) {
                break;
            }
        }
        return position;
    }
}
