package com.parking.parkingapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {

    public static String getData(RequestPackage p) {
        String uri = p.getUri();

        if (p.getMethod().equals("GET")) {
            uri += "?" + p.getEncodedParams();
        }

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(p.getMethod());

            if (p.getMethod().equals("POST")) {
                con.setDoOutput(true);
                OutputStream ops = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));

                writer.write(p.getEncodedParams());
                writer.flush();
                writer.close();
                ops.close();
            }

            StringBuilder sb = new StringBuilder();
            InputStream ips = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            reader.close();
            ips.close();
            con.disconnect();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}