package com.example.restful.api;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class APIServer {
    public enum TypeContent { json, form_url }
    private static CookieManager cookieManager;
    private static HttpURLConnection conn;
    private static volatile String itog_response;

    private APIServer() {}
    static {
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    public static String patchToServer(String targetURL, TypeContent type, String query) {
        return appendToServer(targetURL, type, query, "PATCH");
    }

    public static String postToServer( final String targetURL,
                                       final TypeContent type, final String query) {
        return appendToServer(targetURL, type, query, "POST");
    }
    public static String getFromServer(final String targetURL) {
        itog_response = " ";

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(targetURL);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestProperty("Content-Type", convertTypeContent(TypeContent.form_url));
                    conn.setDoInput(true);
                    conn.connect();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String itog = "";
                    while ((itog = in.readLine()) != null)
                        itog_response = itog;
                    in.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return itog_response;
    }

    private static String appendToServer(final String targetURL, final TypeContent type,
                                         final String query, final String method) {
        itog_response = " ";

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(targetURL);
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestProperty("Content-Type", convertTypeContent(type));
                    conn.setRequestMethod(method);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();

                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(query);
                    out.flush();
                    out.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String itog = "";
                    while ((itog = in.readLine()) != null)
                        itog_response = itog;
                    in.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return itog_response;
    }


    private static String convertTypeContent(TypeContent typeContent) {
        if (typeContent == TypeContent.json)
            return "application/json";
        else /*if (typeContent == TypeContent.form_url) */ //standart
            return "application/x-www-form-urlencoded";
    }

    public static boolean itsOk(String str) {
        if (str.equals("Ok"))
            return true;
        else
            return false;
    }
}
