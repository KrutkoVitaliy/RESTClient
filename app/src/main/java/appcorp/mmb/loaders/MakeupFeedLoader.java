package appcorp.mmb.loaders;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.dto.MakeupDTO;
import appcorp.mmb.dto.TapeDTO;
import appcorp.mmb.fragment_adapters.MakeupFeedFragmentAdapter;

public class MakeupFeedLoader extends AsyncTask<Void, Void, String> {

    HttpURLConnection urlFeedConnection = null;
    BufferedReader reader = null;
    String resultJsonFeed = "", output = "";
    MakeupFeedFragmentAdapter adapter;
    int position;
    private List<String> requestList = new ArrayList<>();
    private String request;

    public MakeupFeedLoader(MakeupFeedFragmentAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + position + ".html");
                urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                urlFeedConnection.setRequestMethod("GET");
                urlFeedConnection.connect();
                InputStream inputStream = urlFeedConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                resultJsonFeed += buffer.toString();
            } else {
                for (int i = 1; i <= position; i++) {
                    URL feedURL = new URL("http://195.88.209.17/app/static/makeup" + i + ".html");
                    urlFeedConnection = (HttpURLConnection) feedURL.openConnection();
                    urlFeedConnection.setRequestMethod("GET");
                    urlFeedConnection.connect();
                    InputStream inputStream = urlFeedConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line);
                    resultJsonFeed += buffer.toString();
                    resultJsonFeed = resultJsonFeed.replace("][", ",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJsonFeed;
    }

    @Override
    protected void onPostExecute(String resultJsonFeed) {
        super.onPostExecute(resultJsonFeed);

        List<MakeupDTO> data = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(resultJsonFeed);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                List<String> images = new ArrayList<>();
                List<String> hashTags = new ArrayList<>();

                for (int j = 0; j < 10; j++)
                    if (!item.getString("screen" + j).equals("empty.jpg"))
                        images.add(item.getString("screen" + j));

                String[] tempTags = item.getString("tags").replace(" ", "").split(",");
                for (int j = 0; j < tempTags.length; j++) {
                    hashTags.add(tempTags[j]);
                }

                if (item.getString("published").equals("t") && !images.isEmpty()) {
                    MakeupDTO makeupDTO = new MakeupDTO(
                            item.getLong("id"),
                            item.getString("uploadDate"),
                            item.getString("authorName"),
                            item.getString("authorPhoto"),
                            images,
                            item.getString("colors"),
                            item.getString("eyeColor"),
                            item.getString("occasion"),
                            item.getString("difficult"),
                            hashTags,
                            item.getLong("likes"));
                    data.add(makeupDTO);
                }
            }
            adapter.setData(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String upperCaseFirst(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}