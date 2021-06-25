package com.example.moviebrowser;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TmdbApi {
    private static final String tmdbApiKey = "API_KEY_GOES_HERE";

    public static String getTmdbApiKey() {
        return tmdbApiKey;
    }

    public static void getJSON(String url, final ReadDataHandler rdh) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String response = "";

                try {
                    URL link = new URL(strings[0]);
                    HttpsURLConnection con = (HttpsURLConnection)link.openConnection();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String row;
                    while ((row = br.readLine()) != null) {
                        response = response + row + "\n";
                    }
                    br.close();
                    con.disconnect();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String response) {
                rdh.setJson(response);
                rdh.sendEmptyMessage(0);
            }
        };
        task.execute(url);
    }

}
