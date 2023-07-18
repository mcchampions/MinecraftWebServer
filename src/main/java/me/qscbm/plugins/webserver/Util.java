package me.qscbm.plugins.webserver;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Util {
    public static String returnContentType(String suffix) {
        File file = WebServerPlugin.getContentTypesFile();
        String data = new String(readFile(file),StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(data);
        if (!jsonObject.keySet().contains(suffix)) {
            return "application/octet-stream";
        } else {
            return jsonObject.getString(suffix);
        }
    }

    public static byte[] readFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = fileInputStream.readAllBytes();
            fileInputStream.close();
            return data;
        } catch (IOException e) {
            return new byte[]{};
        }
    }

    private static String hexString="0123456789ABCDEF";

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length()/2);
        //将每2位16进制整数组装成一个字节
        for(int i=0;i<bytes.length();i+=2)
            baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
        return new String(baos.toByteArray());
    }
}
