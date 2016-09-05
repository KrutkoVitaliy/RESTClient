package appcorp.mmb.loaders;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;

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

import appcorp.mmb.dto.HairstyleDTO;
import appcorp.mmb.fragment_adapters.HairstyleFeedFragmentAdapter;
import appcorp.mmb.fragment_adapters.SearchHairstyleFeedFragmentAdapter;

public class SearchHairstyleFeedLoader extends AsyncTask<Void, Void, String> {

    HttpURLConnection urlFeedConnection = null;
    BufferedReader reader = null;
    String resultJsonFeed = "", output = "";
    int position;
    private String request, hairstyleLength, hairstyleType, hairstyleFor;
    private SearchHairstyleFeedFragmentAdapter adapter;
    private Toolbar toolbar;
    ProgressDialog progressDialog;

    public SearchHairstyleFeedLoader(Toolbar toolbar, SearchHairstyleFeedFragmentAdapter adapter, String request, String hairstyleLength, String hairstyleType, String hairstyleFor, int position, ProgressDialog progressDialog) {
        this.toolbar = toolbar;
        this.adapter = adapter;
        this.request = request;
        this.hairstyleFor = hairstyleFor;
        this.position = position;
        this.progressDialog = progressDialog;

        if (hairstyleLength.equals("0"))
            this.hairstyleLength = "";
        else if (hairstyleLength.equals("1"))
            this.hairstyleLength = "short";
        else if (hairstyleLength.equals("2"))
            this.hairstyleLength = "medium";
        else if (hairstyleLength.equals("3"))
            this.hairstyleLength = "long";

        if (hairstyleType.equals("0"))
            this.hairstyleType = "";
        else if (hairstyleType.equals("1"))
            this.hairstyleType = "straight";
        else if (hairstyleType.equals("2"))
            this.hairstyleType = "braid";
        else if (hairstyleType.equals("3"))
            this.hairstyleType = "tail";
        else if (hairstyleType.equals("4"))
            this.hairstyleType = "bunch";
        else if (hairstyleType.equals("5"))
            this.hairstyleType = "netting";
        else if (hairstyleType.equals("6"))
            this.hairstyleType = "curls";
        else if (hairstyleType.equals("7"))
            this.hairstyleType = "custom";

        if (hairstyleFor.equals("0"))
            this.hairstyleFor = "";
        else if (hairstyleFor.equals("1"))
            this.hairstyleFor = "kids";
        else if (hairstyleFor.equals("2"))
            this.hairstyleFor = "everyday";
        else if (hairstyleFor.equals("3"))
            this.hairstyleFor = "wedding";
        else if (hairstyleFor.equals("4"))
            this.hairstyleFor = "evening";
        else if (hairstyleFor.equals("5"))
            this.hairstyleFor = "exclusive";
    }

    public SearchHairstyleFeedLoader(Toolbar toolbar, SearchHairstyleFeedFragmentAdapter adapter, String request, String hairstyleLength, String hairstyleType, String hairstyleFor, int position) {
        this.toolbar = toolbar;
        this.adapter = adapter;
        this.request = request;
        this.hairstyleFor = hairstyleFor;
        this.position = position;

        if (hairstyleLength.equals("0"))
            this.hairstyleLength = "";
        else if (hairstyleLength.equals("1"))
            this.hairstyleLength = "short";
        else if (hairstyleLength.equals("2"))
            this.hairstyleLength = "medium";
        else if (hairstyleLength.equals("3"))
            this.hairstyleLength = "long";

        if (hairstyleType.equals("0"))
            this.hairstyleType = "";
        else if (hairstyleType.equals("1"))
            this.hairstyleType = "straight";
        else if (hairstyleType.equals("2"))
            this.hairstyleType = "braid";
        else if (hairstyleType.equals("3"))
            this.hairstyleType = "tail";
        else if (hairstyleType.equals("4"))
            this.hairstyleType = "bunch";
        else if (hairstyleType.equals("5"))
            this.hairstyleType = "netting";
        else if (hairstyleType.equals("6"))
            this.hairstyleType = "curls";
        else if (hairstyleType.equals("7"))
            this.hairstyleType = "custom";

        if (hairstyleFor.equals("0"))
            this.hairstyleFor = "";
        else if (hairstyleFor.equals("1"))
            this.hairstyleFor = "kids";
        else if (hairstyleFor.equals("2"))
            this.hairstyleFor = "everyday";
        else if (hairstyleFor.equals("3"))
            this.hairstyleFor = "wedding";
        else if (hairstyleFor.equals("4"))
            this.hairstyleFor = "evening";
        else if (hairstyleFor.equals("5"))
            this.hairstyleFor = "exclusive";
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (position == 1) {
                URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + request + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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
                    URL feedURL = new URL("http://195.88.209.17/search/hairstyle.php?request=" + request + "&hairstyle_length=" + hairstyleLength + "&hairstyle_type=" + hairstyleType + "&hairstyle_for=" + hairstyleFor + "&position=" + position);
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
                    resultJsonFeed = resultJsonFeed.replace("][", "");
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

                String[] tempTags = item.getString("tags").split(",");
                for (int j = 0; j < tempTags.length; j++) {
                    hashTags.add(tempTags[j]);
                }

                if (item.getString("published").equals("t")) {
                    if (!this.request.isEmpty())
                        toolbar.setTitle("#" + this.request + " - " + item.getString("count"));
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
                adapter.setData(data);
                progressDialog.hide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String upperCaseFirst(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}