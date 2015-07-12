package com.knowmiles.www.driverapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ServerConnection {
    private Map<String,String> dataToSend;
    private String connectionAddress;

    public ServerConnection(String connectionAddress) {
        dataToSend = new HashMap<>();
        this.connectionAddress = connectionAddress;
    }

    public void putPair(String key, String value) {
        dataToSend.put(key,value);
    }

    public void clearData() {
        dataToSend.clear();
    }

    public String execute() {
        String result = "";

        //Encoded String
        String encodedStr = getEncodedData(dataToSend);

        //Connection Handling
        BufferedReader reader = null;

        try {
            URL url = new URL(connectionAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //Post Method
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(encodedStr);
            writer.flush();

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //Encoder Method
    private String getEncodedData(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for(String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(sb.length()>0)
                sb.append("&");

            sb.append(key + "=" + value);
        }
        return sb.toString();
    }
}

