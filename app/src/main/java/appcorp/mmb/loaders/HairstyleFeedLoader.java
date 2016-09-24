package appcorp.mmb.loaders;

import android.app.ProgressDialog;
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

import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.fragment_adapters.HairstyleFeedFragmentAdapter;

public class HairstyleFeedLoader extends AsyncTask<Void, Void, String> {

    HttpURLConnection urlFeedConnection = null;
    BufferedReader reader = null;
    String resultJsonFeed = "";
    HairstyleFeedFragmentAdapter adapter;
    int position;
    ProgressDialog progressDialog;

    public HairstyleFeedLoader(HairstyleFeedFragmentAdapter adapter, int position, ProgressDialog progressDialog) {
        this.adapter = adapter;
        this.position = position;
        this.progressDialog = progressDialog;
    }

    public HairstyleFeedLoader(HairstyleFeedFragmentAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/app/static/hairstyle" + position + ".html");
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
                    URL feedURL = new URL("http://195.88.209.17/app/static/hairstyle" + i + ".html");
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

        List<HairstyleDTO> data = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(resultJsonFeed);
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                List<String> images = new ArrayList<>();
                List<String> hashTags = new ArrayList<>();

                for (int j = 0; j < 10; j++)
                    if (!item.getString("screen" + j).equals("empty.jpg"))
                        images.add(item.getString("screen" + j));

                String[] tempTags;
                if (Storage.getString("Localization", "").equals("English")) {
                    tempTags = item.getString("tags").split(",");
                    for (int j = 0; j < tempTags.length; j++) {
                        hashTags.add(tempTags[j]);
                    }
                } else if (Storage.getString("Localization", "").equals("Russian")) {
                    tempTags = item.getString("tagsRu").split(",");
                    for (int j = 0; j < tempTags.length; j++) {
                        hashTags.add(tempTags[j]);
                    }
                }

                if (item.getString("published").equals("t")) {
                    HairstyleDTO hairstyleDTO = new HairstyleDTO(
                            item.getLong("id"),
                            item.getString("uploadDate"),
                            item.getString("authorName"),
                            item.getString("authorPhoto"),
                            item.getString("hairstyleType"),
                            images,
                            hashTags,
                            item.getLong("likes"),
                            item.getString("length"),
                            item.getString("type"),
                            item.getString("for"));
                    data.add(hairstyleDTO);
                }
            }
            if (adapter != null)
                adapter.setData(data);
            if (progressDialog != null)
                progressDialog.hide();

            FireAnal.sendString("1", "Open", "HairstyleFeedLoaded");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String upperCaseFirst(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}