package com.vcat.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.util.UUID;

/**
 * Created by ylin on 2015/10/23.
 */
public class UuidUtils {
    public static String compressedUUID(UUID uuid) {
        byte[] byUuid = new byte[16];
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();
        long2bytes(most, byUuid, 0);
        long2bytes(least, byUuid, 8);
        String compressUUID = Base64.encodeBase64URLSafeString(byUuid);
        return compressUUID;
    }

    private static void long2bytes(long value, byte[] bytes, int offset) {
        for (int i = 7; i > -1; i--) {
            bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
        }
    }

    private static String addLine(String uuidString){
        return uuidString.substring(0,8)+"-"+uuidString.substring(8,12)
                +"-"+uuidString.substring(12,16)
                +"-"+uuidString.substring(16,20)+"-"+uuidString.substring(20);
    }

    public static String compress(String uuidString) {
        String tmp = uuidString;
        if(!uuidString.contains("-")){
            tmp = addLine(uuidString);
        }
        UUID uuid = UUID.fromString(tmp);
        return compressedUUID(uuid);
    }

    public static String uncompress(String compressedUuid) {
        if (compressedUuid.length() != 22) {
            throw new IllegalArgumentException("Invalid uuid!");
        }
        byte[] byUuid = Base64.decodeBase64(compressedUuid + "==");
        long most = bytes2long(byUuid, 0);
        long least = bytes2long(byUuid, 8);
        UUID uuid = new UUID(most, least);
        return uuid.toString().replaceAll("-", "");
    }

    public static String uncompressWithLine(String compressedUuid) {
        if (compressedUuid.length() != 22) {
            throw new IllegalArgumentException("Invalid uuid!");
        }
        byte[] byUuid = Base64.decodeBase64(compressedUuid + "==");
        long most = bytes2long(byUuid, 0);
        long least = bytes2long(byUuid, 8);
        UUID uuid = new UUID(most, least);
        return uuid.toString();
    }

    private static long bytes2long(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 7; i > -1; i--) {
            value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println(compress("1838efee893d3778b48043d6457767ea"));
        System.out.println(uncompress("GDjv7ok9N3i0gEPWRXdn6g"));
    }
}
