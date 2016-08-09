package appcorp.mmb.network;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GetRequest extends AsyncTask<Void, Void, String> {
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String url = "";
    String result = "";

    public GetRequest(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL feedURL = new URL(url);
            connection = (HttpURLConnection) feedURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer profileBuffer = new StringBuffer();
            String profileLine;
            while ((profileLine = reader.readLine()) != null) {
                profileBuffer.append(profileLine);
            }
            result = profileBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}